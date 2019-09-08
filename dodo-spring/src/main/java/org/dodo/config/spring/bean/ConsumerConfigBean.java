package org.dodo.config.spring.bean;

import org.dodo.config.ConfigManager;
import org.dodo.config.ConsumerConfig;
import org.dodo.context.DodoContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

/**
 * @author maxlim
 */
public class ConsumerConfigBean extends ConsumerConfig implements InitializingBean, Ordered, ApplicationListener<ContextRefreshedEvent> {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigManager.instance().setConsumerConfig(this);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        DodoContext.getContext().onConsumerStart();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
