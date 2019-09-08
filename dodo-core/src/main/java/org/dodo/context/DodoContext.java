package org.dodo.context;

import org.dodo.common.spi.SpiLoader;
import org.dodo.config.ConfigManager;
import org.dodo.config.FiltersConfig;
import org.dodo.config.ReferenceConfig;
import org.dodo.consumer.reflect.ReflectFacade;
import org.dodo.provider.ExportFacade;
import org.dodo.provider.invoker.ServiceInvoker;
import org.dodo.register.discovery.Discovery;
import org.dodo.rpc.NodesManager;
import org.omg.SendingContext.RunTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * dodo框架级别的一些处理
 * @author maxlim
 *
 */
public class DodoContext {
    private final static Logger logger = LoggerFactory.getLogger(DodoContext.class);
    private static class Single {
        private static final DodoContext INSTANCE = new DodoContext();
    }
    public static DodoContext getContext() {
        return DodoContext.Single.INSTANCE;
    }

    public void onServerStart() throws Exception {
        if(ConfigManager.instance().getRegisterConfig() == null) {
            throw new NullPointerException("RegisterConfig is null");
        }
        if(ConfigManager.instance().getProviderConfig() == null) {
            throw new NullPointerException("ProviderConfig is null");
        }
        if(ConfigManager.instance().getServiceConfigs() == null || ConfigManager.instance().getServiceConfigs().isEmpty()) {
            throw new NullPointerException("ServiceConfig is null");
        }
        FiltersConfig filtersConfig = ConfigManager.instance().getFiltersConfig();
        if (filtersConfig == null) {
            filtersConfig = new FiltersConfig().setIncludesList("token-rate-limit,access-log,provider-trace").setParametersMap("token-rate-limit.capacity=10000;token-rate-limit.rate-per-second=10000");
            ConfigManager.instance().setFiltersConfig(filtersConfig);
        }
        ConfigManager.instance().getServiceConfigs().forEach((key, serviceConfig) ->  {
            try {
                ServiceInvoker serviceInvoker = SpiLoader.getExtensionHolder(ServiceInvoker.class).get(ConfigManager.instance().getProviderConfig().getReflect());
                serviceInvoker.register(serviceConfig.getInterfaceName(), serviceConfig.getRef());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        try {
            ExportFacade.export();
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    onServerClose();
                } catch (Throwable e) {
                    logger.warn(null, e);
                }
            }));
        }
    }

    public void onServerClose() {
        ExportFacade.unexport();
    }

    ///////////////////////////////////////////////////////////consumer////////////////////////////////////////////////////////////////////////////////////////////
    public void onConsumerStart() {
        if(ConfigManager.instance().getConsumerConfig() == null) {
            throw new NullPointerException("ConsumerConfig is null");
        }
        if(ConfigManager.instance().getRegisterConfig() == null) {
            throw new NullPointerException("RegisterConfig is null");
        }
        if(ConfigManager.instance().getReferenceConfigs() == null || ConfigManager.instance().getReferenceConfigs().isEmpty()) {
            throw new NullPointerException("ReferenceConfigs is null");
        }
        FiltersConfig filtersConfig = ConfigManager.instance().getFiltersConfig();
        if (filtersConfig == null) {
            filtersConfig = new FiltersConfig().setIncludesList("statistics,mock");
            ConfigManager.instance().setFiltersConfig(filtersConfig);
        }

        try {
            for(ReferenceConfig config: ConfigManager.instance().getReferenceConfigs().values()) {
                Discovery.instance().discover(config.getInterfaceName());
            }
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    onConsumerClose();
                } catch (Throwable e) {
                    logger.warn(null, e);
                }
            }));
        }
    }

    public void onConsumerClose() {
        try {
            Discovery.instance().close();
        } catch (Exception e) {
            logger.warn(null,e);
        }
        try {
            NodesManager.instance().close();
        } catch (Exception e) {
            logger.warn(null,e);
        }
    }

    public <T>T reflect(Class<T> clazz) throws Exception {
        return ReflectFacade.instance().reflect(clazz);
    }
}
