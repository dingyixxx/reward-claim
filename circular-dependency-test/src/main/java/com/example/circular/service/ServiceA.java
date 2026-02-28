package com.example.circular.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ServiceA {

    // 构造器注入方式的循环依赖, 即使开了spring:
    //  main:
    //    allow-circular-references: true也会导致死锁, 无法利用上三级缓存解决循环
    private final ServiceB serviceB;

    @Lazy
    public ServiceA(ServiceB serviceB) {
        System.out.println("ServiceA 构造函数被调用");
        this.serviceB = serviceB;
    }

    public String doSomething() {
        return "ServiceA 调用 ServiceB: " + serviceB.doSomethingElse();
    }

    public String getName() {
        return "ServiceA";
    }
}
