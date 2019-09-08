package org.dodo.spring.boot.starter.config;

import org.dodo.config.spring.bean.ProviderConfigBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author maxlim
 */
@ConfigurationProperties(prefix="dodo.provider")
public class ProviderConfigProperties extends ProviderConfigBean {
}
