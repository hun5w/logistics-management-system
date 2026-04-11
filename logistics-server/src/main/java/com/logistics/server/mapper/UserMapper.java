package com.logistics.server.mapper;

import com.logistics.server.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

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

    @Select("SELECT * FROM t_user ORDER BY create_time DESC")
    List<User> findAllUsers();

    @Select("SELECT * FROM t_user WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Select("SELECT * FROM t_user WHERE status = #{status} ORDER BY create_time DESC")
    List<User> findByStatus(@Param("status") Integer status);

    @Update("UPDATE t_user SET role = #{role} WHERE id = #{id}")
    int updateUserRole(@Param("id") Long id, @Param("role") String role);

    @Update("UPDATE t_user SET status = #{status} WHERE id = #{id}")
    int updateUserStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE t_user SET password = #{password} WHERE id = #{id}")
    int updateUserPassword(@Param("id") Long id, @Param("password") String password);
}