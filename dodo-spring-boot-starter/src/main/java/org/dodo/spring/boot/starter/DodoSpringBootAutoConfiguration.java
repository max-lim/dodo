package org.dodo.spring.boot.starter;

import org.dodo.spring.boot.starter.config.*;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author maxlim
 */
@Configurable
@ConditionalOnProperty(name = "dodo.app.name")
@EnableConfigurationProperties(value = {AppConfigProperties.class, FilterConfigProperties.class,
        RegisterConfigProperties.class, ProviderServerConfigProperties.class})
public class DodoSpringBootAutoConfiguration {

    @Bean
    @ConditionalOnClass(value = RegisterConfigProperties.class)
    @ConditionalOnProperty(name = "dodo.provider.name")
    ProviderConfigProperties providerConfigProperties() {
        return new ProviderConfigProperties();
    }

    @Bean
    @ConditionalOnClass(value = RegisterConfigProperties.class)
    @ConditionalOnProperty(name = "dodo.consumer.name")
    ConsumerConfigProperties consumerConfigProperties() {
        return new ConsumerConfigProperties();
    }

}
