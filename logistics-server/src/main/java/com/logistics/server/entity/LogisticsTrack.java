package com.logistics.server.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LogisticsTrack {
    private Long id;
    private Long orderId;
    private String content;
    private LocalDateTime createTime;
}