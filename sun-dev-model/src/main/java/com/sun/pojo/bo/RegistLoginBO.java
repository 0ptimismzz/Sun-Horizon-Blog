package com.sun.pojo.bo;
import jakarta.validation.constraints.NotBlank;

public class RegistLoginBO {

    @NotBlank(message = "手机号不能为空")
    private String mobile = null;
    @NotBlank(message = "短信验证码不能为空")
    private String smsCode = null;

    @Override
    public String toString() {
        return "RegistLoginBO{" +
                "mobile='" + mobile + '\'' +
                ", smsCode='" + smsCode + '\'' +
                '}';
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }
}
