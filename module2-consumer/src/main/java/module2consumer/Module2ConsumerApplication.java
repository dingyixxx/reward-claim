package module2consumer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("module2consumer.mapper")  // 确保扫描mapper接口
public class Module2ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Module2ConsumerApplication.class, args);
        System.out.println("=== 模块2消费者服务启动成功 ===");
        System.out.println("正在监听 RabbitMQ 消息...");
    }

}
