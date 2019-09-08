package org.dodo.example.springboot.consumer;

import org.dodo.config.spring.bean.AppConfigBean;
import org.dodo.config.spring.bean.ConsumerConfigBean;
import org.dodo.config.spring.bean.RegisterConfigBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;

/**
 * @author maxlim
 */
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(value = {"org.dodo.example.springboot.consumer"})
public class DodoSpringBootConsumer {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DodoSpringBootConsumer.class, args);

        while (true) {
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ConsumerService consumerService = context.getBean(ConsumerService.class);
            System.out.println(consumerService.hello("maxlim"));
            consumerService.async("maxlim");
        }
    }

    @Bean
    public AppConfigBean appConfigBean() {
        AppConfigBean appConfigBean = new AppConfigBean();
        appConfigBean.setName("echo");
        appConfigBean.setPackages("org.dodo.example.springboot");
        return appConfigBean;
    }

    @Bean
    public RegisterConfigBean registerConfigBean() {
        RegisterConfigBean registerConfigBean = new RegisterConfigBean();
        registerConfigBean.setAddress("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183");
        registerConfigBean.setConnectTimeout(30 * 1000);
        registerConfigBean.setName("zookeeper");
        registerConfigBean.setRetryInterval(500);
        registerConfigBean.setRetryTimes(25);
        registerConfigBean.setSessionTimeout(120 * 1000);
        return registerConfigBean;
    }

    @Bean
    public ConsumerConfigBean consumerConfigBean() {
        ConsumerConfigBean consumerConfigBean = new ConsumerConfigBean();
        consumerConfigBean.setName("consumer");
        consumerConfigBean.setCluster("failover");
        consumerConfigBean.setLoadBalance("random");
        return consumerConfigBean;
    }

}
