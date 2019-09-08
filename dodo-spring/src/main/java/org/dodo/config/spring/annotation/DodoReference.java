package org.dodo.config.spring.annotation;

import java.lang.annotation.*;

/**
 * consumer reference annotation
 * @author maxlim
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DodoReference {
}
