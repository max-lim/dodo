package org.dodo.provider.invoker;


import org.dodo.common.spi.Spi;

/**
 * 服务提供者调用器
 * @author maxlim
 *
 */
@Spi("default")
public interface Invoker extends org.dodo.consumer.invoker.Invoker {
}
