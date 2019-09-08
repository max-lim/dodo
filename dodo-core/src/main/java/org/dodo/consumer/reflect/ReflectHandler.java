package org.dodo.consumer.reflect;

import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.dodo.common.spi.Spi;

import java.lang.reflect.InvocationTargetException;

/**
 * 反射代理接口
 * @author maxlim
 *
 */
@Spi("javassist")
public interface ReflectHandler {
	public <T> T proxy(Class<T> target) throws Exception;
}
