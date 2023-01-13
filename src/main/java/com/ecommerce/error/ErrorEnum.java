package com.ecommerce.error;

public enum ErrorEnum implements CommonError {

    //Common error code
    PARAMETER_VALIDATION_ERROR(00000, "INVALID PARAMETER"),
    USER_NOT_EXIST(10001, "USER NOT EXIST"),
    INCORRECT_OTP_CODE(10002, "WRONG OTP CODE"),
    DUPLICATE_PHONE_NUMBER(10003,"DUPLICATE PHONE NUMBER"),
    USER_LOGIN_FAILED(10004,"USER DO NOT EXIST OR INCORRECT PASSWORD"),
    PRODUCT_NOT_EXIST(10005, "PRODUCT DO NOT EXIST"),
    INVALID_ORDER_AMOUNT(10006,"Invalid order amount"),
    NOT_ENOUGH_STOCK(10007,"Not enough stock"),
    USER_NOT_LOGIN(10008,"User Not Login"),
    NO_PROMO_EVENT(10009, "No available promo event for current product"),
    PRODUCT_NOT_IN_PROMO(100010,"Current product is not in flash sell"),
    ORDER_PLACE_FIALED(100011,"Order place failed"),
    INVALID_PROMO_TOKEN(100012,"Invalid promo token"),
    NO_PUBLISHED_PROMO_EVENT(100013,"Promo Event has not been published"),
    UNKNOWN_ERROR(99999,"UNKNOWN ERROR")
    ;
    private int errCode;
    private String errMsg;

    private ErrorEnum(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrorCode() {
        return this.errCode;
    }

    @Override
    public String getErrorMessgae() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
       this.errMsg = errorMsg;
       return this;
    }
}
