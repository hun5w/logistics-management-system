package com.logistics.server.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String role;     // ADMIN, WAREHOUSE, COURIER
    private String nickname;
    private String phone;     // 新增
    private Integer status;   // 新增
    private LocalDateTime createTime;
}