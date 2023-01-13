package com.ecommerce.service.Implementor;

import com.ecommerce.dao.PromoDOMapper;
import com.ecommerce.dataobject.PromoDO;
import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.PromoService;
import com.ecommerce.service.UserService;
import com.ecommerce.service.model.ProductModel;
import com.ecommerce.service.model.PromoModel;
import com.ecommerce.service.model.UserModel;
import jakarta.annotation.Resource;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PromoServiceImplementor implements PromoService {

    @Resource
    private PromoDOMapper promoDOMapper;
    @Resource
    @Lazy
    private ProductService productService;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserService userService;


    //Get current or incoming promo events by product id
    @Override
    public PromoModel getPromoByProductId(Integer productId){

        //Multiple promo for single product? Resolve later
        PromoDO promoDO = promoDOMapper.selectByProductId(productId);

        if(promoDO == null){
            return null;
        }

        //Validate promo status
        return convertToModel(promoDO);
    }

    @Override
    public PromoModel createPromo(String promoName, BigDecimal promoPrice, DateTime promoData, Integer productId) {
        return null;
    }

    //Publish Promo Event to redis
    @Override
    public void publishPromo(Integer promoId) throws SystemException {

        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if(promoDO == null || promoDO.getProductid() == null || promoDO.getProductid() == 0){
            return;
        }

        ProductModel productModel = productService.getProduct(promoDO.getProductid());
        String key = "promoProductStock:" + productModel.getId();
        //Update Stock to redis
        redisTemplate.opsForValue().set(key,productModel.getStock());

        //Set promoToken control
        redisTemplate.opsForValue().set("promoTokenLimit:" + promoId ,productModel.getStock()*5 );

    }

    //Validate userModel, ProductModel, PromoModel and return tokenid
    @Override
    public String generateFlashSaleToken(Integer promoId,Integer productId, Integer userId) throws SystemException {

        //Check Sold out Token
        if(redisTemplate.hasKey("promoProductSellOut:"+ productId)){
            throw new SystemException(ErrorEnum.NOT_ENOUGH_STOCK);
        }

        //Validate product By Cache
        ProductModel currProduct = productService.getProductInCache(productId);
        if(currProduct == null){
            throw new SystemException(ErrorEnum.PRODUCT_NOT_EXIST);
        }

        //Validate user By Cache
        UserModel currUser = userService.getUserInCache(userId);
        if(currUser == null){
            throw new SystemException(ErrorEnum.USER_NOT_EXIST);
        }

        //Multiple promo for single product?
        PromoDO promoDO = promoDOMapper.selectByPrimaryKey(promoId);
        if(promoDO == null){
            throw new SystemException(ErrorEnum.NO_PROMO_EVENT);
        }
        PromoModel promoModel = convertToModel(promoDO);
        if(promoModel.getStatus() != 2 || !Objects.equals(promoModel.getProductId(), productId)){
            throw new SystemException(ErrorEnum.NO_PROMO_EVENT);
        }

        Integer promoTokenAmount = (Integer) redisTemplate.opsForValue().get("promoTokenLimit:" + promoId);
        if(promoTokenAmount == null){
            throw new SystemException(ErrorEnum.UNKNOWN_ERROR);
        }

        if(promoTokenAmount >= 1){

            redisTemplate.opsForValue().set("promoTokenLimit:" + promoId, promoTokenAmount-1);

            String token = UUID.randomUUID().toString().replace("","");
            redisTemplate.opsForValue().set("promoToken:" + promoId + ":" + userId + ":" + productId, token);
            redisTemplate.expire("promoToken:" + promoId , 5, TimeUnit.MINUTES);  // 5 minutes expire time

            return token;
        }else{
            throw new SystemException(ErrorEnum.NOT_ENOUGH_STOCK);
        }

    }

    @Override
    public void publishAvailablePromoEvent() throws SystemException {

        List<PromoDO>  promoDOList = promoDOMapper.getAllPromoEvent();

        for(int i = 0 ; i < promoDOList.size();i++){

            DateTime startTime = new DateTime(promoDOList.get(i).getStartdate());
            DateTime endTime = new DateTime(promoDOList.get(i).getEnddate());

            if(endTime.isAfterNow() && startTime.isBeforeNow()  ){
                this.publishPromo(promoDOList.get(i).getId());
            }
        }

    }


    private PromoModel convertToModel(PromoDO promoDO){

        if(promoDO == null){
            return null;
        }

        PromoModel promoModel = new PromoModel();

        promoModel.setId(promoDO.getId());
        promoModel.setPromoName(promoDO.getPromoname());
        promoModel.setPromoPrice(BigDecimal.valueOf(promoDO.getPromoprice()));
        promoModel.setProductId(promoDO.getProductid());
        promoModel.setStartDate(new DateTime(promoDO.getStartdate()));
        promoModel.setEndDate(new DateTime(promoDO.getEnddate()));

        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if(promoModel.getEndDate().isAfterNow() && promoModel.getStartDate().isBeforeNow()  ){
            promoModel.setStatus(2);
        }else{
            promoModel.setStatus(3);
        }

        return promoModel;
    }
}
