package module1controller;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@MapperScan("module1controller.mapper")
public class Module1Application {
    public static void main(String[] args) {
        SpringApplication.run(Module1Application.class, args);
    }
}