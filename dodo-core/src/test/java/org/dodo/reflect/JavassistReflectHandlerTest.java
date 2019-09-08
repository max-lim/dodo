package org.dodo.reflect;

import org.dodo.consumer.reflect.JavassistReflectHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author maxlim
 */
public class JavassistReflectHandlerTest extends JavassistReflectHandler {
    public String makeMethodBodyCode(Class target, Method method, Parameter[] parameters) {
        return "return \"hello,i am javassist\";";
    }
}
