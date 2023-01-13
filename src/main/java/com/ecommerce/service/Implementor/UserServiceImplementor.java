package com.ecommerce.service.Implementor;

import com.ecommerce.dao.UserDOMapper;
import com.ecommerce.dao.UserPasswordDOMapper;
import com.ecommerce.dataobject.UserDO;
import com.ecommerce.dataobject.UserPasswordDO;
import com.ecommerce.error.ErrorEnum;
import com.ecommerce.error.SystemException;
import com.ecommerce.service.UserService;
import com.ecommerce.service.model.UserModel;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImplementor implements UserService {

    @Resource
    private UserDOMapper userDOMapper;
    @Resource
    private UserPasswordDOMapper userPasswordDOMapper;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * Get corresponding data object by userdaomapper
     * @param id: user id
     */
    @Override
    public UserModel getUserById(Integer id) throws SystemException {

        UserDO userDO = userDOMapper.selectByPrimaryKey(id);

        if(userDO == null){
            return null;
        }

        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(id);

        return convertFromDAO(userDO, userPasswordDO);
    }

    @Override
    @Transactional // make transaction atomic
    public void userRegister(UserModel userModel) throws SystemException {

        if(userModel == null){
            throw new SystemException(ErrorEnum.PARAMETER_VALIDATION_ERROR);
        }

        //parameters have already been validated

        //Model to user data object
        UserDO userDO = convertFromModel(userModel);

        try{
            userDOMapper.insertSelective(userDO); // check parameters first. do not insert null object. null -> dont update
        }catch(DuplicateKeyException e){
            throw new SystemException(ErrorEnum.DUPLICATE_PHONE_NUMBER);
        }

        userModel.setId(userDO.getId());

        //Add to database
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
    }

    /**
     * Validate user login input parameters
     * @return userModel contains user info
     */
    @Override
    public UserModel loginValidate(String phone, String encryptedPassword) throws SystemException {

        //Get user info by phone number.
        UserDO userDo = userDOMapper.selectByPhone(phone);

        if(userDo == null){
            throw new SystemException(ErrorEnum.USER_LOGIN_FAILED);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDo.getId());

        UserModel userModel = convertFromDAO(userDo,userPasswordDO);

        //check excrypted password and input password
        if( encryptedPassword.equals(userModel.getEncrypyPassword()) ){
            //Login successful
            return userModel;
        }else{
            throw new SystemException(ErrorEnum.USER_LOGIN_FAILED);
        }

    }

    @Override
    public UserModel getUserInCache(Integer id) throws SystemException {

        UserModel userModel;
        String key = "userValidate:"+id;

        userModel = (UserModel) redisTemplate.opsForValue().get(key);
        if (userModel == null){
            userModel = getUserById(id);
            redisTemplate.opsForValue().set(key,userModel);
            redisTemplate.expire(key,5, TimeUnit.MINUTES);
        }

        return userModel;
    }

    private UserDO convertFromModel(UserModel userModel){
        if(userModel == null){
            return null;
        }

        UserDO res = new UserDO();
        BeanUtils.copyProperties(userModel , res);

        return res;
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel){

        if(userModel == null){
            return null;
        }

        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrypyPassword(userModel.getEncrypyPassword());
        userPasswordDO.setUserId(userModel.getId());

        return userPasswordDO;
    }

    private UserModel convertFromDAO(UserDO userDO , UserPasswordDO userPasswordDO) throws SystemException {

        if(userDO == null){
            return null;
        }

        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);

        if(userPasswordDO != null){
            userModel.setEncrypyPassword(userPasswordDO.getEncrypyPassword());
        }

        return userModel;
    }

}
