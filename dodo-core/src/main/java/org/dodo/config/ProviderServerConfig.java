package org.dodo.config;

import org.dodo.common.utils.StringUtils;

/**
 * 提供者服务（分组）配置
 * @author maxlim
 *
 */
public class ProviderServerConfig {
    private String name;
    private String protocol = "dodo";
    private String ip;
    private int port;
    private int corePoolSize;
    private int maxPoolSize;
    private int workQueueSize;
    private int accepts;
    private String serialization;
    private boolean isDefault;

    public ProviderServerConfig() {
        accepts = 10000;
        isDefault = true;
    }

    public ProviderServerConfig(String name, String protocol, String ip, int port, int corePoolSize, int maxPoolSize, int workQueueSize, int accepts, String serialization, boolean isDefault) {
        this.name = name;
        this.protocol = StringUtils.isBlank(protocol) ? "dodo" : protocol;
        this.ip = ip;
        this.port = port;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.workQueueSize = workQueueSize;
        this.accepts = accepts <= 0 ? 100000 : accepts;
        this.serialization = serialization;
        this.isDefault = isDefault;
    }

    public ProviderServerConfig(String name, String protocol, String ip, int port, int corePoolSize, int maxPoolSize, int workQueueSize, int accepts, String serialization) {
        this(name, protocol, ip, port, corePoolSize, maxPoolSize, workQueueSize, accepts, serialization, false);
    }

    public boolean isVaild() {
        return StringUtils.isNotBlank(ip) && port > 0 && maxPoolSize >= corePoolSize && corePoolSize > 0 && workQueueSize > 0 && accepts > 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getWorkQueueSize() {
        return workQueueSize;
    }

    public void setWorkQueueSize(int workQueueSize) {
        this.workQueueSize = workQueueSize;
    }

    public int getAccepts() {
        return accepts;
    }

    public void setAccepts(int accepts) {
        this.accepts = accepts;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String toString() {
        return "ProviderGroupConfig{" +
                "name='" + name + '\'' +
                ", protocol='" + protocol + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", corePoolSize=" + corePoolSize +
                ", maxPoolSize=" + maxPoolSize +
                ", workQueueSize=" + workQueueSize +
                ", accepts=" + accepts +
                ", serialization='" + serialization + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}
