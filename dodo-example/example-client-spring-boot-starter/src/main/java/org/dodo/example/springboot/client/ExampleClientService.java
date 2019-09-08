package org.dodo.example.springboot.client;

import org.dodo.config.spring.annotation.DodoReference;
import org.dodo.example.springboot.ExampleService;
import org.springframework.stereotype.Service;

/**
 * @author maxlim
 */
@Service
public class ExampleClientService {
    @DodoReference
    ExampleService exampleService;
}
