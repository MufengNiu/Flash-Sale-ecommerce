package com.ecommerce.service.model;


import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;


public class PromoModel implements Serializable {

    private Integer id;
    //Name of flash sale
    private String promoName;
    //Start time of flash sale
    private DateTime startDate;
    private DateTime endDate;

    //Promo sales status :: 1 for incoming , 2 for current , 3 for past
    private Integer status;
    private Integer productId;

    private BigDecimal promoPrice;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromoName() {
        return promoName;
    }

    public void setPromoName(String promoName) {
        this.promoName = promoName;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public BigDecimal getPromoPrice() {
        return promoPrice;
    }

    public void setPromoPrice(BigDecimal promoPrice) {
        this.promoPrice = promoPrice;
    }
}
