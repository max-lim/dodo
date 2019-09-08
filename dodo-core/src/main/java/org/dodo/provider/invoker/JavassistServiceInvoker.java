package org.dodo.provider.invoker;

import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author maxlim
 */
public class JavassistServiceInvoker implements ServiceInvoker {
    private final static Logger logger = LoggerFactory.getLogger(JavassistServiceInvoker.class);
    private final Map<String, ServiceWrapper> servicesWrappers = new ConcurrentHashMap<>();

    @Override
    public Object invoke(String interfaceName, String methodName, Object[] args) throws Exception {
        ServiceWrapper serviceWrapper = servicesWrappers.get(interfaceName);
        if (serviceWrapper == null) {
            throw new IllegalArgumentException("instance of " + interfaceName + " is not defined");
        }
        return serviceWrapper.invoke(methodName, args);
    }

    @Override
    public void register(String interfaceName, Object instance) throws Exception {
        servicesWrappers.put(interfaceName, createWrapper(instance));
    }
    public interface ServiceWrapper {
        Object invoke(String methodName, Object[] args);
    }
    private ServiceWrapper createWrapper(Object instance) throws CannotCompileException, IllegalAccessException, InstantiationException, NotFoundException, NoSuchMethodException, InvocationTargetException {
        Class target = instance.getClass();
        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(target.getClassLoader()));

        String wrapperClassName = target.getName()+"$WrapperCreatedByJavassist";
        CtClass ctClassOfWrapperClass = pool.makeClass(wrapperClassName);

        CtClass ctClassOfInterface = pool.getCtClass(ServiceWrapper.class.getName());
        ctClassOfWrapperClass.addInterface(ctClassOfInterface);

        CtField ctFieldLogger = CtField.make("private final static org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger("+wrapperClassName+".class);\n", ctClassOfWrapperClass);
        ctClassOfWrapperClass.addField(ctFieldLogger);

        CtClass ctClassOfInstance = pool.get(instance.getClass().getName());
        CtField ctFieldService = new CtField(ctClassOfInstance, "service", ctClassOfWrapperClass);
        ctClassOfWrapperClass.addField(ctFieldService);

        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{ctClassOfInstance}, ctClassOfWrapperClass);
        ctConstructor.setBody("this.service=$1;");
        ctClassOfWrapperClass.addConstructor(ctConstructor);

        CtMethod ctMethod = CtMethod.make(makeMethodCode(instance), ctClassOfWrapperClass);
        ctClassOfWrapperClass.addMethod(ctMethod);

        Class classOfProxy = ctClassOfWrapperClass.toClass(target.getClassLoader(), null);
        return (ServiceWrapper) classOfProxy.getConstructor(instance.getClass()).newInstance(instance);
    }

    public String makeMethodCode(Object instance) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\npublic Object invoke(String methodName, Object[]args) {\n");
        stringBuilder.append("switch(methodName) {\n");
        Method[] methods = instance.getClass().getMethods();
        for (Method method : methods) {
            if(method.getName().equals("wait")
                    || method.getName().equals("equals")
                    || method.getName().equals("toString")
                    || method.getName().equals("hashCode")
                    || method.getName().equals("getClass")
                    || method.getName().equals("notify")
                    || method.getName().equals("notifyAll")) {
                continue;
            }
            stringBuilder.append("case \"").append(method.getName()).append("\": {\n");
            if(method.getReturnType() != void.class) {
                stringBuilder.append("return ");
            }
            stringBuilder.append("this.service.").append(method.getName()).append("(");
            if(method.getParameterCount() > 0) {
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    stringBuilder.append("(").append(parameters[i].getType().getName()).append(")args[").append(i).append("]");
                    if(i < parameters.length - 1) stringBuilder.append(",");
                }
            }
            stringBuilder.append(");\n");
            if(method.getReturnType() == void.class) {
                stringBuilder.append("break;\n");
            }
            stringBuilder.append("}\n");
        }
        stringBuilder.append("default:\n");
        stringBuilder.append("throw new IllegalArgumentException(\"method \" + methodName + \" is not found\");\n");
        stringBuilder.append("}\n");
        stringBuilder.append("return null;\n}");
//        if(logger.isDebugEnabled()) {
//            logger.debug(stringBuilder.toString());
//        }
        return stringBuilder.toString();
    }
}
