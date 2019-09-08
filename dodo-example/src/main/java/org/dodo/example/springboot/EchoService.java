package org.dodo.example.springboot;

import org.dodo.config.spring.annotation.DodoMethod;
import org.dodo.config.spring.annotation.DodoReferenceConfig;
import org.dodo.example.springboot.consumer.AsyncAndMockCallback;

/**
 * @author maxlim
 */
@DodoReferenceConfig(cluster = "failfast", loadBalance = "random")
public interface EchoService {
    @DodoMethod(callback = AsyncAndMockCallback.class, mock = "onHelloMock", forceMock = true)
    public String hello(String name);

    @DodoMethod(callback = AsyncAndMockCallback.class, onResponse = "response", onThrow = "onThrow")
    public String async(String name);
}
