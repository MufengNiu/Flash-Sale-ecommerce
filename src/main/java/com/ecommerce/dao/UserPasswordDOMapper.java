package com.ecommerce.dao;

import com.ecommerce.dataobject.UserPasswordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserPasswordDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table encrypt_password
     *
     * @mbg.generated Thu Dec 22 21:34:19 AEDT 2022
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table encrypt_password
     *
     * @mbg.generated Thu Dec 22 21:34:19 AEDT 2022
     */
    int insert(UserPasswordDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table encrypt_password
     *
     * @mbg.generated Thu Dec 22 21:34:19 AEDT 2022
     */
    int insertSelective(UserPasswordDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table encrypt_password
     *
     * @mbg.generated Thu Dec 22 21:34:19 AEDT 2022
     */
    UserPasswordDO selectByPrimaryKey(Integer id);
    UserPasswordDO selectByUserId(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table encrypt_password
     *
     * @mbg.generated Thu Dec 22 21:34:19 AEDT 2022
     */
    int updateByPrimaryKeySelective(UserPasswordDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table encrypt_password
     *
     * @mbg.generated Thu Dec 22 21:34:19 AEDT 2022
     */
    int updateByPrimaryKey(UserPasswordDO record);
}