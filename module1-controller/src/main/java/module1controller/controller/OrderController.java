package module1controller.controller;



import lombok.extern.slf4j.Slf4j;
import module1controller.entity.Order;
import module1controller.service.OrderService;
import module1controller.service.RabbitMQProducerService;
import module1controller.service.RedisService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;


    @Value("${key:defaultkey100}")
    private String key1;

    @Value("${value:defaultvalue200}")
    private String val1;


    // 9. 测试接口 - 创建测试订单
    // 9. 测试接口 - 创建测试订单（增强版）
    // 9. 测试接口 - 创建测试订单（完整版 - 包含所有功能）
    @PostMapping("/test/create")
    public Map<String, Object> createTestOrder() {
        log.info("key1: " + key1);
        log.info("val1: " + val1);
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> operations = new HashMap<>();

        try {
            System.out.println("=== 开始完整测试流程 ===");

            // 1. DB 写操作 - 创建订单
            System.out.println("--- 1. 创建订单 (DB写) ---");
            Order testOrder = new Order();
            // 让Service生成随机数据
            Order createdOrder = orderService.createOrderWithSleep(testOrder);
            operations.put("1_db_write", Map.of(
                    "success", true,
                    "message", "订单创建成功",
                    "order_id", createdOrder.getId(),
                    "order_no", createdOrder.getOrderNo(),
                    "product_name", createdOrder.getProductName(),
                    "price", createdOrder.getPrice(),
                    "quantity", createdOrder.getQuantity()
            ));
            System.out.println("订单创建成功: " + createdOrder.getOrderNo());

            // 2. DB 读操作 - 查询刚创建的订单
            System.out.println("--- 2. 查询订单 (DB读) ---");
            Order retrievedOrder = orderService.selectByIdWithSleep(createdOrder.getId());
            operations.put("2_db_read", Map.of(
                    "success", true,
                    "message", "订单查询成功",
                    "order_data", Map.of(
                            "id", retrievedOrder.getId(),
                            "order_no", retrievedOrder.getOrderNo(),
                            "product_name", retrievedOrder.getProductName(),
                            "status", retrievedOrder.getStatus()
                    )
            ));
            System.out.println("订单查询成功: " + retrievedOrder.getOrderNo());

            // 3. Redis 写操作 - 缓存订单信息
            System.out.println("--- 3. Redis写入 (缓存) ---");
            String redisKey = "order:test:" + createdOrder.getId();
            String redisValue = createdOrder.getOrderNo() + "|" + createdOrder.getProductName();
            redisService.setValueWithExpire(redisKey, redisValue, 300L, TimeUnit.SECONDS);

            // 验证 Redis 写入
            String verifyValue = redisService.getValue(redisKey);
            operations.put("3_redis_write", Map.of(
                    "success", verifyValue != null && verifyValue.equals(redisValue),
                    "message", verifyValue != null ? "Redis写入验证成功" : "Redis写入验证失败",
                    "key", redisKey,
                    "value", redisValue,
                    "verified_value", verifyValue
            ));
            System.out.println("Redis写入: " + redisKey + " = " + redisValue);

            // 4. Redis 读操作 - 读取缓存
            System.out.println("--- 4. Redis读取 (缓存) ---");
            String redisReadValue = redisService.getValue(redisKey);
            operations.put("4_redis_read", Map.of(
                    "success", redisReadValue != null,
                    "message", redisReadValue != null ? "Redis读取成功" : "Redis读取失败",
                    "key", redisKey,
                    "value", redisReadValue
            ));
            System.out.println("Redis读取: " + redisKey + " = " + redisReadValue);

            // 5. 发送 RabbitMQ 消息
            System.out.println("--- 5. 发送RabbitMQ消息 ---");

            rabbitMQProducerService.sendOrderMessage(createdOrder);
            operations.put("5_rabbitmq_send", Map.of(
                    "success", true,
                    "message", "RabbitMQ消息发送成功",
                    "message_content",createdOrder.toString()
            ));
            System.out.println("RabbitMQ消息发送成功");

            // 6. 查看所有 Redis keys（额外功能）
            System.out.println("--- 6. 查看Redis Keys ---");
            Set<String> allKeys = redisService.getAllKeys();
            operations.put("6_redis_keys", Map.of(
                    "success", true,
                    "message", "Redis keys获取成功",
                    "keys_count", allKeys.size(),
                    "sample_keys", allKeys.stream().limit(5).collect(Collectors.toList())
            ));
            System.out.println("Redis keys总数: " + allKeys.size());

            // 7. 更新订单状态（额外功能）
            System.out.println("--- 7. 更新订单状态 ---");
            boolean updateSuccess = orderService.updateOrderStatus(createdOrder.getId(), 1);
            Order updatedOrder = orderService.getOrderById(createdOrder.getId());
            operations.put("7_db_update", Map.of(
                    "success", updateSuccess,
                    "message", updateSuccess ? "订单状态更新成功" : "订单状态更新失败",
                    "old_status", 0,
                    "new_status", updatedOrder.getStatus(),
                    "status_text", getOrderStatusText(updatedOrder.getStatus())
            ));
            System.out.println("订单状态更新: 0 -> " + updatedOrder.getStatus());

            // 构建最终结果
            result.put("success", true);
            result.put("message", "完整测试流程执行成功");
            result.put("operations", operations);
            result.put("summary", Map.of(
                    "total_operations", 7,
                    "successful_operations", operations.size(),
                    "test_order_id", createdOrder.getId(),
                    "test_order_no", createdOrder.getOrderNo(),
                    "test_product", createdOrder.getProductName(),
                    "test_amount", createdOrder.getTotalAmount()
            ));

            System.out.println("=== 完整测试流程完成 ===");

        } catch (Exception e) {
            System.err.println("完整测试流程异常: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "完整测试流程执行失败: " + e.getMessage());
            result.put("operations", operations);
        }

        return result;
    }


    // 辅助方法：获取订单状态文本
    private String getOrderStatusText(Integer status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已完成";
            case 3: return "已取消";
            default: return "未知状态";
        }
    }

    // 在现有控制器中添加新的测试端点

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @GetMapping("/test-dlx")
    public String testDeadLetter() {
        // 发送一条5秒后过期的消息
        String testMessage = "DLX_TEST_" + System.currentTimeMillis();
        // 创建消息属性
        MessageProperties properties = new MessageProperties();
        properties.setExpiration("6000");
        // 创建消息对象
        Message mqMessage = new Message(testMessage.getBytes(), properties);
        // 发送到队列
        rabbitTemplate.send("", "order.queue", mqMessage);
        return "已发送测试消息，5秒后应进入死信队列";
    }
    @GetMapping("/check-queues")
    public Map<String, Object> checkQueueStatus() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查队列是否存在和配置
            RabbitAdmin admin = new RabbitAdmin(rabbitTemplate.getConnectionFactory());

            Properties orderQueueProps = admin.getQueueProperties("order.queue");
            Properties dlxQueueProps = admin.getQueueProperties("dlx.queue");

            result.put("order.queue.exists", orderQueueProps != null);
            result.put("dlx.queue.exists", dlxQueueProps != null);

            if (orderQueueProps != null) {
                result.put("order.queue.arguments", orderQueueProps.get("QUEUE_ARGUMENTS"));
            }

        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        return result;
    }

    // 测试死信队列功能
    @PostMapping("/test/dlx")
    public Map<String, Object> sendDLXTestMessage() {
        Map<String, Object> result = new HashMap<>();
        try {
            rabbitMQProducerService.sendToDLXTestMessage();
            result.put("success", true);
            result.put("message", "测试消息已发送到死信队列流程");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "发送失败: " + e.getMessage());
        }
        return result;
    }
    // 10. 综合测试接口 - 一次性执行所有操作


}
