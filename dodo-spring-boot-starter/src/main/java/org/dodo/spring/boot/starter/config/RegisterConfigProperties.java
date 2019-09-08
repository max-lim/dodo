package org.dodo.spring.boot.starter.config;

import org.dodo.config.spring.bean.RegisterConfigBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author maxlim
 */
@ConfigurationProperties(prefix="dodo.register")
public class RegisterConfigProperties extends RegisterConfigBean {
}
