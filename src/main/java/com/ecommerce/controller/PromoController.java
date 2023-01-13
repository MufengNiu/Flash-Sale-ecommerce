package com.ecommerce.controller;

import com.ecommerce.error.SystemException;
import com.ecommerce.response.CommonReturnType;
import com.ecommerce.service.PromoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("promo")
@RequestMapping("/promo")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*" , originPatterns = "*")
public class PromoController extends BaseController {

    @Resource
    private PromoService promoService;


    @RequestMapping("/")
    @ResponseBody
    public String promoPage(){
        return "Promo Page";
    }

    @RequestMapping("/publishPromoEvent")
    @ResponseBody
    public CommonReturnType publishPromoEvent(@RequestParam(name = "promoId")Integer promoId) throws SystemException {

        promoService.publishPromo(promoId);

        return CommonReturnType.create("Promo Event published successful");
    }


}
