package com.example.circular.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class FieldServiceA {

    @Autowired
//    @Lazy
    private FieldServiceB fieldServiceB;

    public FieldServiceA() {
        System.out.println("FieldServiceA 构造函数被调用");
    }

    public String doSomething() {
        return "FieldServiceA 调用 FieldServiceB: " + fieldServiceB.doSomethingElse();
    }

    public String getName() {
        return "FieldServiceA";
    }
}
