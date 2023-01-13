package com.ecommerce.service.Implementor;

import com.ecommerce.service.CacheService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheServerImplementer implements CacheService {

    private Cache<String, Object> commonCache = null;

    @PostConstruct
    public void init(){
        commonCache = CacheBuilder.newBuilder()
                .initialCapacity(10)   //original capacity
                .maximumSize(100)      //max key storage, if exceed them remove by LRU
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
    }

    @Override
    public void setCommonCache(String key, Object value) {
        commonCache.put(key,value);
    }

    @Override
    public Object getFromCommonCache(String key) {
        return commonCache.getIfPresent(key);
    }
}
