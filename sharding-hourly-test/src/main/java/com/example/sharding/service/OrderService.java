package com.example.sharding.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.sharding.entity.Order;
import com.example.sharding.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");

    /**
     * 创建订单（使用当前时间）
     */
    @Transactional
    public Order createOrder(Long userId, BigDecimal amount, String orderNo) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAmount(amount);
        order.setOrderNo(orderNo != null ? orderNo : generateOrderNo());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);
        log.info("创建订单成功：id={}, orderNo={}, createTime={}, 目标表=t_order_{}",
                order.getId(), order.getOrderNo(), order.getCreateTime(),
                order.getCreateTime().format(FORMATTER));
        return order;
    }

    /**
     * 创建订单（指定创建时间，用于测试分片路由）
     */
    @Transactional
    public Order createOrderWithTime(Long userId, BigDecimal amount, String orderNo, LocalDateTime createTime) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAmount(amount);
        order.setOrderNo(orderNo != null ? orderNo : generateOrderNo());
        order.setCreateTime(createTime);
        order.setUpdateTime(LocalDateTime.now());

        orderMapper.insert(order);
        log.info("创建订单成功：id={}, orderNo={}, createTime={}, 目标表=t_order_{}",
                order.getId(), order.getOrderNo(), order.getCreateTime(),
                order.getCreateTime().format(FORMATTER));
        return order;
    }

    /**
     * 批量创建测试数据（模拟不同小时）
     */
    @Transactional
    public void createTestData() {
        log.info("========== 开始创建测试数据 ==========");

        // 模拟 19:05 的数据 -> 应该写入 t_order_2026022219
        LocalDateTime time1905 = LocalDateTime.of(2026, 2, 22, 19, 5, 0);
        createOrderWithTime(1001L, new BigDecimal("199.00"), "ORD_19_05", time1905);

        // 模拟 20:59 的数据 -> 应该写入 t_order_2026022220
        LocalDateTime time2059 = LocalDateTime.of(2026, 2, 22, 20, 59, 0);
        createOrderWithTime(1002L, new BigDecimal("299.00"), "ORD_20_59", time2059);

        // 模拟 21:00 的数据 -> 应该写入 t_order_2026022221
        LocalDateTime time2100 = LocalDateTime.of(2026, 2, 22, 21, 0, 0);
        createOrderWithTime(1003L, new BigDecimal("399.00"), "ORD_21_00", time2100);

        log.info("========== 测试数据创建完成 ==========");
    }

    /**
     * 根据时间范围查询
     */
    public List<Order> queryByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.info("查询时间范围：{} ~ {}", startTime, endTime);
        return orderMapper.selectByTimeRange(startTime, endTime);
    }

    /**
     * 根据用户 ID 查询
     */
    public List<Order> queryByUserId(Long userId) {
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return orderMapper.selectList(wrapper);
    }

    /**
     * 查询所有订单（跨分片）
     */
    public List<Order> queryAllOrders() {
        return orderMapper.selectList(null);
    }

    /**
     * 根据订单号查询
     */
    public Order queryByOrderNo(String orderNo) {
        return orderMapper.selectByOrderNo(orderNo);
    }

    /**
     * 聚合查询 - 总金额
     */
    public BigDecimal getTotalAmount() {
        return orderMapper.sumAmount();
    }

    /**
     * 聚合查询 - 总数量
     */
    public Long getTotalCount() {
        return orderMapper.totalCount();
    }

    /**
     * 按小时统计
     */
    public List<Map<String, Object>> countByHour() {
        return orderMapper.countByHour();
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis();
    }
}
