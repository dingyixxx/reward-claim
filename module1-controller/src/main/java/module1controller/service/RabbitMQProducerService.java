package module1controller.service;

import module1controller.entity.Order;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class RabbitMQProducerService {
    private static final String ORDER_QUEUE = "order.queue";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 发送消息到已存在的队列
    public void sendOrderMessage(Object message) {
        try {
            String messageStr = convertToString(message);
            // 创建消息属性
            MessageProperties properties = new MessageProperties();
            properties.setExpiration("6000"); // 设置20秒过期时间
            // 创建消息对象
            Message mqMessage = new Message(messageStr.getBytes(), properties);
            // 发送到队列
            rabbitTemplate.send("", ORDER_QUEUE, mqMessage);
            System.out.println("✅ RabbitMQ消息发送成功: " + messageStr);
        } catch (Exception e) {
            System.err.println("❌ RabbitMQ消息发送失败: " + e.getMessage());
            // 记录日志但不中断主流程
        }
    }

    private String convertToString(Object message) {
        if (message instanceof Order) {
             Order order =
                    (Order) message;
            return String.format("ORDER_CREATED|ID:%d|NO:%s|PRODUCT:%s|USER:%d|Quantity:%d",
                    order.getId(), order.getOrderNo(), order.getProductName(), order.getUserId(),order.getQuantity());
        }
        return message.toString();
    }

    // 添加缺失的死信队列测试方法
    public void sendToDLXTestMessage() {
        Map<String, Object> failMessage = new HashMap<>();
        failMessage.put("orderNo", "FAIL_ORDER_001");
        failMessage.put("message", "这是一个会失败的测试消息");
        failMessage.put("timestamp", System.currentTimeMillis());

        // 发送会导致失败的消息
        sendOrderMessage(failMessage);
        System.out.println("📤 发送测试失败消息到死信队列流程");
    }
}
