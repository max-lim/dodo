package org.dodo.config.spring.annotation;

import java.lang.annotation.*;

/**
 * @author maxlim
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DodoReferenceConfig {
    int retry() default 3;
    int timeout() default 1500;
    String cluster() default "";
    String loadBalance() default "";
    String loadBalanceParameters() default "";
}
