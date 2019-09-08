package org.dodo.config.spring.bean;

import org.dodo.config.ConfigManager;
import org.dodo.config.RegisterConfig;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author maxlim
 */
public class RegisterConfigBean extends RegisterConfig implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigManager.instance().setRegisterConfig(this);
    }
}
