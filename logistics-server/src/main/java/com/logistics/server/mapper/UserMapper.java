package com.logistics.server.mapper;

import com.logistics.server.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据用户名查询用户
     * 用于：登录校验、注册时的唯一性检查
     */
    @Select("SELECT * FROM t_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     * 用于：规范化注册时检查手机号是否已被占用
     */
    @Select("SELECT * FROM t_user WHERE phone = #{phone}")
    User findByPhone(@Param("phone") String phone);

    /**
     * 规范化后的插入新用户
     * 增加了 phone 和 status 字段
     */
    @Insert("INSERT INTO t_user(username, password, role, nickname, phone, status, create_time) " +
            "VALUES(#{username}, #{password}, #{role}, #{nickname}, #{phone}, #{status}, NOW())")
    int insertUser(User user);
}