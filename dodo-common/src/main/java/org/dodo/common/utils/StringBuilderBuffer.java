package org.dodo.common.utils;

/**
 * 本地线程string builder缓存，让char常驻内存，减少GC
 * @author maxlim
 *
 */
public class StringBuilderBuffer {
    private final static ThreadLocal<StringBuilder> THREAD_LOCAL = ThreadLocal.withInitial(()-> new StringBuilder());
    public static StringBuilder getStringBuilder() {
        StringBuilder stringBuilder = THREAD_LOCAL.get();
        stringBuilder.setLength(0);
        return stringBuilder;
    }
}
