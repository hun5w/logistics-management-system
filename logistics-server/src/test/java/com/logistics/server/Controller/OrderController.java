package com.logistics.server.controller;

import com.logistics.server.entity.Order;
import com.logistics.server.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @PostMapping("/create")
    public String createOrder(@RequestBody Order order) {
        // 1. 自动生成订单号 (模拟逻辑)
        order.setOrderNo("LOG" + System.currentTimeMillis());

        // 2. 模拟运费计算：重量 * 5元/kg (毕设亮点：可以根据距离扩展)
        if (order.getWeight() != null) {
            order.setFee(order.getWeight().multiply(new BigDecimal("5.0")));
        }

        // 3. 存入数据库
        int result = orderMapper.insertOrder(order);

        return result > 0 ? "订单创建成功，单号：" + order.getOrderNo() : "创建失败";
    }
}