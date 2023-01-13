package com.ecommerce.controller.ViewObjects;


import java.math.BigDecimal;

public class OrderView {
    private String id;
    private Integer productId;

    private Integer orderAmount;
    private Double productPrice;
    private Double OrderPrice;
    private Byte isFlashSale;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public Double getOrderPrice() {
        return OrderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        OrderPrice = orderPrice;
    }

    public Byte getIsFlashSale() {
        return isFlashSale;
    }

    public void setIsFlashSale(Byte isFlashSale) {
        this.isFlashSale = isFlashSale;
    }
}
