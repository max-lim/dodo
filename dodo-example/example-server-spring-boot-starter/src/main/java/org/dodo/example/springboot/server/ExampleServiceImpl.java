package org.dodo.example.springboot.server;

import org.dodo.config.spring.annotation.DodoService;
import org.dodo.example.springboot.ExampleService;
import org.springframework.stereotype.Component;

/**
 * @author maxlim
 */
@Component
@DodoService(interfaceClass = ExampleService.class)
public class ExampleServiceImpl implements ExampleService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
