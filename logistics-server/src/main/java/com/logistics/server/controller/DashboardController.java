package com.logistics.server.controller;

import com.logistics.server.entity.LogisticsTrack;
import com.logistics.server.entity.Order;
import com.logistics.server.mapper.OrderMapper;
import com.logistics.server.mapper.TrackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TrackMapper trackMapper;

    @GetMapping("/overview")
    public Map<String, Object> overview() {
        List<Order> orders = orderMapper.findAll();
        List<LogisticsTrack> tracks = trackMapper.findAll();

        Map<Long, List<LogisticsTrack>> tracksByOrderId = tracks.stream()
                .filter(track -> track.getOrderId() != null)
                .collect(Collectors.groupingBy(LogisticsTrack::getOrderId));

        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);
        LocalDateTime staleThreshold = LocalDateTime.now().minusHours(24);

        Map<String, Object> summary = buildSummary(orders, today, staleThreshold, tracksByOrderId);
        List<Map<String, Object>> statusDistribution = buildStatusDistribution(orders);
        List<Map<String, Object>> orderTrend = buildOrderTrend(orders, sevenDaysAgo, today);
        List<Map<String, Object>> destinationRanking = buildDestinationRanking(orders);
        List<Map<String, Object>> abnormalOrders = buildAbnormalOrders(orders, tracksByOrderId, staleThreshold);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("summary", summary);
        data.put("statusDistribution", statusDistribution);
        data.put("orderTrend", orderTrend);
        data.put("destinationRanking", destinationRanking);
        data.put("abnormalOrders", abnormalOrders);

        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("msg", "查询成功");
        res.put("data", data);
        return res;
    }

    private Map<String, Object> buildSummary(List<Order> orders, LocalDate today, LocalDateTime staleThreshold,
                                             Map<Long, List<LogisticsTrack>> tracksByOrderId) {
        long totalOrders = orders.size();
        long todayOrders = orders.stream()
                .filter(order -> order.getCreateTime() != null && order.getCreateTime().toLocalDate().equals(today))
                .count();
        long pendingOrders = orders.stream().filter(order -> Integer.valueOf(0).equals(order.getStatus())).count();
        long inTransitOrders = orders.stream().filter(order -> Integer.valueOf(2).equals(order.getStatus()) || Integer.valueOf(3).equals(order.getStatus())).count();
        long finishedOrders = orders.stream().filter(order -> Integer.valueOf(4).equals(order.getStatus())).count();
        long abnormalOrders = orders.stream().filter(order -> isAbnormal(order, tracksByOrderId, staleThreshold)).count();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalOrders", totalOrders);
        summary.put("todayOrders", todayOrders);
        summary.put("pendingOrders", pendingOrders);
        summary.put("inTransitOrders", inTransitOrders);
        summary.put("finishedOrders", finishedOrders);
        summary.put("abnormalOrders", abnormalOrders);
        return summary;
    }

    private List<Map<String, Object>> buildStatusDistribution(List<Order> orders) {
        List<Map<String, Object>> statusList = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : getStatusLabelMap().entrySet()) {
            long count = orders.stream().filter(order -> entry.getKey().equals(order.getStatus())).count();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("status", entry.getKey());
            item.put("label", entry.getValue());
            item.put("count", count);
            statusList.add(item);
        }
        return statusList;
    }

    private List<Map<String, Object>> buildOrderTrend(List<Order> orders, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate, Long> countByDate = orders.stream()
                .filter(order -> order.getCreateTime() != null)
                .collect(Collectors.groupingBy(order -> order.getCreateTime().toLocalDate(), Collectors.counting()));

        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date.format(formatter));
            item.put("count", countByDate.getOrDefault(date, 0L));
            trend.add(item);
        }
        return trend;
    }

    private List<Map<String, Object>> buildDestinationRanking(List<Order> orders) {
        Map<String, Long> countByDestination = orders.stream()
                .map(order -> normalizeDestination(order.getReceiverAddress()))
                .collect(Collectors.groupingBy(destination -> destination, Collectors.counting()));

        return countByDestination.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(6)
                .map(entry -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> buildAbnormalOrders(List<Order> orders,
                                                          Map<Long, List<LogisticsTrack>> tracksByOrderId,
                                                          LocalDateTime staleThreshold) {
        return orders.stream()
                .filter(order -> isAbnormal(order, tracksByOrderId, staleThreshold))
                .sorted(Comparator.comparing(Order::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(10)
                .map(order -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", order.getId());
                    item.put("orderNo", order.getOrderNo());
                    item.put("receiverName", order.getReceiverName());
                    item.put("statusLabel", getStatusLabel(order.getStatus()));
                    item.put("reason", getAbnormalReason(order, tracksByOrderId, staleThreshold));
                    item.put("lastUpdate", formatDateTime(getLatestUpdateTime(order, tracksByOrderId)));
                    item.put("createTime", formatDateTime(order.getCreateTime()));
                    item.put("receiverAddress", order.getReceiverAddress());
                    return item;
                })
                .collect(Collectors.toList());
    }

    private boolean isAbnormal(Order order, Map<Long, List<LogisticsTrack>> tracksByOrderId, LocalDateTime staleThreshold) {
        if (order.getStatus() == null || Integer.valueOf(4).equals(order.getStatus())) {
            return false;
        }
        LocalDateTime latestUpdateTime = getLatestUpdateTime(order, tracksByOrderId);
        if (latestUpdateTime == null) {
            return order.getCreateTime() != null && order.getCreateTime().isBefore(staleThreshold);
        }
        return latestUpdateTime.isBefore(staleThreshold);
    }

    private String getAbnormalReason(Order order, Map<Long, List<LogisticsTrack>> tracksByOrderId, LocalDateTime staleThreshold) {
        LocalDateTime latestUpdateTime = getLatestUpdateTime(order, tracksByOrderId);
        if (latestUpdateTime == null) {
            return "订单创建后 24 小时内未产生物流轨迹";
        }
        if (latestUpdateTime.isBefore(staleThreshold)) {
            return "物流状态超过 24 小时未更新";
        }
        return "状态停留时间过长";
    }

    private LocalDateTime getLatestUpdateTime(Order order, Map<Long, List<LogisticsTrack>> tracksByOrderId) {
        List<LogisticsTrack> orderTracks = tracksByOrderId.get(order.getId());
        if (orderTracks == null || orderTracks.isEmpty()) {
            return order.getCreateTime();
        }
        return orderTracks.stream()
                .map(LogisticsTrack::getCreateTime)
                .filter(time -> time != null)
                .max(LocalDateTime::compareTo)
                .orElse(order.getCreateTime());
    }

    private String normalizeDestination(String address) {
        if (address == null || address.trim().isEmpty()) {
            return "未知目的地";
        }
        String clean = address.trim();
        String[] tokens = clean.split("[，,\\s]");
        return tokens.length > 0 && !tokens[0].isEmpty() ? tokens[0] : clean;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private Map<Integer, String> getStatusLabelMap() {
        Map<Integer, String> statusMap = new LinkedHashMap<>();
        statusMap.put(0, "待揽件");
        statusMap.put(1, "已揽件");
        statusMap.put(2, "运输中");
        statusMap.put(3, "派送中");
        statusMap.put(4, "已签收");
        return statusMap;
    }

    private String getStatusLabel(Integer status) {
        return Optional.ofNullable(getStatusLabelMap().get(status)).orElse("未知");
    }
}