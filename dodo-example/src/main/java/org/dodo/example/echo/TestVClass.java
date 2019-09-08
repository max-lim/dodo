package org.dodo.example.echo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author lin.bingzhong
 */
public class TestVClass<K, V> {

    /**
     * 获取V.class
     * @return
     */
    public Class<V> vClass() {
        Type type = getClass().getGenericSuperclass();
        Class<V> result = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            result = (Class<V>) pType.getActualTypeArguments()[0];
        }
        if(result == Object.class) {
            throw new IllegalArgumentException("ActualTypeArguments V cannot be set 'Object'");
        }
        return result;
    }

    public static void main(String args[]) {
        TestVClass testVClass = new TestVClass();
        testVClass.vClass();
        System.out.println(testVClass.vClass());
    }
}
