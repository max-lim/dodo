package org.dodo.example.springboot.consumer;

import org.springframework.stereotype.Component;

/**
 * @author maxlim
 */
@Component
public class AsyncAndMockCallback {
    public String onHelloMock(String name) {
        return "hello " + name + ". i am mock";
    }

    public void response(String name) {
        System.out.println(name);
    }

    public void onThrow(Exception e) {
        e.printStackTrace();
    }
}
