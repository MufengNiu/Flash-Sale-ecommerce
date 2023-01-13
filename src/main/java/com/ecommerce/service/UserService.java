package com.ecommerce.service;

import com.ecommerce.error.SystemException;
import com.ecommerce.service.model.UserModel;

public interface UserService {

    /**
     * Return user object by id
     */
    public UserModel getUserById(Integer id) throws SystemException;

    void userRegister(UserModel userModel) throws SystemException;

    UserModel loginValidate(String phone , String encryptedPassword) throws SystemException;

    UserModel getUserInCache(Integer id) throws SystemException;
}
