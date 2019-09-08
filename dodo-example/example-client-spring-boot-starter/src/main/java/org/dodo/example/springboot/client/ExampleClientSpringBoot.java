package org.dodo.example.springboot.client;
import org.dodo.example.springboot.ExampleService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author maxlim
 */
@SpringBootApplication
@ComponentScan(value = {"org.dodo.example.springboot"})
public class ExampleClientSpringBoot {

    public static void main(String[] args) throws InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(ExampleClientSpringBoot.class, args);

        while (true) {
            Thread.sleep(10000L);
            try {
                ExampleClientService exampleClientService = context.getBean(ExampleClientService.class);
                System.out.println(exampleClientService.exampleService.hello("maxlim"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
