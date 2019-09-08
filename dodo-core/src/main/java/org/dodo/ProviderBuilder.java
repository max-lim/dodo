package org.dodo;

import org.dodo.common.spi.SpiLoader;
import org.dodo.config.*;
import org.dodo.context.DodoContext;
import org.dodo.provider.invoker.ServiceInvoker;

/**
 * 提供者服务构建器
 * @author maxlim
 *
 */
public class ProviderBuilder {
	public ProviderBuilder setAppConfig(AppConfig appConfig) {
		ConfigManager.instance().setAppConfig(appConfig);
		return this;
	}
	public ProviderBuilder setRegisterConfig(RegisterConfig registerConfig) {
		ConfigManager.instance().setRegisterConfig(registerConfig);
		return this;
	}
	public ProviderBuilder setProviderConfig(ProviderConfig providerConfig) {
		ConfigManager.instance().setProviderConfig(providerConfig);
		return this;
	}
	public ProviderBuilder addServiceConfig(ServiceConfig serviceConfig) throws Exception {
		ConfigManager.instance().addServiceConfig(serviceConfig);
		return this;
	}
	public ProviderBuilder setFiltersConfig(FiltersConfig filtersConfig) {
		ConfigManager.instance().setFiltersConfig(filtersConfig);
		return this;
	}
	public void build() throws Exception {
		DodoContext.getContext().onServerStart();
	}
}
