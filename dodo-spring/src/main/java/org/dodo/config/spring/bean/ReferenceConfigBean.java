package org.dodo.config.spring.bean;

import org.dodo.config.ConfigManager;
import org.dodo.config.MethodConfig;
import org.dodo.config.ReferenceConfig;
import org.dodo.consumer.reflect.ReflectFacade;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author maxlim
 */
public class ReferenceConfigBean extends ReferenceConfig implements InitializingBean, FactoryBean {
    private String name;
    private List<MethodConfig> methods;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(name)) {
            return;
        }
        super.setInterfaceName(name);
        if (methods != null && ! methods.isEmpty()) {
            methods.forEach(methodConfig -> super.addMethodConfig(methodConfig));
        }
        ConfigManager.instance().addReferenceConfig(this);
    }

    @Override
    public Object getObject() throws Exception {
        return ReflectFacade.instance().reflect(this.getInterfaceClass());
    }

    @Override
    public Class<?> getObjectType() {
        return this.getInterfaceClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MethodConfig> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodConfig> methods) {
        this.methods = methods;
    }
}
