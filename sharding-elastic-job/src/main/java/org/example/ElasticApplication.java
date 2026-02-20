package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ElasticApplication {
    public static void main(String[] args) {
        // 调试：打印 snakeyaml 来源
        Class<?> clazz = org.yaml.snakeyaml.Yaml.class;
        System.out.println("SnakeYAML loaded from: " + clazz.getProtectionDomain().getCodeSource());
        SpringApplication.run(ElasticApplication.class, args);
//        -Dserver.port=8094
    }
}
