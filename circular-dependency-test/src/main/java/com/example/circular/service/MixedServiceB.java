package com.example.circular.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MixedServiceB {

    private MixedServiceA mixedServiceA;

    public MixedServiceB() {
        System.out.println("MixedServiceB 构造函数被调用");
    }

    @Autowired
    public void setMixedServiceA(MixedServiceA mixedServiceA) {
        System.out.println("MixedServiceB setter 被调用");
        this.mixedServiceA = mixedServiceA;
    }

    public String doSomethingElse() {
        return "MixedServiceB 调用 MixedServiceA: " + mixedServiceA.getName();
    }

    public String getName() {
        return "MixedServiceB";
    }
}
