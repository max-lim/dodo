package org.dodo.example.javassist;

import java.lang.reflect.Method;

/**
 * @author maxlim
 */
public interface InterceptorHandler {
    public Object invoke(Object obj, Method method, Object[] args) throws Throwable;
}
