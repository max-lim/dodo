package org.dodo.example.javassist;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.dodo.example.echo.EchoService;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author maxlim
 */
public class TestProxy {
    public static class MyInterceptorHandler implements InterceptorHandler {
        @Override
        public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
            System.out.println(method.getName());
            System.out.println(Arrays.toString(args));
            return "hello " + args[0];
        }
    }
    public static void main(String[] args) throws CannotCompileException, InstantiationException, NotFoundException, IllegalAccessException {
//        TestProxyService service = (TestProxyService) MyProxyImpl.newProxyInstance(TestProxyService.class,
//                "org.dodo.example.javassist.TestProxyServiceImpl",
//                MyInterceptorHandler.class);
//        System.out.println(service.hello("max"));

        EchoService echoService = (EchoService) MyProxyImpl.newProxyInstance(EchoService.class,
                "org.dodo.example.echo.impl.EchoServiceImpl", MyInterceptorHandler.class);
        echoService.hello("max");
    }
}
