package com.ecommerce.service;

import com.ecommerce.error.SystemException;
import com.ecommerce.service.model.PromoModel;
import org.joda.time.DateTime;
import java.math.BigDecimal;
import java.util.List;

public interface PromoService {
    //Return promo Model
    PromoModel getPromoByProductId(Integer productId) throws SystemException;
    //Create Promo Event
    PromoModel createPromo(String promoName, BigDecimal promoPrice, DateTime promoData , Integer productId);
    //Publish prome Event, Add to redis
    void publishPromo(Integer promoId) throws SystemException;
    //Generate flash sale token
    String generateFlashSaleToken(Integer promoId , Integer productId , Integer userId) throws SystemException;

    void publishAvailablePromoEvent() throws SystemException;

}
