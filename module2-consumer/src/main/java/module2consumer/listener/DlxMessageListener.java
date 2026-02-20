package module2consumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DlxMessageListener {
//    @RabbitListener(queues = "dlx.queue")
    public void handleDeadLetterMessage(Message message) {
        log.error("=== 💀 接收到死信消息 ===");
        log.error("消息内容: {}", new String(message.getBody()));
        log.error("原交换机: {}", message.getMessageProperties().getReceivedExchange());
        log.error("原路由键: {}", message.getMessageProperties().getReceivedRoutingKey());
        log.error("重试次数: {}", message.getMessageProperties().getHeaders().get("x-death"));

        // 记录到监控系统或发送告警
        handleDeadMessage(message);
    }

    private void handleDeadMessage(Message message) {
        // 可以记录到数据库、发送邮件告警等
        log.error("死信消息已记录到监控系统");
    }

}
