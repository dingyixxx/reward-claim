package com.example.sharding.controller;

import com.example.sharding.entity.Order;
import com.example.sharding.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单（当前时间）
     */
    @PostMapping("/create")
    public Map<String, Object> createOrder(@RequestParam Long userId,
                                           @RequestParam BigDecimal amount,
                                           @RequestParam(required = false) String orderNo) {
        Order order = orderService.createOrder(userId, amount, orderNo);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "创建成功");
        result.put("data", order);
        result.put("targetTable", "t_order_" + order.getCreateTime().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHH")));
        return result;
    }

    /**
     * 创建测试数据（模拟 19:05、20:59、21:00 三个时间点）
     */
    @PostMapping("/create/test-data")
    public Map<String, Object> createTestData() {
        orderService.createTestData();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "测试数据创建成功");
        result.put("data", Map.of(
                "19:05", "t_order_2026022219",
                "20:59", "t_order_2026022220",
                "21:00", "t_order_2026022221"
        ));
        return result;
    }

    /**
     * 创建订单（指定时间）
     */
    @PostMapping("/create/with-time")
    public Map<String, Object> createOrderWithTime(@RequestParam Long userId,
                                                   @RequestParam BigDecimal amount,
                                                   @RequestParam String createTime) {
        LocalDateTime time = LocalDateTime.parse(createTime);
        Order order = orderService.createOrderWithTime(userId, amount, null, time);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "创建成功");
        result.put("data", order);
        result.put("targetTable", "t_order_" + order.getCreateTime().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHH")));
        return result;
    }

    /**
     * 查询所有订单
     */
    @GetMapping("/list")
    public Map<String, Object> listAll() {
        List<Order> orders = orderService.queryAllOrders();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", orders);
        result.put("total", orders.size());
        return result;
    }

    /**
     * 根据时间范围查询
     */
    @GetMapping("/list/range")
    public Map<String, Object> listByTimeRange(@RequestParam String startTime,
                                               @RequestParam String endTime) {
        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);
        List<Order> orders = orderService.queryByTimeRange(start, end);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", orders);
        result.put("total", orders.size());
        return result;
    }

    /**
     * 根据订单号查询
     */
    @GetMapping("/detail/{orderNo}")
    public Map<String, Object> getByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.queryByOrderNo(orderNo);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", order);
        return result;
    }

    /**
     * 聚合统计
     */
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        BigDecimal totalAmount = orderService.getTotalAmount();
        Long totalCount = orderService.getTotalCount();
        List<Map<String, Object>> hourStats = orderService.countByHour();

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", Map.of(
                "totalAmount", totalAmount,
                "totalCount", totalCount,
                "hourStats", hourStats
        ));
        return result;
    }
}
