package com.logistics.server.mapper;

import com.logistics.server.entity.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface OrderMapper {

    @Insert("INSERT INTO t_order(order_no, sender_name, sender_phone, sender_address, " +
            "receiver_name, receiver_phone, receiver_address, weight, distance, fee, status) " +
            "VALUES(#{orderNo}, #{senderName}, #{senderPhone}, #{senderAddress}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverAddress}, #{weight}, #{distance}, #{fee}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrder(Order order);
}