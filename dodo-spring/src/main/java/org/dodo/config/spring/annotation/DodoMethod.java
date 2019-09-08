package org.dodo.config.spring.annotation;

import java.lang.annotation.*;

/**
 * reference函数配置
 * @author maxlim
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface DodoMethod {
    Class callback();
    String onResponse() default "";
    String onThrow() default "";
    String mock() default "";
    boolean forceMock() default false;
}
