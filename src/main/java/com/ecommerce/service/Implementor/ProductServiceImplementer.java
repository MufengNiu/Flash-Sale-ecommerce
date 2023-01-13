package com.ecommerce.service.Implementor;

import com.ecommerce.dao.ProductDOMapper;
import com.ecommerce.dao.ProductStockDOMapper;
import com.ecommerce.dao.StockLogDOMapper;
import com.ecommerce.dataobject.ProductDO;
import com.ecommerce.dataobject.ProductStockDO;
import com.ecommerce.dataobject.StockLogDO;
import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;
import com.ecommerce.mq.MqProducer;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.PromoService;
import com.ecommerce.service.model.ProductModel;
import com.ecommerce.service.model.PromoModel;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Service
public class ProductServiceImplementer implements ProductService {

    @Resource
    private ProductDOMapper productDOMapper;
    @Resource
    private ProductStockDOMapper productStockDOMapper;
    @Resource
    private PromoService promoService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private MqProducer mqProducer;
    @Resource
    private StockLogDOMapper stockLogDOMapper;

    //Add product to Database
    @Override
    @Transactional
    public ProductModel addProduct(ProductModel productModel) throws SystemException{

        //Validate Input
        if(productModel == null){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        //Transfer to DAO
        ProductDO productDO = convertFromModel(productModel);

        //Write to database
        productDOMapper.insertSelective(productDO);
        productModel.setId(productDO.getId()); //Get id after insertion.

        ProductStockDO productStockDO = convertProductStockFromModel(productModel);
        productStockDOMapper.insertSelective(productStockDO);

        return this.getProduct(productModel.getId());
    }

    /**
     *  Get product Model. Check product id from product table, stock table and promo table
     */
    @Override
    public ProductModel getProduct(Integer product_id) throws SystemException {

        //Get product info from mysql
        ProductDO productDO = productDOMapper.selectByPrimaryKey(product_id);
        if(productDO == null){
            return null;
        }
        ProductStockDO productStockDO = productStockDOMapper.selectByProductId(productDO.getId());
        PromoModel promoModel = promoService.getPromoByProductId(productDO.getId());

        ProductModel productModel = convertToModel(productDO,productStockDO);
        //Incoming promo event
        if(promoModel != null){
            productModel.setPromoModel(promoModel);
        }

        return productModel;
    }

    //Get all product models as list, descending order by sales.
    //Dont set promo event
    @Override
    public List<ProductModel> getProductList() {

        List<ProductDO> productDoList = productDOMapper.selectProductList();

        List<ProductModel> productModelList;

        productModelList = productDoList.stream().map(productDO -> {
            ProductStockDO productStockDO = productStockDOMapper.selectByProductId(productDO.getId());
            try {
                return convertToModel(productDO,productStockDO);
            } catch (SystemException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        return productModelList;
    }

    //Decrease stock number stored in redis
    @Override
    public boolean decreasePromoProductStock(Integer productId , Integer orderAmount,Integer promoId) throws SystemException {

        //By Redis cache
        String key = "promoProductStock:" + productId;
        Integer originalStock = (Integer) redisTemplate.opsForValue().get(key);
        if(originalStock == null){
            throw new SystemException(ErrorEnum.NO_PUBLISHED_PROMO_EVENT);
        }

        //Not Enough stock in redis
        if(originalStock < orderAmount){
            return false;
        }else if(  originalStock == 0 ){
            redisTemplate.opsForValue().set("promoProductSellOut:"+ productId ,  true);
            return false;
        } else if( originalStock.equals(orderAmount) ){
            //Sell out. Add sign to redis
            redisTemplate.opsForValue().set("promoProductSellOut:"+ productId ,  true);
        }

        //Decrease stock in redis
        redisTemplate.opsForValue().set(key, originalStock-orderAmount);
        return true;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer productId , Integer orderAmount) {

      int affectedRow = productStockDOMapper.decreaseStock(productId,orderAmount);
      //Update failed
      return affectedRow != 0;
    }


    @Transactional
    public boolean updateSales(Integer productId , Integer amount) {

        ProductDO productDO = productDOMapper.selectByPrimaryKey(productId);

        if(productDO == null){
            return false;
        }

        productDO.setSales(productDO.getSales() + amount);
        productDOMapper.updateByPrimaryKeySelective(productDO);

        return true;
    }

    @Override
    public boolean asyncDecreaseStock(Integer productId, Integer orderAmount) {
        //Update Stock to database
        return mqProducer.asyncReduceStock(productId,orderAmount);
    }

    @Override
    public ProductModel getProductInCache(Integer productId) throws SystemException {

        String key = "productValidate:" + productId;
        ProductModel productModel;

        //Check redis
        productModel = (ProductModel) redisTemplate.opsForValue().get(key);
        if (productModel == null){
            //Add to cache
            productModel = getProduct(productId);
            redisTemplate.opsForValue().set(key,productModel);
            redisTemplate.expire(key,5, TimeUnit.MINUTES);
        }

        return productModel;
    }

    @Override
    public void rollbackStockInRedis(String key, Integer amount) {
        redisTemplate.opsForValue().set(key,  (Integer)redisTemplate.opsForValue().get(key) + amount);
    }

    /**
     *
     *  Initial stock, used before order placement,
     *
     *  Statue:
     *  0 : initial state
     *  1: order place success
     *  2: order place fail, roll back
     * @
     */
    @Override
    public String initialStockLog(Integer productId, Integer orderAmount, Boolean isFlashSale) {
        StockLogDO stockLogDO = new StockLogDO();

        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-",""));
        stockLogDO.setProductId(productId);
        stockLogDO.setOrderAmount(orderAmount);
        stockLogDO.setStatus(0);
        stockLogDO.setIsflashsale((byte) (isFlashSale?1:0));

        stockLogDOMapper.insertSelective(stockLogDO);

        return stockLogDO.getStockLogId();
    }



    private ProductModel convertToModel(ProductDO productDO , ProductStockDO productStockDO) throws SystemException {

        ProductModel productModel = new ProductModel();

        BeanUtils.copyProperties(productDO,productModel);
        productModel.setPrice(BigDecimal.valueOf(productDO.getPrice()));
        productModel.setStock(productStockDO.getStock());

        return productModel;
    }

    //Convert ProductModel to Product dao
    private ProductDO convertFromModel(ProductModel productModel){

        if(productModel == null){
            return null;
        }

        ProductDO productDO = new ProductDO();
        BeanUtils.copyProperties(productModel , productDO);

        //price not same price, Manually convert
        productDO.setPrice( productModel.getPrice().doubleValue() );
        return productDO;
    }

    private ProductStockDO convertProductStockFromModel(ProductModel productModel){

        if(productModel == null){
            return null;
        }

        ProductStockDO productStockDO = new ProductStockDO();

        productStockDO.setProductId(productModel.getId());
        productStockDO.setStock(productModel.getStock());

        return productStockDO;
    }

}