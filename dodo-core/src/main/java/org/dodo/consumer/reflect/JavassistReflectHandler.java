package org.dodo.consumer.reflect;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * javassist代理
 * @author maxlim
 *
 */
public class JavassistReflectHandler implements ReflectHandler {
    private final static Logger logger = LoggerFactory.getLogger(JavassistReflectHandler.class);
    private AtomicInteger incr = new AtomicInteger();//for test
    @Override
    public <T> T proxy(Class<T> target) throws Exception {
        return createProxy(target);
    }

    private <T> T createProxy(Class<T> target) throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException, IOException {
        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(target.getClassLoader()));

//        String proxyClassName = target.getName()+"$ProxyCreatedByJavassist";
        String proxyClassName = target.getName()+"$ProxyCreatedByJavassist" + incr.getAndIncrement();//for test
        CtClass ctClassOfProxyClass = pool.makeClass(proxyClassName);

        CtClass ctClassOfInterface = pool.getCtClass(target.getName());
        ctClassOfProxyClass.addInterface(ctClassOfInterface);

        CtField ctField = CtField.make("private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("+proxyClassName+".class);\n", ctClassOfProxyClass);
        ctClassOfProxyClass.addField(ctField);

        ctClassOfProxyClass.addConstructor(CtNewConstructor.defaultConstructor(ctClassOfProxyClass));

        //使用getMethods，只针对public进行处理
        Method[] methods = target.getMethods();
        for (Method method : methods) {
            if(Modifier.isAbstract(method.getModifiers())) {
                CtMethod ctMethod = CtMethod.make(makeMethodCode(target, method), ctClassOfProxyClass);
                ctClassOfProxyClass.addMethod(ctMethod);
            }
        }
        Class classOfProxy = ctClassOfProxyClass.toClass(target.getClassLoader(), null);
        return (T) classOfProxy.newInstance();
    }
    public String makeMethodCode(Class target, Method method) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\npublic ").append(method.getReturnType().getName()).append(" ").append(method.getName()).append("(");
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            stringBuilder.append(parameters[i].getType().getName()).append(" ").append(parameters[i].getName());
            if(i < parameters.length - 1) stringBuilder.append(",");
        }
        stringBuilder.append(") {\n");
        stringBuilder.append(makeMethodBodyCode(target, method, parameters));
        stringBuilder.append("}");
        if(logger.isTraceEnabled()) {
            logger.trace(stringBuilder.toString());
        }
        return stringBuilder.toString();
    }
    public String makeMethodBodyCode(Class target, Method method, Parameter[] parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("if(logger.isTraceEnabled()){\n logger.trace(\"i am \" +this.getClass()+ \" make by javassist\");\n}\n")
                .append("org.dodo.config.ReferenceConfig referenceConfig = org.dodo.config.ConfigManager.instance().getReferenceConfig(\""+target.getName()+"\");\n")
                .append("org.dodo.consumer.invoker.InvokerRequest request = new org.dodo.consumer.invoker.InvokerRequest();\n")
                .append("request.setClassName(\"").append(target.getName()).append("\");\n")
                .append("request.setMethodName(\"").append(method.getName()).append("\");\n")
                .append("request.setReferenceConfig(referenceConfig);\n");

        if (parameters.length > 0) stringBuilder.append("request.setArgs(new Object[] {");
        for (int i = 0; i < parameters.length; i++) {
            stringBuilder.append("($w)").append(parameters[i].getName());
            if(i < parameters.length - 1) stringBuilder.append(",");
        }
        if (parameters.length > 0) stringBuilder.append("});\n");

        stringBuilder.append("java.lang.String cluster = (java.lang.String)java.util.Optional.ofNullable(referenceConfig.getCluster()).orElse(org.dodo.config.ConfigManager.instance().getConsumerConfig().getCluster());\n")
                .append("org.dodo.consumer.invoker.Invoker invoker = (org.dodo.consumer.invoker.Invoker)org.dodo.common.spi.SpiLoader.getExtensionHolder(org.dodo.consumer.invoker.Invoker.class).get(cluster);\n");
        if(method.getReturnType() == void.class) {
            stringBuilder.append("return invoker.invoke(request);\n");
        }
        else stringBuilder.append("return (").append(method.getReturnType().getName()).append(")invoker.invoke(request);\n");
        return stringBuilder.toString();
    }
}
