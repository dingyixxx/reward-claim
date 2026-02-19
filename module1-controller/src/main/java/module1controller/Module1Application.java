package module1controller;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@MapperScan("module1controller.mapper")
public class Module1Application {
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);   // 连接超时 5s
        requestFactory.setReadTimeout(10000);     // 读取超时 10s
        return new RestTemplate(requestFactory);
    }

    public static void main(String[] args) {
        SpringApplication.run(Module1Application.class, args);
    }
//    -javaagent:C:\Users\P72\Downloads\skywalking-agent\skywalking-agent.jar
//-Dskywalking.agent.service_name=consumer-app
//-Dskywalking.collector.backend_service=192.168.200.131:11800
}