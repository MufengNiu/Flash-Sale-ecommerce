package com.ecommerce;

import com.ecommerce.config.SpringUtil;
import com.ecommerce.error.SystemException;
import com.ecommerce.service.Implementor.PromoServiceImplementor;
import com.ecommerce.service.PromoService;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication(scanBasePackages = {"com.ecommerce"})
@RestController
@MapperScan("com.ecommerce.dao")
public class App
{

    public static void main( String[] args ) throws SystemException {
        SpringApplication.run(App.class,args);

        //Publish Available promo event to redis
        ApplicationContext context = SpringUtil.getApplicationContext();
        PromoService promoService = context.getBean(PromoService.class);// 注意是Service，不是ServiceImpl
        promoService.publishAvailablePromoEvent();
    }

    @RequestMapping("/")
    @ResponseBody
    public String homePage(){
        return "HOME";
    }

}
