package com.logistics.server.controller;

import com.logistics.server.entity.LogisticsTrack;
import com.logistics.server.entity.Order;
import com.logistics.server.mapper.OrderMapper;
import com.logistics.server.mapper.TrackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private TrackMapper trackMapper;

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
    @Transactional // 建议加上事务注解，保证订单状态和轨迹记录同时成功
    public String updateOrderStatus(@RequestParam Long id, @RequestParam Integer status) {
        // 1. 更新订单状态
        int result = orderMapper.updateStatus(id, status);

        // 2. 根据状态生成对应的轨迹文字
        String content = "";
        switch (status) {
            case 1: content = "仓库已揽件，正准备发往分拨中心"; break;
            case 2: content = "包裹已发出，正在运输途中"; break;
            case 3: content = "包裹已到达目的地网点，派送员正在派送"; break;
            case 4: content = "包裹已签收，感谢使用本物流服务"; break;
            default: content = "订单状态更新";
        }

        // 3. 插入轨迹表
        LogisticsTrack track = new LogisticsTrack();
        track.setOrderId(id);
        track.setContent(content);
        trackMapper.insertTrack(track);

        return result > 0 ? "状态及轨迹更新成功" : "更新失败";
    }

    // 4. 根据订单号查询单个订单 (轨迹查询)
    @GetMapping("/search")
    public Order getOrderByNo(@RequestParam String orderNo) {
        orderNo = orderNo.trim();
        // 1. 先根据用户输入的 order_no（字符串）找到订单基本信息
        Order order = orderMapper.findByOrderNo(orderNo);

        if (order != null) {
            // 2. 关键：获取该订单的自增 id (Long类型)，去轨迹表里查出所有记录
            List<LogisticsTrack> tracks = trackMapper.findByOrderId(order.getId());

            // 3. 把轨迹塞进订单对象里一起返回给前端
            order.setTracks(tracks);
        }
        return order;
    }

    // 5. 仓库入库操作
    @PutMapping("/arrive")
    @Transactional
    public String orderArrive(@RequestParam Long id, @RequestParam String location) {
        // 入库通常不改变订单大状态（依然是运输中），但要更新轨迹
        LogisticsTrack track = new LogisticsTrack();
        track.setOrderId(id);
        track.setContent("快件已到达 【" + location + "】");
        trackMapper.insertTrack(track);
        return "入库成功";
    }

    // 6. 仓库出库（发往下一站）
    @PutMapping("/depart")
    @Transactional
    public String orderDepart(@RequestParam Long id, @RequestParam String nextStop) {
        LogisticsTrack track = new LogisticsTrack();
        track.setOrderId(id);
        track.setContent("快件已从上一站发出，正发往 【" + nextStop + "】");
        trackMapper.insertTrack(track);
        return "出库成功";
    }
}