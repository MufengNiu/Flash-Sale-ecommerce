package com.ecommerce.service.model;

import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductModel implements Serializable {
    private Integer id;
    private String title;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private Integer sales;  //Amount selled;
    private String imgurl; //url for image url

    //If promoModel is not null, then there is incoming promo event.
    private PromoModel promoModel;

    public PromoModel getPromoModel() {
        return promoModel;
    }

    public void setPromoModel(PromoModel promoModel) {
        this.promoModel = promoModel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) throws SystemException {

        if(id == null){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws SystemException {

        if(title == null || title.length() == 0){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) throws SystemException {

        if(price == null || price.compareTo(new BigDecimal(0)) < 0){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) throws SystemException {

        if(stock == null || stock < 0){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) throws SystemException {

        if(sales == null || sales < 0){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        this.sales = sales;
    }


    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) throws SystemException{

        if(imgurl == null || imgurl.length() == 0){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        this.imgurl = imgurl;
    }
}
