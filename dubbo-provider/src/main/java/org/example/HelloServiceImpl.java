package org.example;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@DubboService(version = "1.0.0",group = "dingyi")
@Component
public class HelloServiceImpl implements HelloService {

    public HelloServiceImpl() {
        System.out.println(">>> HelloServiceImpl constructed!");
    }
    @Override
    public String sayHello(String name) {
        System.out.println(">>> HelloServiceImpl.sayHello() invoked!");
        return "[Provider on port %d] Hello, %s!";
    }
}