package com.ecommerce.controller;

import com.ecommerce.controller.ViewObjects.OtpView;
import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;
import com.ecommerce.response.CommonReturnType;
import com.ecommerce.service.Implementor.UserServiceImplementor;
import com.ecommerce.service.model.UserModel;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.controller.ViewObjects.UserView;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*" , originPatterns = "*")
public class UserController extends BaseController{

    @Resource
    private UserServiceImplementor userService;

    //Spring bean wraper. ThreadLocal
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private HttpServletResponse httpServletResponse;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("/")
    @ResponseBody
    public String userPage(){
        return "USER PAGE";
    }


    //Get Information of currently login user
    @RequestMapping(value = "/GetLoginUser", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType GetLoginUser(){

        //By session
//      Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
//      UserModel currUser = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");
//
//      if(isLogin == null || !isLogin){
//          return CommonReturnType.create(null,"fail");
//      }

        //By Token
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if(StringUtils.isEmpty(token)){
            return CommonReturnType.create(null,"fail");
        }
        UserModel currUser = (UserModel) redisTemplate.opsForValue().get(token);
        if(currUser == null){
            return CommonReturnType.create(null,"fail");
        }

        return CommonReturnType.create(currUser);
    }

    @RequestMapping(value = "/userLogOff", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType userLogOff(){

        //By Session
//        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
//        if ( isLogin != null && isLogin) {
//            httpServletRequest.getSession().removeAttribute("IS_LOGIN");
//            httpServletRequest.getSession().removeAttribute("LOGIN_USER");
//        }

        //By Token
        String token = httpServletRequest.getParameterMap().get("token")[0];
        boolean isLogin = StringUtils.isEmpty(token) ? false : true;
        if (isLogin) {
            redisTemplate.delete(token);
        }

        return CommonReturnType.create("Log Off Successful");
    }

    /**
     * Invoke "Service" service to obtain user object and return to front-end
     * @param userId User id : Primary key of user_info table
     */
    @RequestMapping(value = "/get", method = {RequestMethod.POST},consumes = {BaseController.CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer userId) throws SystemException {

        UserModel userModel = userService.getUserById(userId);

        if(userModel == null){
            throw new SystemException(ErrorEnum.USER_NOT_EXIST);
        }

        UserView userView = convertFromModel(userModel);
        //Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");

        String token = httpServletRequest.getParameterMap().get("token")[0];

        if(StringUtils.isEmpty(token) || redisTemplate.opsForValue().get(token) == null){
            userView.setIsLogin(0);
        }else{
            userView.setIsLogin(1);
        }

        // Return common object
        return CommonReturnType.create(userView);
    }

    /**
     * User registration by one time password
     */
    @RequestMapping(value = "/register", method = {RequestMethod.POST},consumes = {BaseController.CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType userRegister(@RequestParam(name="phone")String phone,
                                         @RequestParam(name="userOtp")String userOtp,
                                         @RequestParam(name="name")String name,
                                         @RequestParam(name="gender")Integer gender,
                                         @RequestParam(name="age")Integer age,
                                         @RequestParam(name="password")String password) throws SystemException, UnsupportedEncodingException, NoSuchAlgorithmException {


        //By Session
        //Validate correspondence of otpcode and user phone number.
//        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(phone);
//        if( !checkOtpCode(inSessionOtpCode,userOtp)){
//            throw new SystemException(ErrorEnum.INCORRECT_OTP_CODE);
//        }

        //By Token via redis
        String otpToken = httpServletRequest.getParameterMap().get("otpToken")[0];
        if(StringUtils.isEmpty(otpToken)){
            throw new SystemException(ErrorEnum.INCORRECT_OTP_CODE);
        }
        String inSessionOtpCode = (String) redisTemplate.opsForValue().get(otpToken);

        if( !checkOtpCode(inSessionOtpCode,userOtp)){
            throw new SystemException(ErrorEnum.INCORRECT_OTP_CODE);
        }

        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(age);
        userModel.setGender(Byte.parseByte(String.valueOf(gender.intValue())));
        userModel.setPhone(phone);
        userModel.setRegisterMod("Phone");
        userModel.setEncrypyPassword( super.MD5Encode(password)  );

        //Registration
        userService.userRegister(userModel);

        return CommonReturnType.create("Registration Successful");
    }


    /**
     * User login function
     */
    @RequestMapping(value = "/login", method = {RequestMethod.POST},consumes = {BaseController.CONTENT_TYPE_FORMED})
    @ResponseBody()
    public CommonReturnType userLogin(@RequestParam(name = "phone")String phone , @RequestParam(name = "password")String password) throws SystemException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //Parameter validation
        if (phone == null || phone.length() == 0 || password == null || password.length() == 0) {
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        //Validate login info
        UserModel userModel;
        try {
            userModel = userService.loginValidate(phone, super.MD5Encode(password));
        } catch (SystemException e) {
            throw new SystemException(ErrorEnum.USER_LOGIN_FAILED);
        }

        //By Session
//        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", httpServletRequest.getSession().getId() ) // key & value
//                .httpOnly(true)       //
//                .secure(true)     // Allow http transfer
//                .domain("localhost")// Domain name
//                .path("/")       // path
//                .maxAge(3600)  // Set 1 hour lifetime
//                .sameSite("None")  //
//                .build()
//                ;
//        httpServletResponse.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//        Add login verification to session
//        this.httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
//        this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);

        //By Token
        //Login token , UUID
        String uuidToken = UUID.randomUUID().toString().replace("-","");
        //Connection between token and user login state
        redisTemplate.opsForValue().set(uuidToken,userModel,1,TimeUnit.HOURS);

        return CommonReturnType.create(uuidToken);
    }

    /**
     * Valid user input Otp and otp stored in session
     */
    private boolean checkOtpCode(String inSessionOtpCode , String userOtp){

        if(inSessionOtpCode == null || inSessionOtpCode.equals("") || userOtp == null || userOtp.equals("") ){
            return false;
        }
        return inSessionOtpCode.equals(userOtp);
    }
    /**
     * User register function. Get otp . One Time Password
     */

    @RequestMapping(value = "/getotp", method = {RequestMethod.POST},consumes = {BaseController.CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "phone") String phone){

        Random r = new Random();

        //Generate otp code
        // Random otp : 10000 to 700001
        int randomOtp = r.nextInt(60001) + 10000;
        String userOtp = String.valueOf(randomOtp);

        //By Session
//        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", httpServletRequest.getSession().getId() ) // key & value
//                .httpOnly(true)       //
//                .secure(true)     // Allow http transfer
//                .domain("localhost")// Domain name
//                .path("/")       // path
//                .maxAge(3600)  // Set 1 hour lifetime
//                .sameSite("None")  //
//                .build()
//                ;
//        httpServletResponse.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
//        //Connect Otp code to corresponding user phone number, Add to http session
//        httpServletRequest.getSession().setAttribute(phone,userOtp);

        //By Token via redis
        String uuidToken = UUID.randomUUID().toString().replace("-","");
        redisTemplate.opsForValue().set(uuidToken,userOtp,30,TimeUnit.MINUTES);

        //Pass otp code to user through message
        System.out.println("User otp for " + phone + " is: " + userOtp);

        return CommonReturnType.create(new OtpView(uuidToken,userOtp));
    }

    private UserView convertFromModel(UserModel userModel){

        if(userModel == null){
            return null;
        }

        UserView userView = new UserView();
        BeanUtils.copyProperties(userModel,userView);

        return userView;
    }




}
