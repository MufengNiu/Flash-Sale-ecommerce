package com.ecommerce.controller.ViewObjects;

public class OtpView {
    String otpToken;
    String otp;

    public String getOtpToken() {
        return otpToken;
    }

    public OtpView(String otpToken,String otp) {
        this.otpToken = otpToken;
        this.otp = otp;
    }

    public void setOtpToken(String otpToken) {
        this.otpToken = otpToken;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
