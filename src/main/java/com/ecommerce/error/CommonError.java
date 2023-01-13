package com.ecommerce.error;

public interface CommonError {
    public int getErrorCode();
    public String getErrorMessgae();
    public CommonError setErrorMsg(String errorMsg);
}
