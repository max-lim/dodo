package org.dodo.config.spring.bean;

import org.dodo.config.ConfigManager;
import org.dodo.config.ServiceConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

/**
 * @author maxlim
 */
public class ServiceConfigBean extends ServiceConfig implements InitializingBean {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        super.setInterfaceName(name);
        ConfigManager.instance().addServiceConfig(this);
    }
}
