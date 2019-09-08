package org.dodo.config;

import org.dodo.common.spi.SpiLoader;
import org.dodo.consumer.reflect.ReflectFacade;
import org.dodo.provider.invoker.ServiceInvoker;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配置管理
 * @author maxlim
 *
 */
public class ConfigManager {
	private ConfigManager() {}
	private static class Single {
		private static final ConfigManager INSTANCE = new ConfigManager();
	}
	public static ConfigManager instance() {
		return Single.INSTANCE;
	}
	//global
	private AppConfig appConfig;
    private RegisterConfig registerConfig;
    private FiltersConfig filtersConfig;
    //for provider
    private ProviderConfig providerConfig;
    private Map<String, ProviderServerConfig> providerServerConfigs = new ConcurrentHashMap<>();
    private Map<String, ServiceConfig> serviceConfigs = new ConcurrentHashMap<>();
    //for consumer
    private ConsumerConfig consumerConfig;
    private Map<String, ReferenceConfig> referenceConfigs = new ConcurrentHashMap<>();

    //setter getter
    public AppConfig getAppConfig() {
		return appConfig;
	}

	public ConfigManager setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
		return this;
	}

	public RegisterConfig getRegisterConfig() {
		return registerConfig;
	}

	public ConfigManager setRegisterConfig(RegisterConfig registerConfig) {
		this.registerConfig = registerConfig;
		return this;
	}

	public FiltersConfig getFiltersConfig() {
		return filtersConfig;
	}

	public ConfigManager setFiltersConfig(FiltersConfig filtersConfig) {
		this.filtersConfig = filtersConfig;
		return this;
	}

	public ProviderConfig getProviderConfig() {
        return providerConfig;
    }

    public ConfigManager setProviderConfig(ProviderConfig providerConfig) {
        this.providerConfig = providerConfig;
        return this;
    }

    public void addProviderServerConfig(ProviderServerConfig providerServerConfig) {
    	this.providerServerConfigs.put(providerServerConfig.getName(), providerServerConfig);
	}

	public Map<String, ProviderServerConfig> getProviderServerConfigs() {
		return Collections.unmodifiableMap(this.providerServerConfigs);
	}

	public ProviderServerConfig getProviderServerConfig(String name) {
    	if(name == null || ! this.providerServerConfigs.containsKey(name)) {
    		return this.providerConfig;
		}
		return this.providerServerConfigs.get(name);
	}
    
    public ConfigManager addServiceConfig(ServiceConfig serviceConfig) {
    	this.serviceConfigs.put(serviceConfig.getInterfaceName(), serviceConfig);
		return this;
    }
    
    public ServiceConfig getServiceConfig(String interfaceName) {
    	return this.serviceConfigs.get(interfaceName);
    }

	public Map<String, ServiceConfig> getServiceConfigs() {
		return Collections.unmodifiableMap(this.serviceConfigs);
	}

    public ConsumerConfig getConsumerConfig() {
		return consumerConfig;
	}
    
	public ConfigManager setConsumerConfig(ConsumerConfig consumerConfig) {
		this.consumerConfig = consumerConfig;
		return this;
	}
	
	public ConfigManager addReferenceConfig(ReferenceConfig referenceConfig) throws Exception {
    	this.referenceConfigs.put(referenceConfig.getInterfaceName(), referenceConfig);
		//启动预热
		ReflectFacade.instance().reflect(referenceConfig.getInterfaceClass());
		return this;
    }
    
    public ReferenceConfig getReferenceConfig(String interfaceName) {
    	return this.referenceConfigs.get(interfaceName);
    }

    public Map<String, ReferenceConfig> getReferenceConfigs() {
		return Collections.unmodifiableMap(this.referenceConfigs);
	}
}
