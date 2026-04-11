package com.logistics.server.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminOpLog {
    private Long id;
    private String operatorUsername;
    private Long targetUserId;
    private String action;
    private String detail;
    private LocalDateTime createTime;
}