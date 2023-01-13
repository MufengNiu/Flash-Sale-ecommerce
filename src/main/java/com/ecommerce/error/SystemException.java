package com.ecommerce.error;

//Wrapper for business error
public class SystemException extends Exception implements CommonError{

    private final CommonError commonError;

    public SystemException(CommonError commonError){
        super();
        this.commonError = commonError;
    }

    // Self-defined errorMsg
    public SystemException(CommonError commonError, String errMsg){
        super();
        commonError.setErrorMsg(errMsg);
        this.commonError = commonError;
    }


    @Override
    public int getErrorCode() {
        return this.commonError.getErrorCode();
    }

    @Override
    public String getErrorMessgae() {
        return this.commonError.getErrorMessgae();
    }

    @Override
    public CommonError setErrorMsg(String errorMsg) {
        this.commonError.setErrorMsg(errorMsg);
        return this.commonError;
    }
}
