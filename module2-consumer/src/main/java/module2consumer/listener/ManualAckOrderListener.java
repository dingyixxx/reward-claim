package module2consumer.listener;


import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import module2consumer.service.InventoryService;
import module2consumer.service.MessageDeduplicationService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ManualAckOrderListener {

    private final InventoryService inventoryService;
    private final MessageDeduplicationService deduplicationService;




//    如果需要消息过期进入死信队列,那就注释掉listener就可以
    @RabbitListener(queues = "order.queue")
    public void handleMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws Exception {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        String messageId = extractMessageId(messageBody);

        log.info("===  接收到订单消息 ===");
        log.info("消息ID: {}", messageId);
        log.info("Delivery Tag: {}", deliveryTag);
        log.info("消息内容: {}", messageBody);

        // 手动从上下文获取
        RetryContext context = RetrySynchronizationManager.getContext();
        int retryCount = (context != null) ? context.getRetryCount() : 0;
        log.info("重试次数: {}", retryCount);


        try {
            // 幂等性检查
            if (deduplicationService.isProcessed(messageId)) {
                log.warn("消息已处理过，直接确认: {}", messageId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 处理字符串消息
            log.info("处理字符串消息: {}", messageBody);

            // 解析订单信息
            String[] parts = messageBody.split("\\|");
            if (parts.length >= 5) {
                Long orderId = Long.parseLong(parts[1].split(":")[1]);
                String orderNo = parts[2].split(":")[1];
                String productName = parts[3].split(":")[1];
                Long userId = Long.parseLong(parts[4].split(":")[1]);
                Integer quantity = Integer.parseInt(parts[5].split(":")[1]);

//                首次处理+重试4次=5次 失败后进入死信队列
                log.info("订单信息: {}, {}, {}, {}, {}", orderId, orderNo, productName, userId, quantity);
                if (1==1) {
                    log.error("模拟消费失败！订单号: {}", orderNo);
                    throw new RuntimeException("模拟业务处理失败");
                }
                // 更新库存 - 业务处理
                inventoryService.updateInventory(orderId, orderNo, productName, userId, quantity);

                // 标记为已处理
                deduplicationService.markAsProcessed(messageId);

                // 手工确认消息
                channel.basicAck(deliveryTag, false);
                log.info("消息处理成功并确认: {}", deliveryTag);
            } else {
                Thread.sleep(10000);
                log.error("消息格式不正确: {}", messageBody);
                // 格式错误的消息直接拒绝，不重新入队
//                channel.basicNack(deliveryTag, false, false);
            }
        } catch (Exception e) {
           if (retryCount<4){
               log.error("处理消息时发生异常: {}", e.getMessage(), e);
               // 让RabbitMQ自动重试，不手动Nack
               throw e; // 重新抛出异常触发RabbitMQ重试机制
           }else{
               log.error("go to dead letter queue: {}", e.getMessage(), e);
               channel.basicNack(deliveryTag, false, false);
           }
        }
    }

    /**
     * 从消息内容中提取消息ID用于去重
     */
    private String extractMessageId(String messageBody) {
        return "MSG_" + Math.abs(messageBody.hashCode());
    }
}
