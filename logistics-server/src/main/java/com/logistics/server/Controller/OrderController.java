package com.logistics.server.Controller;

import com.logistics.server.entity.Order;
import com.logistics.server.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*") // 确保跨域正常
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @PostMapping("/create")
    public String createOrder(@RequestBody Order order) {
        // 1. 生成唯一订单号
        order.setOrderNo("LOG" + System.currentTimeMillis());

        // 2. 模拟阶梯计费算法 (毕设亮点)
        // 逻辑：起步价 10元 + 重量费(2元/kg) + 远程费(超出100km后，每km加0.5元)
        BigDecimal basePrice = new BigDecimal("10.00");
        BigDecimal weightPrice = order.getWeight().multiply(new BigDecimal("2.0"));

        BigDecimal distancePrice = BigDecimal.ZERO;
        if (order.getDistance() != null && order.getDistance().compareTo(new BigDecimal("100")) > 0) {
            distancePrice = order.getDistance().subtract(new BigDecimal("100")).multiply(new BigDecimal("0.5"));
        }

        BigDecimal totalFee = basePrice.add(weightPrice).add(distancePrice);
        order.setFee(totalFee.setScale(2, RoundingMode.HALF_UP)); // 保留两位小数

        // 3. 初始状态设为 0 (待揽件)
        order.setStatus(0);

        // 4. 执行写入
        int result = orderMapper.insertOrder(order);

        return result > 0 ? "订单创建成功！单号：" + order.getOrderNo() + "，计算运费：" + order.getFee() + "元" : "系统繁忙，下单失败";
    }
}