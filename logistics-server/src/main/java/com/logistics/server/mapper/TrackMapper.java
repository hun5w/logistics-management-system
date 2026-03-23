package com.logistics.server.mapper;

import com.logistics.server.entity.LogisticsTrack;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface TrackMapper {

    @Insert("INSERT INTO t_track(order_id, content) VALUES(#{orderId}, #{content})")
    int insertTrack(LogisticsTrack track);

    @Select("SELECT * FROM t_track WHERE order_id = #{orderId} ORDER BY create_time DESC")
    List<LogisticsTrack> findByOrderId(@Param("orderId") Long orderId);
}