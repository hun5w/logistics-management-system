package com.logistics.server.Controller;

import com.logistics.server.entity.Order;
import com.logistics.server.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    // 1. 创建订单
    @PostMapping("/create")
    public String createOrder(@RequestBody Order order) {
        order.setOrderNo("LOG" + System.currentTimeMillis());

        // 阶梯计费逻辑
        BigDecimal basePrice = new BigDecimal("10.00");
        BigDecimal weightPrice = order.getWeight().multiply(new BigDecimal("2.0"));
        BigDecimal distancePrice = BigDecimal.ZERO;
        if (order.getDistance() != null && order.getDistance().compareTo(new BigDecimal("100")) > 0) {
            distancePrice = order.getDistance().subtract(new BigDecimal("100")).multiply(new BigDecimal("0.5"));
        }
        order.setFee(basePrice.add(weightPrice).add(distancePrice).setScale(2, RoundingMode.HALF_UP));

        order.setStatus(0);
        int result = orderMapper.insertOrder(order);
        return result > 0 ? "订单创建成功！单号：" + order.getOrderNo() : "下单失败";
    }

    // 2. 获取所有订单列表
    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderMapper.findAll();
    }

    // 3. 更新订单状态
    // 前端传来 id 和目标 status
    @PutMapping("/status")
    public String updateOrderStatus(@RequestParam Long id, @RequestParam Integer status) {
        int result = orderMapper.updateStatus(id, status);
        return result > 0 ? "状态更新成功" : "状态更新失败";
    }
}