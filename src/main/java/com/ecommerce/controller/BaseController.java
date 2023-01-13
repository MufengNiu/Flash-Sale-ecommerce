package com.ecommerce.controller;


import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;
import com.ecommerce.response.CommonReturnType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class BaseController {

    protected static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";
    /**
     * Exception handler to handle exception are accepted by controller Level
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK) // 200
    @ResponseBody
    public Object HandlerException(HttpServletRequest request, Exception e) throws Exception {

        HashMap<String,Object> respsonseData = new HashMap<>();

        if(e instanceof SystemException sysException){
            respsonseData.put("errCode" , sysException.getErrorCode());
            respsonseData.put("errMsg", sysException.getErrorMessgae());
        }else if(e instanceof NoHandlerFoundException){
            respsonseData.put("errCode", ErrorEnum.UNKNOWN_ERROR.getErrorCode());
            respsonseData.put("errMsg","No Handler Found");
        } else{
              throw e;
//            respsonseData.put("errCode", ErrorEnum.UNKNOWN_ERROR.getErrorCode());
//            respsonseData.put("errMsg" , ErrorEnum.UNKNOWN_ERROR.getErrorMessgae());
        }

        return CommonReturnType.create(respsonseData, "FAIL");
    }

    /**
     * MD5 encoding
     * @param input
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    protected String MD5Encode(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder base64Encoder = Base64.getEncoder();

        String encodedString = Arrays.toString(base64Encoder.encode(md5.digest(input.getBytes(StandardCharsets.UTF_8))));
        return encodedString;
    }

}
