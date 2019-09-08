package org.dodo.provider;

import org.dodo.common.spi.SpiLoader;
import org.dodo.config.ConfigManager;
import org.dodo.config.ProviderServerConfig;
import org.dodo.config.ServiceConfig;
import org.dodo.provider.server.Server;
import org.dodo.register.RegisterException;
import org.dodo.register.URL;
import org.dodo.register.center.Register;
import org.dodo.rpc.serialize.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务暴露门面：服务端口暴露，服务注册暴露
 * @author maxlim
 */
public class ExportFacade {
    private final static Logger logger = LoggerFactory.getLogger(ExportFacade.class);

    private static void buildServers() throws Exception {
        int vaildServerCount = 0;
        for (ProviderServerConfig providerServerConfig: ConfigManager.instance().getProviderServerConfigs().values()) {
            if(providerServerConfig.isVaild()) {
                Server server = SpiLoader.getExtensionHolder(Server.class).get(providerServerConfig.getProtocol());
                Server serverNew = server.getClass().newInstance();
                build(serverNew, providerServerConfig);
                vaildServerCount ++;
            }
        }
        if(ConfigManager.instance().getProviderConfig().isVaild()) {
            Server server = SpiLoader.getExtensionHolder(Server.class).get(ConfigManager.instance().getProviderConfig().getProtocol());
            Server serverNew = server.getClass().newInstance();
            build(serverNew, ConfigManager.instance().getProviderConfig());
            vaildServerCount ++;
        }
        if(vaildServerCount == 0) {
            throw new IllegalArgumentException("has not vaild provider server configuration be found!");
        }
    }
    private static Server build(Server server, ProviderServerConfig providerServerConfig) {
        server.export(providerServerConfig.getName(), providerServerConfig.getProtocol(), providerServerConfig.getIp(), providerServerConfig.getPort(),
                providerServerConfig.getCorePoolSize(), providerServerConfig.getMaxPoolSize(), providerServerConfig.getWorkQueueSize(), providerServerConfig.getAccepts(),
                SpiLoader.getExtensionHolder(Serialization.class).get(providerServerConfig.getSerialization()));
        logger.info("server {} listening {}:{}", providerServerConfig.getName(), providerServerConfig.getIp(), providerServerConfig.getPort());
        return server;
    }

    /**
     * 暴露service
     */
    public static void export() throws Exception {
        //export provider servers
        buildServers();

        //export service to registery
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("app", ConfigManager.instance().getAppConfig().getName());
        properties.put("attachment", ConfigManager.instance().getProviderConfig().getAttachment());
        for(ServiceConfig serviceConfig: ConfigManager.instance().getServiceConfigs().values()) {
            properties.put("retry", String.valueOf(serviceConfig.getRetry()));
            properties.put("timeout", String.valueOf(serviceConfig.getTimeout()));

            ProviderServerConfig providerServerConfig = ConfigManager.instance().getProviderServerConfig(serviceConfig.getGroup());

            URL url = new URL(providerServerConfig.getProtocol(),
                    ConfigManager.instance().getProviderConfig().getIp(),
                    ConfigManager.instance().getProviderConfig().getPort(),
                    serviceConfig.getInterfaceName(), properties);

            register(url);
        }
    }

    /**
     * 撤销service暴露
     * @throws RegisterException
     */
    public static void unexport() {
        //unregister services
        for(ServiceConfig serviceConfig: ConfigManager.instance().getServiceConfigs().values()) {

            ProviderServerConfig providerServerConfig = ConfigManager.instance().getProviderServerConfig(serviceConfig.getGroup());

            URL url = new URL(providerServerConfig.getProtocol(),
                    ConfigManager.instance().getProviderConfig().getIp(),
                    ConfigManager.instance().getProviderConfig().getPort(),
                    serviceConfig.getInterfaceName(), null);

            unregister(url);
        }

        //close servers
        SpiLoader.getExtensionHolder(Server.class).getAll().forEach(server -> {
            try {
                server.close();
            } catch (IOException e) {
            }
        });
    }
    /**
     * 注册暴露接口
     * @param url
     * @throws RegisterException
     */
    public static void register(URL url) throws RegisterException {
        try {
            Register register = SpiLoader.getExtensionHolder(Register.class).get(ConfigManager.instance().getRegisterConfig().getName());
            register.register(url);
        } catch (Exception e) {
            throw new RegisterException(url.toString()+" register fail", e);
        }
    }

    /**
     * 撤销注册暴露接口
     * @param url
     * @throws RegisterException
     */
    public static void unregister(URL url) {
        try {
            Register register = SpiLoader.getExtensionHolder(Register.class).get(ConfigManager.instance().getRegisterConfig().getName());
            register.unregister(url);
        } catch (Exception e) {
            logger.warn(url.toString()+" unregister fail", e);
        }
    }

}
