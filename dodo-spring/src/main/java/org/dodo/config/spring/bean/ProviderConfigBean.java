package org.dodo.config.spring.bean;

import org.dodo.config.ConfigManager;
import org.dodo.config.ProviderConfig;
import org.dodo.context.DodoContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @author maxlim
 */
public class ProviderConfigBean extends ProviderConfig implements InitializingBean, ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigManager.instance().setProviderConfig(this);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            DodoContext.getContext().onServerStart();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
