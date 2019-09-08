package org.dodo.config.spring.bean;

import org.dodo.config.ConfigManager;
import org.dodo.config.ProviderServerConfig;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author maxlim
 */
public class ProviderServerConfigBean extends ProviderServerConfig implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        if ( ! this.isVaild()) {
            return;
        }
        ConfigManager.instance().addProviderServerConfig(this);
    }
}
