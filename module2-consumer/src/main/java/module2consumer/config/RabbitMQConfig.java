package module2consumer.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RabbitMQConfig {

    // 原始队列和交换机配置
    public static final String ORDER_QUEUE = "order.queue";
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_ROUTING_KEY = "order.routing.key";

    // 死信队列配置
    public static final String DLX_QUEUE = "dlx.queue";

    @Value("dlx.exchange")
    private String dlxExchange;

    @Value("dlx.routing.key")
    private String dlxRoutingKey;

    // 死信交换机
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(dlxExchange);
    }

    // 死信队列
    @Bean
    public Queue dlxQueue() {
        return new Queue(DLX_QUEUE, true);
    }

    // 绑定死信队列到死信交换机
    @Bean
    public Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue()).to(dlxExchange()).with(dlxRoutingKey);
    }






    // 带死信配置的订单队列
    @Bean
    public Queue orderQueue() {

        Map<String, Object> arguments = new HashMap<>();

        // 关键：正确设置死信交换机
        arguments.put("x-dead-letter-exchange", "dlx.exchange");
        // 关键：正确设置死信路由键
        arguments.put("x-dead-letter-routing-key", "dlx.routing.key");
        arguments.put("x-message-ttl", 6000);

        return new Queue("order.queue", true, false, true, arguments);

    }

    // 订单交换机
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    // 绑定订单队列到交换机
    @Bean
    public Binding orderBinding() {
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with(ORDER_ROUTING_KEY);
    }

    // 配置 RabbitTemplate 使用 JSON 转换器
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}
