package org.dodo.config.spring;

import org.dodo.config.spring.bean.*;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * spring xml自定义bean解析类注册
 * @author maxlim
 *
 */
public class NamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("app", new DodoBeanDefinitionParser(AppConfigBean.class));
        registerBeanDefinitionParser("register", new DodoBeanDefinitionParser(RegisterConfigBean.class));
        registerBeanDefinitionParser("consumer", new DodoBeanDefinitionParser(ConsumerConfigBean.class, false));
        registerBeanDefinitionParser("reference", new DodoBeanDefinitionParser(ReferenceConfigBean.class));
        registerBeanDefinitionParser("filters", new DodoBeanDefinitionParser(FiltersConfigBean.class));
        registerBeanDefinitionParser("provider", new DodoBeanDefinitionParser(ProviderConfigBean.class));
        registerBeanDefinitionParser("server", new DodoBeanDefinitionParser(ProviderServerConfigBean.class));
        registerBeanDefinitionParser("service", new DodoBeanDefinitionParser(ServiceConfigBean.class));
    }
}
