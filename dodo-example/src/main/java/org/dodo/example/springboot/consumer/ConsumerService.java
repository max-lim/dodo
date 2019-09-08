package org.dodo.example.springboot.consumer;

import org.dodo.config.spring.annotation.DodoReference;
import org.dodo.example.springboot.EchoService;
import org.springframework.stereotype.Service;

/**
 * @author maxlim
 */
@Service
public class ConsumerService {
    @DodoReference
    private EchoService echoService;

    public String hello(String name) {
        return echoService.hello(name);
    }

    public void async(String name) {
        echoService.async(name);
    }
}
