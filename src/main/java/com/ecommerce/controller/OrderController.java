package com.ecommerce.controller;


import com.ecommerce.controller.ViewObjects.OrderView;
import com.ecommerce.dataobject.OrderDO;
import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;
import com.ecommerce.mq.MqProducer;
import com.ecommerce.response.CommonReturnType;
import com.ecommerce.service.CacheService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.PromoService;
import com.ecommerce.service.model.ProductModel;
import com.ecommerce.service.model.UserModel;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.concurrent.*;


/**
 *  1.Pass promoId end from front-end , then check the correspondence of promoId to product and whether promo is strated
 *  2. Check in service level
 */


@Controller("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*" , originPatterns = "*")
public class OrderController extends BaseController{

    @Resource
    OrderService orderService;
    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ProductService productService;
    @Resource
    private CacheService cacheService;
    @Resource
    private MqProducer mqProducer;
    @Resource
    private PromoService promoService;
    @Resource
    private ExecutorService executorService;
    @Resource
    private RateLimiter flashSaleRateLimiter;


    @PostConstruct
    public void init(){
        executorService = Executors.newFixedThreadPool(20);
        flashSaleRateLimiter = RateLimiter.create(300);  //Permits per second .  10 tps/ Transactions per second
    }

    @RequestMapping("/")
    @ResponseBody
    public String orderPage(){
        return "Order Page";
    }

    @RequestMapping(value = "/getOrdersById", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getOrdersByUserId(@RequestParam(name = "userId")Integer userId){

        List<OrderDO> orderDOList = orderService.selectOrdersByUserId(userId);
        List<OrderView> orderViewList = orderDOList.stream().map(this::convertToView).toList();

        return CommonReturnType.create(orderViewList);
    }

    @RequestMapping(value = "/placeOrder", method = {RequestMethod.POST},consumes = {BaseController.CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType placeOrder(@RequestParam(name = "productId") Integer productId , @RequestParam(name = "orderAmount") Integer orderAmount) throws SystemException {

        //By Session
//        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
//        UserModel currUser = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        //By Token
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)){
            throw new SystemException(ErrorEnum.USER_NOT_LOGIN);
        }

        UserModel currUser = (UserModel) redisTemplate.opsForValue().get(token);
        if(currUser == null){
            throw new SystemException(ErrorEnum.USER_NOT_LOGIN);
        }

        //Stock initial stock log, Status: 0
        String stockLogId = productService.initialStockLog(productId,orderAmount, false);

        //Create Order. Amount 1.
        boolean orderResult = mqProducer.transactionalAsyncReduceStock(productId,orderAmount, currUser.getId(), null, stockLogId);
        if(!orderResult){
            throw new SystemException(ErrorEnum.ORDER_PLACE_FIALED);
        }

        //Update Cache
        updateCache(productId);
        //Return to front end
        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/placeFlashOrder", method = {RequestMethod.POST},consumes = {BaseController.CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType placeFlashOrder(@RequestParam(name = "productId") Integer productId ,
                                            @RequestParam(name = "orderAmount") Integer orderAmount,
                                            @RequestParam(name = "promoId")Integer promoId,
                                            @RequestParam(name = "promoToken")String promoToken) throws SystemException, MQBrokerException, RemotingException, InterruptedException, MQClientException {

        //Token Bucket. 300
        if(flashSaleRateLimiter.acquire() <= 0){
            throw new SystemException(ErrorEnum.ORDER_PLACE_FIALED);
        }

        //By Session
        //Obtain user login info
        //Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        //UserModel currUser = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        //By Token
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)){
            throw new SystemException(ErrorEnum.USER_NOT_LOGIN);
        }
        UserModel currUser = (UserModel) redisTemplate.opsForValue().get(token);
        if (currUser == null){
            throw new SystemException(ErrorEnum.USER_NOT_LOGIN);
        }

        //Check flash sale token
        String inRedisPromoToken = (String) redisTemplate.opsForValue().get("promoToken:" + promoId + ":" + currUser.getId() + ":" + productId);
        if(  inRedisPromoToken == null || !StringUtils.equals(inRedisPromoToken,promoToken) ){
            throw new SystemException(ErrorEnum.INVALID_PROMO_TOKEN);
        };


        //Only 20 placed request will be executed
        // Congestion window of size 20.
        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                //Add init state to stock_log table
                String stockLogId = productService.initialStockLog(productId,orderAmount,true);

                //Add to transaction MQ
                boolean orderResult = mqProducer.transactionalAsyncReduceStock(productId,orderAmount, currUser.getId(), promoId, stockLogId);
                if(!orderResult){
                    throw new SystemException(ErrorEnum.ORDER_PLACE_FIALED);
                }

                //Update front end Cache
                updateCache(productId);

                return null;
            }
        });
        /**
         * Cluster Design:
         *   Each server set queue to redis
         */


        try {
            future.get();
        } catch (ExecutionException e) {
            throw new SystemException(ErrorEnum.ORDER_PLACE_FIALED);
        }catch (InterruptedException e){
            e.printStackTrace();
            throw new SystemException(ErrorEnum.UNKNOWN_ERROR);
        }


        return CommonReturnType.create("Order Place Successful");
    }

    /**
     *  Generate flashsale token for user. Promo service must already started
     */
    @RequestMapping(value = "/generateFlashSaleToken" , method = {RequestMethod.POST}, consumes = {BaseController.CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getFlashToken( @RequestParam(name = "productId")Integer productId,
                                           @RequestParam(name = "promoId")Integer promoId) throws SystemException {

        //Check Login States in redis
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)){
            throw new SystemException(ErrorEnum.USER_NOT_LOGIN);
        }
        UserModel currUser = (UserModel) redisTemplate.opsForValue().get(token);
        if (currUser == null){
            throw new SystemException(ErrorEnum.USER_NOT_LOGIN);
        }

        //Get flash sale access token
        String flashSaleToken = (String) promoService.generateFlashSaleToken(promoId,productId, currUser.getId());
        if(flashSaleToken == null){
            throw new SystemException(ErrorEnum.UNKNOWN_ERROR);
        }

        return CommonReturnType.create(flashSaleToken);
    }


    private void updateCache(Integer productId) throws SystemException {

        ProductModel productModel = productService.getProduct(productId);
        String key = "product:" + productId;
        redisTemplate.opsForValue().set(key,productModel);
        cacheService.setCommonCache(key,productModel);

        List<ProductModel> productList = productService.getProductList();
        key = "productList";
        redisTemplate.opsForValue().set(key,productList);
        cacheService.setCommonCache(key,productList);
    }


    private OrderView convertToView(OrderDO orderDO){
        OrderView orderView = new OrderView();

        orderView.setId(orderDO.getOrderid());
        orderView.setProductId(orderDO.getProductid());

        orderView.setOrderAmount(orderDO.getOrderamount());
        orderView.setProductPrice(orderDO.getProductprice());
        orderView.setOrderPrice(orderDO.getOrderprice());

        orderView.setIsFlashSale(orderDO.getIsflashsale());

        return orderView;
    }


}
