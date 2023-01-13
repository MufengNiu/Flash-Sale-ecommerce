package com.ecommerce.service;


//Local cache wrapper
public interface CacheService {

    //Store
    void setCommonCache(String key, Object value);

    Object getFromCommonCache(String key);
}
