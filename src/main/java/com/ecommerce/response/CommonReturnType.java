package com.ecommerce.response;

public class CommonReturnType {

    //Success or Fail
    private String status;

    // If status is success , then data will return the required json data
    // If status is fail, then data will return common error code.
    private Object data;

    public CommonReturnType(){
        this.status = null;
        this.data = null;
    }

    //If no status info is given, then result is success
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result, "success");
    }

    // Return result based on status
    public static CommonReturnType create(Object result ,String status){
        CommonReturnType type = new CommonReturnType();
        type.setData(result);
        type.setStatus(status);

        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
