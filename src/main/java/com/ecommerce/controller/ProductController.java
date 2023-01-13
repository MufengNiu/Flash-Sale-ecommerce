package com.ecommerce.controller;

import com.ecommerce.controller.ViewObjects.ProductView;
import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;
import com.ecommerce.response.CommonReturnType;
import com.ecommerce.service.CacheService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.model.ProductModel;
import com.ecommerce.service.model.PromoModel;
import jakarta.annotation.Resource;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller("product")
@RequestMapping("/product")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*" , originPatterns = "*")
public class ProductController extends BaseController{

    @Resource
    ProductService productService;

    @Resource
    private CacheService cacheService;
    @Resource
    private RedisTemplate redisTemplate;

    //Main Page
    @RequestMapping("/")
    @ResponseBody
    public String userPage(){
        return "Product Page";
    }

    //Create Product
    @RequestMapping(value = "/create", method = {RequestMethod.POST},consumes = {BaseController.CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createProduct(@RequestParam(name = "title")String title,
                                          @RequestParam(name = "description")String Description,
                                          @RequestParam(name = "price")BigDecimal price,
                                          @RequestParam(name = "stock")Integer stock,
                                          @RequestParam(name = "imgUrl")String imgUrl) throws SystemException {

        ProductModel productModel = new ProductModel();
        productModel.setTitle(title);
        productModel.setPrice(price);
        productModel.setImgurl(imgUrl);
        productModel.setDescription(Description);
        productModel.setStock(stock);

        ProductModel newProductModel = productService.addProduct(productModel);
        ProductView productView = convertFromModel(newProductModel);

        //Update cache
        String key =  "productList";
        List<ProductModel> newPorductList = productService.getProductList();

        cacheService.setCommonCache(key, newPorductList);
        redisTemplate.opsForValue().set(key,newPorductList);

        return  CommonReturnType.create(productView);
    }

    /**
     *  Return a productView object by selected id.
     * @param id product id
     * @return productView object converted from product Model
     * @throws SystemException id product id not exist in database
     */
    @RequestMapping(value = "/get", method = {RequestMethod.GET} )
    @ResponseBody
    public CommonReturnType getProduct( @RequestParam(name = "id")Integer id) throws SystemException {

        ProductModel productModel;
        String key = "product:" + id;

        //Check local cache first
        productModel = (ProductModel) cacheService.getFromCommonCache(key);
        if(productModel == null){

            //Check redis cache
            productModel = (ProductModel) redisTemplate.opsForValue().get(key);
            if(productModel == null){

                //Not in redis. Load from Mysql
                productModel = productService.getProduct(id);
                if(productModel == null){
                    throw new SystemException(ErrorEnum.PRODUCT_NOT_EXIST);
                }
                //Store in redis cache
                redisTemplate.opsForValue().set(key,productModel);
            }

            //Store in local cache
            cacheService.setCommonCache(key,productModel);
        }

        ProductView productView = convertFromModel(productModel);
        return CommonReturnType.create(productView);
    }

    /**
     *
     * @return List of product View ordered by sales
     */
    @RequestMapping(value = "/getProducts", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getAllProducts(){

        List<ProductModel> modelList;
        String key = "productList";

        //Check local cache
        modelList = (List<ProductModel>) cacheService.getFromCommonCache(key);
        if(modelList == null){

            //Check redis cache
            modelList = (List<ProductModel>) redisTemplate.opsForValue().get(key);
            if(modelList == null){

                //Get from mysql
                modelList = productService.getProductList();
                redisTemplate.opsForValue().set(key,modelList);
            }

            cacheService.setCommonCache(key,modelList);
        }

        List<ProductView> productViewList;
        productViewList = modelList.stream().map(productModel -> {
            return this.convertFromModel(productModel);
        }).toList();

        return CommonReturnType.create(productViewList);
    }

    private ProductView convertFromModel(ProductModel productModel){

        if(productModel == null){
            return null;
        }

        ProductView productView = new ProductView();
        BeanUtils.copyProperties(productModel,productView);

        //Promo event related parameters
        if(productModel.getPromoModel() != null ){

            PromoModel promoModel = productModel.getPromoModel();
            productView.setPromoStatus(promoModel.getStatus());
            productView.setPromoName(promoModel.getPromoName());
            productView.setPromoStartTime(promoModel.getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            productView.setPromoPrice(promoModel.getPromoPrice());
            productView.setPromoId(promoModel.getId());

        }else{
            productView.setPromoStatus(0);
            productView.setPromoName(null);
            productView.setPromoStartTime(null);
            productView.setPromoPrice(null);
            productView.setPromoId(null);
        }

        return productView;
    }



}
