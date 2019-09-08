package org.dodo.example.springboot.server;

import org.dodo.config.spring.bean.AppConfigBean;
import org.dodo.config.spring.bean.ProviderConfigBean;
import org.dodo.config.spring.bean.RegisterConfigBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author maxlim
 */
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(value = "org.dodo.example.springboot.server")
public class DodoSpringBootServer {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DodoSpringBootServer.class, args);
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
    public ProviderConfigBean providerConfigBean() {
        ProviderConfigBean providerConfigBean = new ProviderConfigBean();
        providerConfigBean.setIp("127.0.0.1");
        providerConfigBean.setPort(9001);
        providerConfigBean.setCorePoolSize(100);
        providerConfigBean.setMaxPoolSize(1000);
        providerConfigBean.setWorkQueueSize(1000);
        providerConfigBean.setAccepts(10000);
        providerConfigBean.setDefault(true);
        return providerConfigBean;
    }


}
