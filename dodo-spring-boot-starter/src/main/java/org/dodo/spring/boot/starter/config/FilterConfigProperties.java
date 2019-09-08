package org.dodo.spring.boot.starter.config;

import org.dodo.config.spring.bean.FiltersConfigBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author maxlim
 */
@ConfigurationProperties(prefix="dodo.filter")
public class FilterConfigProperties extends FiltersConfigBean {
}
