package com.logistics.server.mapper;

import com.logistics.server.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

    // 1. 插入订单功能
    @Insert("INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, " +
            "receiver_name, receiver_phone, receiver_address, weight, distance, fee, status) " +
            "VALUES(#{orderNo}, #{senderName}, #{senderPhone}, #{senderAddress}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverAddress}, #{weight}, #{distance}, #{fee}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);

    // 2. 查询所有订单 (按创建时间倒序，让新订单排在最前面)
    @Select("SELECT * FROM t_order ORDER BY create_time DESC")
    List<Order> findAll();

    // 3. 更新订单状态 (用于 揽件 -> 运输 -> 签收 的状态流转)
    @Update("UPDATE t_order SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    // 4. 根据订单号查询 (预留给后期的轨迹查询功能)
    @Select("SELECT * FROM t_order WHERE order_no = #{orderNo}")
    Order findByOrderNo(@Param("orderNo") String orderNo);
}