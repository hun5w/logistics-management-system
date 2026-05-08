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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TrackMapper trackMapper;

    /**
     * 1. 创建订单（管理员权限）
     */
    @PostMapping("/create")
    public Map<String, Object> createOrder(@RequestBody Order order) {
        Map<String, Object> res = new HashMap<>();

        // 生成唯一单号
        order.setOrderNo("LOG" + System.currentTimeMillis());

        // 阶梯计费逻辑：基础10元 + 重量(2元/kg) + 距离(超出100km部分0.5元/km)
        BigDecimal basePrice = new BigDecimal("10.00");
        BigDecimal weightPrice = order.getWeight().multiply(new BigDecimal("2.0"));
        BigDecimal distancePrice = BigDecimal.ZERO;
        if (order.getDistance() != null && order.getDistance().compareTo(new BigDecimal("100")) > 0) {
            distancePrice = order.getDistance().subtract(new BigDecimal("100")).multiply(new BigDecimal("0.5"));
        }

        order.setFee(basePrice.add(weightPrice).add(distancePrice).setScale(2, RoundingMode.HALF_UP));
        order.setStatus(0); // 待揽收

        int result = orderMapper.insertOrder(order);
        if (result > 0) {
            res.put("code", 200);
            res.put("msg", "订单创建成功");
            res.put("orderNo", order.getOrderNo());
        } else {
            res.put("code", 500);
            res.put("msg", "数据库写入失败");
        }
        return res;
    }

    /**
     * 2. 获取所有订单列表（管理员/仓库员可见）
     */
    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderMapper.findAll();
    }

    /**
     * 3. 更新订单状态（带权限校验与轨迹生成）
     */
    @PutMapping("/status")
    @Transactional
    public Map<String, Object> updateOrderStatus(
            @RequestParam Long id,
            @RequestParam Integer status,
            @RequestHeader(value = "User-Role", required = false) String role) {

        Map<String, Object> res = new HashMap<>();

        Order currentOrder = orderMapper.findById(id);
        if (currentOrder == null) {
            res.put("code", 404);
            res.put("msg", "订单不存在");
            return res;
        }

        if (status == null || status < 1 || status > 4) {
            res.put("code", 400);
            res.put("msg", "目标状态不合法");
            return res;
        }

        // 🚩 权限校验：只有管理员或快递员能操作签收(status=4)
        if (!"ADMIN".equals(role)) {
            boolean allowed = false;
            if ("WAREHOUSE".equals(role)) {
                allowed = (currentOrder.getStatus() == 0 && status == 1)
                        || (currentOrder.getStatus() == 1 && status == 2);
            } else if ("COURIER".equals(role)) {
                allowed = (currentOrder.getStatus() == 2 && status == 3)
                        || (currentOrder.getStatus() == 3 && status == 4);
            }

            if (!allowed) {
                res.put("code", 403);
                res.put("msg", "权限不足或状态流转不合法");
                return res;
            }
        }

        // 1. 更新订单状态
        int result = orderMapper.updateStatus(id, status);
        if (result <= 0) {
            res.put("code", 400);
            res.put("msg", "订单不存在或更新失败");
            return res;
        }

        // 2. 生成对应轨迹内容
        String content;
        switch (status) {
            case 1: content = "仓库已揽件，正准备发往分拨中心"; break;
            case 2: content = "包裹已发出，正在运输途中"; break;
            case 3: content = "包裹已到达目的地网点，派送员正在派送"; break;
            case 4: content = "包裹已签收，感谢使用本物流服务"; break;
            default: content = "订单状态已更新至: " + status;
        }

        // 3. 记录轨迹
        LogisticsTrack track = new LogisticsTrack();
        track.setOrderId(id);
        track.setContent(content);
        trackMapper.insertTrack(track);

        res.put("code", 200);
        res.put("msg", "状态及轨迹更新成功");
        return res;
    }

    /**
     * 3.1 删除订单（管理员）
     */
    @DeleteMapping
    @Transactional
    public Map<String, Object> deleteOrder(@RequestParam Long id,
                                           @RequestHeader(value = "User-Role", required = false) String role) {
        Map<String, Object> res = new HashMap<>();

        if (!"ADMIN".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员可删除订单");
            return res;
        }

        Order currentOrder = orderMapper.findById(id);
        if (currentOrder == null) {
            res.put("code", 404);
            res.put("msg", "订单不存在");
            return res;
        }

        trackMapper.deleteByOrderId(id);
        int result = orderMapper.deleteById(id);
        if (result > 0) {
            res.put("code", 200);
            res.put("msg", "订单删除成功");
        } else {
            res.put("code", 500);
            res.put("msg", "订单删除失败");
        }
        return res;
    }

    /**
     * 4. 轨迹查询（公共接口）
     */
    @GetMapping("/search")
    public Map<String, Object> getOrderByNo(@RequestParam String orderNo) {
        Map<String, Object> res = new HashMap<>();
        Order order = orderMapper.findByOrderNo(orderNo.trim());

        if (order != null) {
            // 获取并注入该订单的所有轨迹
            List<LogisticsTrack> tracks = trackMapper.findByOrderId(order.getId());
            order.setTracks(tracks);

            res.put("code", 200);
            res.put("data", order);
        } else {
            res.put("code", 404);
            res.put("msg", "未查询到该单号信息");
        }
        return res;
    }

    /**
     * 5. 当前登录用户订单列表
     */
    @GetMapping("/mine")
    public Map<String, Object> getMyOrders(
            @RequestHeader(value = "User-Role", required = false) String role,
            @RequestHeader(value = "User-Name", required = false) String username,
            @RequestHeader(value = "User-Phone", required = false) String phone) {
        Map<String, Object> res = new HashMap<>();

        if (!"USER".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅普通用户可查看个人订单");
            return res;
        }

        if (username == null || username.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
            res.put("code", 400);
            res.put("msg", "缺少必要用户信息，无法获取个人订单");
            return res;
        }

        List<Order> orders = orderMapper.findBySenderNameAndPhone(username.trim(), phone.trim());
        res.put("code", 200);
        res.put("data", orders);
        return res;
    }

    /**
     * 6. 仓库入库（仅限管理员和仓库员）
     */
    @PutMapping("/arrive")
    @Transactional
    public Map<String, Object> orderArrive(@RequestParam Long id,
                                           @RequestParam String location,
                                           @RequestHeader(value = "User-Role", required = false) String role) {
        Map<String, Object> res = new HashMap<>();

        if (!"ADMIN".equals(role) && !"WAREHOUSE".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员或仓库员可执行入库操作");
            return res;
        }

        Order currentOrder = orderMapper.findById(id);
        if (currentOrder == null) {
            res.put("code", 404);
            res.put("msg", "订单不存在");
            return res;
        }

        int update = orderMapper.updateStatus(id, 1);
        if (update <= 0) {
            res.put("code", 500);
            res.put("msg", "入库时更新订单状态失败");
            return res;
        }

        LogisticsTrack track = new LogisticsTrack();
        track.setOrderId(id);
        track.setContent("快件已到达 【" + location + "】");
        trackMapper.insertTrack(track);

        res.put("code", 200);
        res.put("msg", "入库记录成功，订单状态已更新为已揽件");
        return res;
    }

    /**
     * 6. 仓库出库
     */
    @PutMapping("/depart")
    @Transactional
    public Map<String, Object> orderDepart(@RequestParam Long id,
                                           @RequestParam String nextStop,
                                           @RequestHeader(value = "User-Role", required = false) String role) {
        Map<String, Object> res = new HashMap<>();

        if (!"ADMIN".equals(role) && !"WAREHOUSE".equals(role)) {
            res.put("code", 403);
            res.put("msg", "仅管理员或仓库员可执行出库操作");
            return res;
        }

        Order currentOrder = orderMapper.findById(id);
        if (currentOrder == null) {
            res.put("code", 404);
            res.put("msg", "订单不存在");
            return res;
        }

        int update = orderMapper.updateStatus(id, 2);
        if (update <= 0) {
            res.put("code", 500);
            res.put("msg", "出库时更新订单状态失败");
            return res;
        }

        LogisticsTrack track = new LogisticsTrack();
        track.setOrderId(id);
        track.setContent("快件已从上一站发出，正发往 【" + nextStop + "】");
        trackMapper.insertTrack(track);

        res.put("code", 200);
        res.put("msg", "出库记录成功，订单状态已更新为运输中");
        return res;
    }
}