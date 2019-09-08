package org.dodo.config.spring.annotation;

import java.lang.annotation.*;

/**
 * provider service annotation
 * @author maxlim
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DodoService {
    Class<?> interfaceClass();
    int retry() default 3;
    int timeout() default 1500;
    String group() default "default";
}
