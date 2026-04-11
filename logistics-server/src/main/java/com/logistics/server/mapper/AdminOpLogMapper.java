package com.logistics.server.mapper;

import com.logistics.server.entity.AdminOpLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminOpLogMapper {

    @Insert("INSERT INTO t_admin_op_log(operator_username, target_user_id, action, detail, create_time) " +
            "VALUES(#{operatorUsername}, #{targetUserId}, #{action}, #{detail}, NOW())")
    int insertLog(AdminOpLog log);

    @Select("SELECT * FROM t_admin_op_log ORDER BY create_time DESC LIMIT 100")
    List<AdminOpLog> findRecentLogs();
}