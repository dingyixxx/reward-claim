package com.example.sharding;

import com.example.sharding.entity.Order;
import com.example.sharding.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class ShardingHourlyTest {

    @Autowired
    private OrderService orderService;

    /**
     * 测试 1：模拟 19:05 的数据 -> 应该写入 t_order_2026022219
     */
    @Test
    public void test1905() {
        log.info("========== 测试：19:05 的数据 ==========");
        LocalDateTime time1905 = LocalDateTime.of(2026, 2, 22, 19, 5, 0);
        Order order = orderService.createOrderWithTime(1001L, new BigDecimal("199.00"), "ORD_19_05", time1905);
        log.info("✅ 订单创建成功，预期表：t_order_2026022219");
    }

    /**
     * 测试 2：模拟 20:59 的数据 -> 应该写入 t_order_2026022220
     */
    @Test
    public void test2059() {
        log.info("========== 测试：20:59 的数据 ==========");
        LocalDateTime time2059 = LocalDateTime.of(2026, 2, 22, 20, 59, 0);
        Order order = orderService.createOrderWithTime(1002L, new BigDecimal("299.00"), "ORD_20_59", time2059);
        log.info("✅ 订单创建成功，预期表：t_order_2026022220");
    }

    /**
     * 测试 3：模拟 21:00 的数据 -> 应该写入 t_order_2026022221
     */
    @Test
    public void test2100() {
        log.info("========== 测试：21:00 的数据 ==========");
        LocalDateTime time2100 = LocalDateTime.of(2026, 2, 22, 21, 0, 0);
        Order order = orderService.createOrderWithTime(1003L, new BigDecimal("399.00"), "ORD_21_00", time2100);
        log.info("✅ 订单创建成功，预期表：t_order_2026022221");
    }

    /**
     * 测试 4：综合测试（三个时间点一起）
     */
    @Test
    public void testAllThreeTimes() {
        log.info("========== 综合测试：三个时间点 ==========");
        orderService.createTestData();

        // 验证查询
        List<Order> allOrders = orderService.queryAllOrders();
        log.info("总订单数：{}", allOrders.size());

        // 按小时统计
        List<Map<String, Object>> hourStats = orderService.countByHour();
        log.info("按小时统计：");
        for (Map<String, Object> stat : hourStats) {
            log.info("  小时={}, 数量={}", stat.get("hour"), stat.get("count"));
        }
    }

    /**
     * 测试 5：时间范围查询
     */
    @Test
    public void testTimeRangeQuery() {
        log.info("========== 测试：时间范围查询 ==========");
        LocalDateTime startTime = LocalDateTime.of(2026, 2, 22, 20, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2026, 2, 22, 21, 59, 59);

        List<Order> orders = orderService.queryByTimeRange(startTime, endTime);
        log.info("时间范围查询结果数：{}", orders.size());
        for (Order order : orders) {
            log.info("订单：orderNo={}, createTime={}", order.getOrderNo(), order.getCreateTime());
        }
    }

    /**
     * 测试 6：聚合查询
     */
    @Test
    public void testAggregation() {
        log.info("========== 测试：聚合查询 ==========");
        BigDecimal totalAmount = orderService.getTotalAmount();
        Long totalCount = orderService.getTotalCount();
        log.info("总金额：{}, 总数量：{}", totalAmount, totalCount);
    }
}
