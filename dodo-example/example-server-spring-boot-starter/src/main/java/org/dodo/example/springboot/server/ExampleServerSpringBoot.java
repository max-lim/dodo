package org.dodo.example.springboot.server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author maxlim
 */
@SpringBootApplication
@ComponentScan(value = {"org.dodo.example.springboot"})
public class ExampleServerSpringBoot {

    public static void main(String[] args) {
        SpringApplication.run(ExampleServerSpringBoot.class, args);
    }

}
