package com.ecommerce.service;

import com.ecommerce.error.SystemException;
import com.ecommerce.service.model.ProductModel;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;

public interface ProductService {

    //Create product
    ProductModel addProduct(ProductModel productModel) throws SystemException;

    //View Product
    ProductModel getProduct(Integer product_id) throws SystemException;

    boolean decreasePromoProductStock(Integer productId, Integer orderAmount,Integer promoId) throws SystemException, MQBrokerException, RemotingException, InterruptedException, MQClientException;

    boolean decreaseStock(Integer productId, Integer orderAmount);

    boolean updateSales(Integer productId , Integer amount);

    boolean asyncDecreaseStock(Integer productId,Integer orderAmount);

    ProductModel getProductInCache(Integer productId) throws SystemException;

    void rollbackStockInRedis(String key,Integer amount);

    String initialStockLog(Integer productId, Integer orderAmount, Boolean isFlashSale);

    //View product list
    List<ProductModel> getProductList();
}
