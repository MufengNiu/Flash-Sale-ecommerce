package com.ecommerce.service.model;

import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;

import java.io.Serializable;


//Implements serializable for distributed session on redis
public class UserModel implements Serializable {
    private Integer id;
    private String name;
    private Byte gender;
    private Integer age;
    private String phone;
    private String registerMod;
    private String thirdParty;
    private String encrypyPassword;

    public String getEncrypyPassword() {
        return encrypyPassword;
    }

    public String getRegisterMod() {
        return registerMod;
    }

    public void setRegisterMod(String registerMod) {
        this.registerMod = registerMod;
    }

    public void setEncrypyPassword(String encrypyPassword) throws SystemException {

        if(encrypyPassword == null || encrypyPassword.length() == 0){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        this.encrypyPassword = encrypyPassword;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws SystemException {

        if(name == null || name.length() == 0){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        this.name = name;
    }

    public Byte getGender() {
        return gender;
    }

    public void setGender(Byte gender) throws SystemException {

        if(gender == null){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) throws SystemException {

        if(age == null || age < 0 || age > 120){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) throws SystemException {

        if(phone == null || phone.length() == 0){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        this.phone = phone;
    }

    public String getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(String thirdParty) {
        this.thirdParty = thirdParty;
    }
}
