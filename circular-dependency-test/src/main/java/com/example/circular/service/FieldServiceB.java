package com.example.circular.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FieldServiceB {

    @Autowired
    private FieldServiceA fieldServiceA;

    public FieldServiceB() {
        System.out.println("FieldServiceB 构造函数被调用");
    }

    public String doSomethingElse() {
        return "FieldServiceB 调用 FieldServiceA: " + fieldServiceA.getName();
    }

    public String getName() {
        return "FieldServiceB";
    }
}
