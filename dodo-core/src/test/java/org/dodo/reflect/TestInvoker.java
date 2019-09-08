package org.dodo.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author maxlim
 */
public class TestInvoker {
    int cnt = 10000;
    @org.junit.Test
    public void invoke() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Test test = new Test();
        long start = System.nanoTime();
        for (int i = 0; i < cnt; i++) {
            Method method = Test.class.getMethod("test");
            method.invoke(test);
        }
        System.out.println("invoke use time:" + (System.nanoTime() - start)/1000);
    }

    @org.junit.Test
    public void test() {
        Test test = new Test();
        long start = System.nanoTime();
        for (int i = 0; i < cnt; i++) {
            test.test();
        }
        System.out.println("test use time: "+(System.nanoTime() - start)/1000);
    }
    public static class Test {
        public void test() {

        }
    }
}
