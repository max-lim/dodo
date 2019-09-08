package org.dodo.common.spi;

import com.sun.tools.javac.code.TargetType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * spi单例模式
 * @author maxlim
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Deprecated
public @interface Single {
}
