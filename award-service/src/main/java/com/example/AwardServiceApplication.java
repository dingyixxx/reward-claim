package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.client")
public class AwardServiceApplication {
    @Bean
    public HttpMessageConverters httpMessageConverters() {
        return new HttpMessageConverters();
    }

    public static void main(String[] args) {
        SpringApplication.run(AwardServiceApplication.class, args);
    }


}