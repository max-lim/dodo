package org.dodo.example.javassist;

import javassist.*;

import java.lang.reflect.Method;

/**
 * @author maxlim
 */
public class MyProxyImpl {
    /** 动态代理类的类名后缀 */
    private final static String PROXY_CLASS_NAME_SUFFIX = "$MyProxy_";
    /** 拦截器接口 */
    private final static String INTERCEPTOR_HANDLER_INTERFACE = "org.dodo.example.javassist.InterceptorHandler";
    /** 动态代理类的类名索引，防止类名重复 */
    private static int proxyClassIndex = 1;

    public static Object newProxyInstance(Class interfaceClass, String classToProxy, Class interceptorHandlerImplClass) throws InstantiationException, IllegalAccessException, NotFoundException, CannotCompileException {
        return dynamicImplementsInterface(classToProxy, interfaceClass, interceptorHandlerImplClass);
    }

    /**
     * 动态实现要代理的接口
     * @param classToProxy String 要动态代理的接口的实现类的类名, e.g test.StudentInfoServiceImpl
     * @param interfaceClass Class 要动态代理的接口类, e.g test.StudentInfoService
     * @param interceptorHandlerImplClass Class 用户提供的拦截器接口的实现类
     * @return Object 返回某个接口的动态代理对象
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private static Object dynamicImplementsInterface(String classToProxy, Class interfaceClass, Class interceptorHandlerImplClass) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException {
        ClassPool cp = ClassPool.getDefault();
        String interfaceName = interfaceClass.getName();
        //动态指定代理类的类名
        String proxyClassName = interfaceName + PROXY_CLASS_NAME_SUFFIX + proxyClassIndex++;
        //要实现的接口的包名+接口名
        String interfaceNamePath = interfaceName;

        CtClass ctInterface = cp.getCtClass(interfaceNamePath);
        CtClass cc = cp.makeClass(proxyClassName);
        cc.addInterface(ctInterface);

        Method [] methods = interfaceClass.getMethods();
        for(int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            dynamicImplementsMethodsFromInterface(classToProxy, cc, method, interceptorHandlerImplClass, i);
        }
        return (Object)cc.toClass().newInstance();
    }

    /**
     * 动态实现接口里的方法
     * @param classToProxy String 要动态代理的接口的实现类的类名, e.g test.StudentInfoServiceImpl
     * @param implementer CtClass 动态代理类的包装
     * @param methodToImpl Method 动态代理类里面要实现的接口方法的包装
     * @param interceptorClass Class 用户提供的拦截器实现类
     * @param methodIndex int 要实现的方法的索引
     */
    private static void dynamicImplementsMethodsFromInterface(String classToProxy, CtClass implementer, Method methodToImpl, Class interceptorClass, int methodIndex) throws CannotCompileException {
        String methodCode = generateMethodCode(classToProxy, methodToImpl, interceptorClass, methodIndex);
        CtMethod cm = CtNewMethod.make(methodCode, implementer);
        implementer.addMethod(cm);
    }

    /**
     * 动态组装方法体，当然代理里面的方法实现并不是简单的方法拷贝，而是反射调用了拦截器里的invoke方法，并将接收到的参数进行传递
     * @param classToProxy String 要动态代理的接口的实现类的类名, e.g test.StudentInfoServiceImpl
     * @param methodToImpl Method 动态代理类里面要实现的接口方法的包装
     * @param interceptorClass Class 用户提供的拦截器实现类
     * @param methodIndex int 要实现的方法的索引
     * @return String 动态组装的方法的字符串
     */
    private static String generateMethodCode(String classToProxy, Method methodToImpl, Class interceptorClass, int methodIndex) {
        String methodName = methodToImpl.getName();
        String methodReturnType = methodToImpl.getReturnType().getName();
        Class []parameters = methodToImpl.getParameterTypes();
        Class []exceptionTypes = methodToImpl.getExceptionTypes();
        StringBuffer exceptionBuffer = new StringBuffer();
        //组装方法的Exception声明
        if(exceptionTypes.length > 0) exceptionBuffer.append(" throws ");
        for(int i = 0; i < exceptionTypes.length; i++) {
            if(i != exceptionTypes.length - 1) exceptionBuffer.append(exceptionTypes[i].getName()).append(",");
            else exceptionBuffer.append(exceptionTypes[i].getName());
        }
        StringBuffer parameterBuffer = new StringBuffer();
        //组装方法的参数列表
        for(int i = 0; i < parameters.length; i++) {
            Class parameter = parameters[i];
            String parameterType = parameter.getName();
            //动态指定方法参数的变量名
            String refName = "a" + i;
            if(i != parameters.length - 1) parameterBuffer.append(parameterType).append(" " + refName).append(",");
            else parameterBuffer.append(parameterType).append(" " + refName);
        }
        StringBuffer methodDeclare = new StringBuffer();
        //方法声明，由于是实现接口的方法，所以是public
        methodDeclare.append("public ").append(methodReturnType).append(" ").append(methodName).append("(").append(parameterBuffer).append(")").append(exceptionBuffer).append(" {\n");
        String interceptorImplName = interceptorClass.getName();
        //方法体
        methodDeclare.append(INTERCEPTOR_HANDLER_INTERFACE).append(" interceptor = new ").append(interceptorImplName).append("();\n");
        //反射调用用户的拦截器接口
        methodDeclare.append("Object returnObj = interceptor.invoke(Class.forName(\"" + classToProxy + "\").newInstance(), Class.forName(\"" + classToProxy + "\").getMethods()[" + methodIndex + "], ");
        //传递方法里的参数
        if(parameters.length > 0) methodDeclare.append("new Object[]{");
        for(int i = 0; i < parameters.length; i++) {
            //($w) converts from a primitive type to the corresponding wrapper type: e.g.
            //Integer i = ($w)5;
            if(i != parameters.length - 1) methodDeclare.append("($w)a" + i + ",");
            else methodDeclare.append("($w)a" + i);
        }
        if(parameters.length > 0) methodDeclare.append("});\n");
        else methodDeclare.append("null);\n");
        //对调用拦截器的返回值进行包装
        if(methodToImpl.getReturnType().isPrimitive()) {
            if(methodToImpl.getReturnType().equals(Boolean.TYPE)) methodDeclare.append("return ((Boolean)returnObj).booleanValue();\n");
            else if(methodToImpl.getReturnType().equals(Integer.TYPE)) methodDeclare.append("return ((Integer)returnObj).intValue();\n");
            else if(methodToImpl.getReturnType().equals(Long.TYPE)) methodDeclare.append("return ((Long)returnObj).longValue();\n");
            else if(methodToImpl.getReturnType().equals(Float.TYPE)) methodDeclare.append("return ((Float)returnObj).floatValue();\n");
            else if(methodToImpl.getReturnType().equals(Double.TYPE)) methodDeclare.append("return ((Double)returnObj).doubleValue();\n");
            else if(methodToImpl.getReturnType().equals(Character.TYPE)) methodDeclare.append("return ((Character)returnObj).charValue();\n");
            else if(methodToImpl.getReturnType().equals(Byte.TYPE)) methodDeclare.append("return ((Byte)returnObj).byteValue();\n");
            else if(methodToImpl.getReturnType().equals(Short.TYPE)) methodDeclare.append("return ((Short)returnObj).shortValue();\n");
        } else {
            methodDeclare.append("return (" + methodReturnType + ")returnObj;\n");
        }
        methodDeclare.append("}");
        System.out.println(methodDeclare.toString());
        return methodDeclare.toString();
    }
}
