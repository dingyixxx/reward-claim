package com.example.circular.controller;

import com.example.circular.service.FieldServiceA;
import com.example.circular.service.MixedServiceA;
import com.example.circular.service.ServiceA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class CircularDependencyController {

    @Autowired
    private ServiceA serviceA;

    @Autowired
    private FieldServiceA fieldServiceA;

    @Autowired
    private MixedServiceA mixedServiceA;

    @GetMapping("/constructor")
    public String testConstructorInjection() {
        try {
            return serviceA.doSomething();
        } catch (Exception e) {
            return "构造器注入循环依赖测试失败: " + e.getMessage();
        }
    }

    @GetMapping("/field")
    public String testFieldInjection() {
        try {
            return fieldServiceA.doSomething();
        } catch (Exception e) {
            return "字段注入循环依赖测试失败: " + e.getMessage();
        }
    }

    @GetMapping("/mixed")
    public String testMixedInjection() {
        try {
            return mixedServiceA.doSomething();
        } catch (Exception e) {
            return "混合注入循环依赖测试失败: " + e.getMessage();
        }
    }

    @GetMapping("/all")
    public String testAll() {
        StringBuilder result = new StringBuilder("循环依赖测试结果:\n");

        try {
            result.append("1. 构造器注入: ").append(serviceA.doSomething()).append("\n");
        } catch (Exception e) {
            result.append("1. 构造器注入失败: ").append(e.getMessage()).append("\n");
        }

        try {
            result.append("2. 字段注入: ").append(fieldServiceA.doSomething()).append("\n");
        } catch (Exception e) {
            result.append("2. 字段注入失败: ").append(e.getMessage()).append("\n");
        }

        try {
            result.append("3. 混合注入: ").append(mixedServiceA.doSomething()).append("\n");
        } catch (Exception e) {
            result.append("3. 混合注入失败: ").append(e.getMessage()).append("\n");
        }

        return result.toString();
    }
}
