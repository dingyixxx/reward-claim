package com.example.circular.service;

import org.springframework.stereotype.Service;

@Service
public class ServiceB {

    // 构造器注入方式的循环依赖
    private final ServiceA serviceA;

    public ServiceB(ServiceA serviceA) {
        System.out.println("ServiceB 构造函数被调用");
        this.serviceA = serviceA;
    }

    public String doSomethingElse() {
        return "ServiceB 调用 ServiceA: " + serviceA.getName();
    }

    public String getName() {
        return "ServiceB";
    }
}
