package org.dodo.config.spring.bean;

import org.dodo.config.*;
import org.dodo.config.spring.annotation.DodoMethod;
import org.dodo.config.spring.annotation.DodoReference;
import org.dodo.config.spring.annotation.DodoReferenceConfig;
import org.dodo.config.spring.annotation.DodoService;
import org.dodo.consumer.reflect.ReflectFacade;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author maxlim
 */
public class AppConfigBean extends AppConfig implements InitializingBean, BeanFactoryPostProcessor, BeanPostProcessor, BeanFactoryAware, Ordered {
    private String packages;
    private String[] packagesArray;
    private BeanFactory beanFactory;
    @Override
    public void afterPropertiesSet() throws Exception {
        ConfigManager.instance().setAppConfig(this);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (StringUtils.isEmpty(packages)) {
            return;
        }
        if (beanFactory instanceof BeanDefinitionRegistry) {
            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) beanFactory, true);
            AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(DodoService.class);
            scanner.addIncludeFilter(annotationTypeFilter);
            scanner.scan(packagesArray);
        }
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ( ! isInsideMyPackages(bean.getClass())) {
            return bean;
        }
        Class clazz = originClazz(bean.getClass());
        Field[]fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (! field.isAccessible()) {
                field.setAccessible(true);
            }
            DodoReference dodoReference = field.getAnnotation(DodoReference.class);
            if (dodoReference != null) {
                try {
                    Object value = getReferenceProxy(field.getType());
                    field.set(bean, value);
                } catch (Exception e) {
                    throw new RuntimeException(beanName + " set field("+field.getName()+") value", e);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if ( ! isInsideMyPackages(bean.getClass())) {
            return bean;
        }
        Class clazz = originClazz(bean.getClass());
        DodoService dodoService = (DodoService) clazz.getAnnotation(DodoService.class);
        if (dodoService != null) {
            ServiceConfig serviceConfig = new ServiceConfig();
            serviceConfig.setGroup(dodoService.group());
            serviceConfig.setInterfaceName(dodoService.interfaceClass().getName());
            serviceConfig.setRef(bean);
            serviceConfig.setRetry(dodoService.retry());
            serviceConfig.setTimeout(dodoService.timeout());
            try {
                ConfigManager.instance().addServiceConfig(serviceConfig);
            } catch (Exception e) {
                throw new RuntimeException("init service config fail," + clazz.getName(), e);
            }
        }
        return bean;
    }

    private boolean isInsideMyPackages(Class clazz) {
        if (StringUtils.isEmpty(packages)) {
            return true;
        }
        Class originClazz = originClazz(clazz);
        for (String packageName : packagesArray) {
            if (originClazz.getPackage().getName().startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }
    private Class originClazz(Class clazz) {
        Class originClazz = clazz;
        if (AopUtils.isAopProxy(clazz)) {
            originClazz = AopUtils.getTargetClass(clazz);
        }
        return originClazz;
    }

    public Object getReferenceProxy(Class reference) throws Exception {
        ReferenceConfig referenceConfig = ConfigManager.instance().getReferenceConfig(reference.getName());
        if (referenceConfig == null) {
            DodoReferenceConfig dodoReferenceConfig = (DodoReferenceConfig) reference.getAnnotation(DodoReferenceConfig.class);
            referenceConfig = new ReferenceConfig();
            referenceConfig.setRetry(dodoReferenceConfig.retry());
            referenceConfig.setTimeout(dodoReferenceConfig.timeout());
            referenceConfig.setCluster(dodoReferenceConfig.cluster());
            referenceConfig.setLoadBalance(dodoReferenceConfig.loadBalance());
            referenceConfig.setLoadBalanceParameters(dodoReferenceConfig.loadBalanceParameters());
            referenceConfig.setInterfaceClass(reference);

            Method[] methods = reference.getMethods();
            for (Method method : methods) {
                DodoMethod dodoMethod = method.getAnnotation(DodoMethod.class);
                if (dodoMethod != null) {
                    MethodConfig methodConfig = new MethodConfig();
                    methodConfig.setName(method.getName());
                    methodConfig.setRef(beanFactory.getBean(dodoMethod.callback()));
                    methodConfig.setOnResponse(dodoMethod.onResponse());
                    methodConfig.setOnException(dodoMethod.onThrow());
                    methodConfig.setOnMock(dodoMethod.mock());
                    methodConfig.setForceMock(dodoMethod.forceMock());
                    referenceConfig.addMethodConfig(methodConfig);
                }
            }
            ConfigManager.instance().addReferenceConfig(referenceConfig);
        }
        return ReflectFacade.instance().reflect(referenceConfig.getInterfaceClass());
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
        this.packagesArray = packages != null ? packages.split("\\s*,\\s*") : null;
    }
}
