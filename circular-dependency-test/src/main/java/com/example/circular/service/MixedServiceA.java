package com.example.circular.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class MixedServiceA {

    private MixedServiceB mixedServiceB;

    public MixedServiceA() {
        System.out.println("MixedServiceA 构造函数被调用");
    }

    @Autowired
//    @Lazy
    public void setMixedServiceB(MixedServiceB mixedServiceB) {
        System.out.println("MixedServiceA setter 被调用");
        this.mixedServiceB = mixedServiceB;
    }

    public String doSomething() {
        return "MixedServiceA 调用 MixedServiceB: " + mixedServiceB.doSomethingElse();
    }

    public String getName() {
        return "MixedServiceA";
    }
}
