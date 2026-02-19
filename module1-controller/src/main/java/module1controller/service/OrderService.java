package module1controller.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import module1controller.entity.Order;
import module1controller.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RabbitMQProducerService rabbitMQProducerService;


    // DB 读操作 - 根据 ID 查询订单
    // DB 读操作 - 根据 ID 查询订单
    public Order getOrderById(Long id) {
        // 先从 Redis 查找（只查找ID映射）
        String orderIdStr = redisService.getValue("order:id:" + id);
        if (orderIdStr != null) {
            System.out.println("从 Redis 获取订单ID映射: " + id);
            // 如果Redis中有记录，从数据库查询完整信息
            return this.getById(id);
        }

        // Redis 中没有则从数据库查询
        Order order = this.getById(id);
        if (order != null) {
            // 查询到后写入 Redis 缓存（只缓存ID映射）
            try {
                redisService.setValue("order:id:" + id, id.toString());
                System.out.println("从数据库获取订单并缓存ID映射到 Redis: " + id);
            } catch (Exception e) {
                System.err.println("Redis缓存更新失败: " + e.getMessage());
            }
        }

        return order;
    }

    // DB 读操作 - 根据订单号查询
    public Order getOrderByOrderNo(String orderNo) {
        // 先从 Redis 查找
        String orderIdStr = redisService.getValue("order:no:" + orderNo);
        if (orderIdStr != null) {
            Long orderId = Long.parseLong(orderIdStr);
            Order order = (Order) redisService.getObject("order:" + orderId);
            if (order != null) {
                System.out.println("从 Redis 获取订单: " + orderNo);
                return order;
            }
        }

        // 从数据库查询
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        Order order = this.getOne(queryWrapper);

        if (order != null) {
            // 缓存到 Redis
            redisService.setObject("order:" + order.getId(), order);
            redisService.setValue("order:no:" + orderNo, order.getId().toString());
            System.out.println("从数据库获取订单并缓存到 Redis: " + orderNo);
        }

        return order;
    }

    // DB 读操作 - 查询用户的所有订单
    public List<Order> getOrdersByUserId(Long userId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return this.list(queryWrapper);
    }

    // DB 更新操作
    @Transactional
    public boolean updateOrderStatus(Long orderId, Integer status) {
        Order order = new Order();
        order.setId(orderId);

        boolean result = this.updateById(order);

        if (result) {
            // 更新 Redis 缓存 - 使用字符串存储避免序列化问题
            Order updatedOrder = this.getById(orderId);
            String orderInfo = updatedOrder.getOrderNo() + "|" + updatedOrder.getProductName() +
                    "|" + updatedOrder.getStatus() + "|" + updatedOrder.getTotalAmount();
            redisService.setString("order:" + orderId, orderInfo);
            System.out.println("更新订单状态并刷新 Redis 缓存: " + orderId);

            // 发送状态变更消息
        }

        return result;

    }

    // 测试商品数据
    private static final String[] PRODUCT_NAMES = {
            "iPhone 15 Pro Max", "MacBook Air M2", "iPad Pro 12.9", "Apple Watch Series 9",
            "AirPods Pro 2", "Samsung Galaxy S24 Ultra", "Sony WH-1000XM5", "Dell XPS 13",
            "Nike Air Jordan 1", "Adidas Ultraboost 22", "Levi's 501 Original", "Uniqlo U系列T恤",
            "星巴克咖啡豆", "农夫山泉矿泉水", "三顿半咖啡", "喜茶多肉葡萄",
            "Kindle Paperwhite", "Nintendo Switch OLED", "PlayStation 5", "Xbox Series X"
    };

    private static final String[] PRODUCT_CATEGORIES = {
            "电子产品", "数码配件", "服装配饰", "运动户外",
            "食品饮料", "图书音像", "游戏娱乐", "家居生活"
    };

    private static final BigDecimal[] PRICE_RANGES = {
            new BigDecimal("99.00"), new BigDecimal("199.00"), new BigDecimal("299.00"),
            new BigDecimal("499.00"), new BigDecimal("799.00"), new BigDecimal("1299.00"),
            new BigDecimal("1999.00"), new BigDecimal("2999.00"), new BigDecimal("4999.00"),
            new BigDecimal("8999.00"), new BigDecimal("12999.00")
    };
    @Autowired
    private ProductDataService productDataService; // 注入商品数据服务

    private void generateRandomOrderData(Order order) {
        Random random = new Random();

        // 使用专业的商品数据服务
        ProductDataService.ProductInfo productInfo = productDataService.getRandomProduct();

        order.setUserId((long) (random.nextInt(10000) + 1));
        order.setProductName(productInfo.getName()); // 使用专业商品名称
        order.setPrice(productInfo.getPrice());      // 使用合理价格
        order.setOrderNo(generateOrderNo());
        order.setQuantity(random.nextInt(5) + 1);
        order.setStatus(random.nextInt(3));

        System.out.println("生成商品类别: " + productInfo.getCategory());
        System.out.println("生成商品: " + productInfo.getName());
        System.out.println("生成价格: " + productInfo.getPrice());
    }

    // DB 写操作 - 创建订单（增强版）
    @Transactional
    public Order createOrder(Order order) {
        System.out.println("=== 开始创建订单 ===");

        // 如果是测试订单，生成随机数据
        if (order.getUserId() == null || order.getUserId() == 0) {
            generateRandomOrderData(order);
        }

        // 生成订单号
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        System.out.println("生成订单号: " + orderNo);

        // 计算总金额
        BigDecimal totalAmount = order.getPrice().multiply(new BigDecimal(order.getQuantity()));
        order.setTotalAmount(totalAmount);
        System.out.println("计算总金额: " + totalAmount);

        // 手动设置时间
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        order.setUpdateTime(now);
        System.out.println("设置时间: " + now);

        // 保存到数据库
        boolean saveResult = this.save(order);
        System.out.println("数据库保存结果: " + saveResult);

        if (!saveResult) {
            throw new RuntimeException("订单保存失败");
        }

        // 缓存处理
        try {
            redisService.setValue("order:id:" + order.getId(), order.getId().toString());
            redisService.setValue("order:no:" + orderNo, order.getId().toString());
            System.out.println("Redis缓存设置成功");
        } catch (Exception e) {
            System.err.println("Redis缓存设置失败: " + e.getMessage());
        }



        System.out.println("创建订单成功: " + order.getOrderNo());
        return order;
    }


    // 生成订单号
    private String generateOrderNo() {
        // 格式: ORD + 时间戳 + 随机字符
        StringBuilder sb = new StringBuilder("ORD");
        sb.append(System.currentTimeMillis());

        // 添加4位随机字母数字
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    // 获取订单状态文本
    private String getOrderStatusText(Integer status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已完成";
            case 3: return "已取消";
            default: return "未知状态";
        }
    }

    @Autowired
    private OrderMapper orderMapper;

    public Order createOrderWithSleep(Order request) {
        // 如果是测试订单，生成随机数据
        if (request.getUserId() == null || request.getUserId() == 0) {
            generateRandomOrderData(request);
        }
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setProductName(request.getProductName());
        order.setOrderNo(request.getOrderNo());
        order.setQuantity(request.getQuantity());
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        // 执行存储过程并获取返回的完整订单信息
        Order resultOrder = orderMapper.insertWithSleep(order);

        log.info("创建的订单信息: {}", resultOrder);
        return resultOrder;
    }


    public Order selectByIdWithSleep(Long id) {
        // 使用自定义SQL添加随机睡眠
        return orderMapper.selectByIdWithSleep(id);
    }
}
