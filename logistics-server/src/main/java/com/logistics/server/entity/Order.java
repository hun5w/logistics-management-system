package com.logistics.server.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Order {
    private Long id;
    private String orderNo;      // 对应数据库 order_no
    private String senderName;
    private String senderPhone;
    private String senderAddress;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private BigDecimal weight;
    private BigDecimal fee;
    private Integer status;      // 0待揽件 1已揽件 ...
    private LocalDateTime createTime;
    private BigDecimal distance;
}