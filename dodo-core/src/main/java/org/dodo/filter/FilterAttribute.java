package org.dodo.filter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * filter注解属性
 * @author maxlim
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FilterAttribute {
    FilterGroup group();

    /**
     * 用来排序，值小排前面
     * @return
     */
    int order() default 0;
}
