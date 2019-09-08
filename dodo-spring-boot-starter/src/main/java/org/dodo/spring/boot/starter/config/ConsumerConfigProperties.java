package org.dodo.spring.boot.starter.config;

import org.dodo.config.spring.bean.ConsumerConfigBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author maxlim
 */
@ConfigurationProperties(prefix="dodo.consumer")
public class ConsumerConfigProperties extends ConsumerConfigBean {
}
