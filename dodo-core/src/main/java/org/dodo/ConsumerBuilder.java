package org.dodo;

import org.dodo.config.*;
import org.dodo.context.DodoContext;
import org.dodo.consumer.reflect.ReflectFacade;
import org.dodo.register.discovery.DiscoveryException;

/**
 * 消费端构建器
 * @author maxlim
 *
 */
public class ConsumerBuilder {
	public ConsumerBuilder setAppConfig(AppConfig appConfig) {
		ConfigManager.instance().setAppConfig(appConfig);
		return this;
	}
	public ConsumerBuilder setConsumerConfig(ConsumerConfig consumerConfig) {
		ConfigManager.instance().setConsumerConfig(consumerConfig);
		return this;
	}
	public ConsumerBuilder setRegisterConfig(RegisterConfig registerConfig) {
		ConfigManager.instance().setRegisterConfig(registerConfig);
		return this;
	}
	public ConsumerBuilder addReferenceConfig(ReferenceConfig referenceConfig) throws Exception {
		ConfigManager.instance().addReferenceConfig(referenceConfig);
		//启动预热
		ReflectFacade.instance().reflect(Class.forName(referenceConfig.getInterfaceName()));
		return this;
	}
	public ConsumerBuilder setFiltersConfig(FiltersConfig filtersConfig) {
		ConfigManager.instance().setFiltersConfig(filtersConfig);
		return this;
	}
	public void build() throws DiscoveryException {
		DodoContext.getContext().onConsumerStart();
	}
	public <T>T reflect(Class<T> clazz) throws Exception {
		return ReflectFacade.instance().reflect(clazz);
	}

}
