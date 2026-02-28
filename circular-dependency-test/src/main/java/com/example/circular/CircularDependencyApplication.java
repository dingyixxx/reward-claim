package com.example.circular;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CircularDependencyApplication {

    public static void main(String[] args) {
        System.out.println("=== 开始启动循环依赖测试应用 ===");
        try {
            SpringApplication.run(CircularDependencyApplication.class, args);
            System.out.println("=== 应用启动成功! ===");
        } catch (Exception e) {
            System.err.println("=== 应用启动失败 ===");
            e.printStackTrace();
        }
    }
}
