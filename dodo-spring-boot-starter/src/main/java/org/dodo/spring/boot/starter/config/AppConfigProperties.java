package org.dodo.spring.boot.starter.config;

import org.dodo.config.spring.bean.AppConfigBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author maxlim
 */
@ConfigurationProperties(prefix="dodo.app")
public class AppConfigProperties extends AppConfigBean {
}
