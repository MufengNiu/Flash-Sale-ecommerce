package com.ecommerce.service.model;

import java.math.BigDecimal;

//User Model
public class OrderModel {
    private String orderId;
    private Integer userId;
    private Integer productId;
    //Amount ordered
    private Integer orderAmount;
    //Price for single product
    private BigDecimal productPrice;
    //Total price
    private BigDecimal orderPrice;

    private Boolean isFlashOrder;
    private Integer promoId;

    public Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(Integer promoId) {
        this.promoId = promoId;
    }

    public Boolean getFlashOrder() {
        return isFlashOrder;
    }

    public void setFlashOrder(Boolean flashOrder) {
        isFlashOrder = flashOrder;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Integer orderAmount) {
        this.orderAmount = orderAmount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

}
