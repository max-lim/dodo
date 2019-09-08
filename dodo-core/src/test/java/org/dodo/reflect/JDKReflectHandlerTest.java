package org.dodo.reflect;

import org.dodo.consumer.reflect.JDKReflectHandler;

import java.lang.reflect.Method;

/**
 * @author maxlim
 */
public class JDKReflectHandlerTest extends JDKReflectHandler {

    public Object invoke(Class<?> clazz, Method method, Object[] args) throws Exception {

        return "hello,i am jdk InvocationHandler";
    }
}