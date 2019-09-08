package org.dodo.example.springboot.server;

import org.dodo.config.spring.annotation.DodoService;
import org.dodo.example.springboot.EchoService;
import org.springframework.stereotype.Component;

/**
 * @author maxlim
 */
@Component
@DodoService(interfaceClass = EchoService.class)
public class EchoServiceImpl implements EchoService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }

    @Override
    public String async(String name) {
        return "hello "+ name + ". i am async method";
    }
}
