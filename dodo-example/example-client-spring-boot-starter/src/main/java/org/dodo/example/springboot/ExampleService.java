package org.dodo.example.springboot;

import org.dodo.config.spring.annotation.DodoReference;
import org.dodo.config.spring.annotation.DodoReferenceConfig;

/**
 * @author maxlim
 */
@DodoReferenceConfig
public interface ExampleService {
    public String hello(String name);
}
