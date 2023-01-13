package com.ecommerce.service.Implementor;

import com.ecommerce.dao.OrderDOMapper;
import com.ecommerce.dao.SequenceDOMapper;
import com.ecommerce.dao.StockLogDOMapper;
import com.ecommerce.dataobject.OrderDO;
import com.ecommerce.dataobject.SequenceDO;
import com.ecommerce.dataobject.StockLogDO;
import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.PromoService;
import com.ecommerce.service.UserService;
import com.ecommerce.service.model.OrderModel;
import com.ecommerce.service.model.ProductModel;
import com.ecommerce.service.model.UserModel;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImplementer implements OrderService {

    @Resource
    private SequenceDOMapper sequenceDOMapper;
    @Resource
    @Lazy
    private ProductService productService;
    @Resource
    private UserService userService;
    @Resource
    private OrderDOMapper orderDOMapper;
    @Resource
    private StockLogDOMapper stockLogDOMapper;
    @Resource
    private PromoService promoService;
    @Resource
    RedisTemplate redisTemplate;


    /**
     * User place order
     * @param userId
     * @param productId
     * @param orderAmount
     * @return
     * @throws SystemException
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)  // Avoid duplicates. No rollBack
    @Override
    public OrderModel createOrder(Integer userId, Integer productId, Integer orderAmount, String stockLogId) throws SystemException {

        //Validate order status By Cache
        ProductModel currProduct = productService.getProductInCache(productId);
        if(currProduct == null){
            throw new SystemException(ErrorEnum.PRODUCT_NOT_EXIST);
        }

        //Validate User status By Cache
        UserModel currUser = userService.getUserInCache(userId);
        if(currUser == null){
            throw new SystemException(ErrorEnum.USER_NOT_EXIST);
        }

        //Validate order Amount
        if(orderAmount <= 0 || orderAmount >= 99){
            throw new SystemException(ErrorEnum.INVALID_ORDER_AMOUNT);
        }

        //Update Stock  1. Reduce stock by placement && Reduce stock by payment
        boolean result = productService.decreaseStock(productId,orderAmount);
        if(!result){
            throw new SystemException(ErrorEnum.NOT_ENOUGH_STOCK);
        }

        //Generate Order ID
        String orderId = generateOrderId();
        //Update to DataBase
        OrderModel orderModel = convertToOrderModel(productId, userId , orderAmount, currProduct.getPrice(), false,0);
        OrderDO orderDO = convertFromOrderModel(orderId, orderModel);
        orderDOMapper.insertSelective(orderDO);

        //Update sales.
        boolean updateSalesResult = productService.updateSales(productId,orderAmount);
        if(!updateSalesResult){
            throw new SystemException(ErrorEnum.PRODUCT_NOT_EXIST);
        }

        StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
        if(stockLogDO == null){
            throw new SystemException(ErrorEnum.UNKNOWN_ERROR);
        }
        stockLogDO.setStatus(1); // Successful
        stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO); // Update to database

        //Return to frontEnd
        return orderModel;
    }

    //Flash sell purchase
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)  // Avoid duplicates. No rollBack
    public OrderModel createFlashOrder(Integer userId, Integer productId, Integer orderAmount , Integer promoId, String stockLogId)
            throws SystemException, MQBrokerException, RemotingException, InterruptedException, MQClientException {

        //Validate order amount
        if(orderAmount <= 0 || orderAmount >= 99){
            throw new SystemException(ErrorEnum.INVALID_ORDER_AMOUNT);
        }

        //Validate currProduct
        ProductModel currProduct = productService.getProductInCache(productId);
        if(currProduct == null){
            throw new SystemException(ErrorEnum.PRODUCT_NOT_EXIST);
        }
//
//        //Validate user By Cache
//        UserModel currUser = userService.getUserInCache(userId);
//        if(currUser == null){
//            throw new SystemException(ErrorEnum.USER_NOT_EXIST);
//        }
//
//        //Validate promoModel
//        PromoModel promoModel = currProduct.getPromoModel();
//        if( promoModel == null || promoModel.getStatus() != 2){
//            throw new SystemException(ErrorEnum.NO_PROMO_EVENT);
//        }
        // The above parameters are verified at promoToken generation

        //Decrease stock in redis
        boolean result = productService.decreasePromoProductStock(productId,orderAmount,promoId);
        if(!result){
            throw new SystemException(ErrorEnum.NOT_ENOUGH_STOCK);
        }

        //If Order generation failed in mysql. Then rollback, However stock may already have benn reduced
        //Less sale.  Stock reduced , No order

        //Generate Order ID
        String orderId = generateOrderId();
        //Update order to DataBase
        OrderModel orderModel = convertToOrderModel(productId, userId , orderAmount , currProduct.getPromoModel().getPromoPrice(), true, promoId);
        OrderDO orderDO = convertFromOrderModel(orderId, orderModel);
        orderDOMapper.insertSelective(orderDO);

        //Update sales.
        productService.updateSales(productId,orderAmount);

        //ASynchronization update stock. Add to message queue
        boolean reduceStockResult = productService.asyncDecreaseStock(productId,orderAmount); // Add to queue successful
        if(!reduceStockResult){
            //Rollback stock in redis. If Message send failed
            productService.rollbackStockInRedis("promoProductStock:" + productId , orderAmount);
            throw new SystemException(ErrorEnum.ORDER_PLACE_FIALED);
        }

        //Update to Stock Log
        StockLogDO stockLogDO = stockLogDOMapper.selectByPrimaryKey(stockLogId);
        if(stockLogDO == null){
            throw new SystemException(ErrorEnum.UNKNOWN_ERROR);
        }
        stockLogDO.setStatus(1); // Successful
        stockLogDOMapper.updateByPrimaryKeySelective(stockLogDO); // Update to database

        //Return  orderModel
        return orderModel;
    }

    @Override
    public List<OrderDO> selectOrdersByUserId(Integer userId) {
        return orderDOMapper.selectOrdersByUserId(userId);
    }

    /**
     * Generate 16 digits order number
     * First 8 digits are date info //Year/Month/Date
     * Middle 6 digits are increasing sequence  -- Avoid duplicate
     * Last digits are mul-table/multi-database bits  -- 00-99 user for table divide
     */
    private String generateOrderId(){

        StringBuilder res = new StringBuilder();

        //First 8 digits date
        LocalDateTime now = LocalDateTime.now();
        String dateBits = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        res.append(dateBits);

        //Middle 6 bits, increasing sequence. Increase by step value, Update to database
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        int increasingSequence = sequenceDO.getCurrValue();
        sequenceDO.setCurrValue(sequenceDO.getCurrValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        //Append to 6 bits
        String middleBits = String.valueOf(increasingSequence);
        res.append("0".repeat(Math.max(0, 6 - middleBits.length())));
        res.append(middleBits);

        //Last 2 digits for paging
        res.append("00");

        return res.toString();
    }


    /**
     * \  Create orderModel. Dont set order ID yet
     */
    private OrderModel convertToOrderModel(Integer productId , Integer userId , Integer orderAmount, BigDecimal purchasePrice, Boolean isFlashSale, Integer promoId){
        OrderModel orderModel = new OrderModel();

        orderModel.setUserId(userId);
        orderModel.setProductId(productId);
        orderModel.setProductPrice(purchasePrice);
        orderModel.setOrderAmount(orderAmount);
        orderModel.setOrderPrice( new BigDecimal(orderAmount).multiply(orderModel.getProductPrice()) );
        orderModel.setPromoId(promoId);
        orderModel.setFlashOrder(isFlashSale);

        return orderModel;
    }

    //Convert ProductModel to ProductDO, Set orderID
    private OrderDO convertFromOrderModel(String orderId , OrderModel orderModel){

        if(orderModel == null){
            return null;
        }

        OrderDO orderDO = new OrderDO();

        orderDO.setOrderid(orderId);
        orderDO.setUserid(orderModel.getUserId());
        orderDO.setProductid(orderModel.getProductId());
        orderDO.setOrderamount(orderModel.getOrderAmount());

        //Flash Sale related
        if(orderModel.getFlashOrder()){
            //Is flash sale
            orderDO.setIsflashsale((byte) 1);
        }else{
            orderDO.setIsflashsale((byte) 0);
        }

        orderDO.setPromoid(orderModel.getPromoId());

        //Big Decimal to double
        orderDO.setOrderprice(orderModel.getOrderPrice().doubleValue());
        orderDO.setProductprice(orderModel.getProductPrice().doubleValue());

        return orderDO;
    }


}
