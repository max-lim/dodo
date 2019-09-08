package org.dodo.config;

import org.dodo.common.utils.ClassUtil;
import org.dodo.common.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 消费端接口配置
 * @author maxlim
 *
 */
public class ReferenceConfig {
	private Class interfaceClass;
	private String interfaceName;
	private int retry;
	private int timeout;
	private String cluster;
	private String loadBalance;
	private String loadBalanceParameters;
	private Map<String, String> loadBalanceParametersMap;
	private Map<String, MethodConfig> methodConfigs;

	public ReferenceConfig() {
		this.methodConfigs = new HashMap<>();
	}

	public ReferenceConfig(Class<?> clazz) throws ClassNotFoundException {
		this(clazz.getName(), 3, 1500, null, null);
		this.interfaceClass = clazz;
	}

	public ReferenceConfig(Class<?> clazz, int retry, int timeout, String cluster, String loadBalance) throws ClassNotFoundException {
		this(clazz.getName(), retry, timeout, cluster, loadBalance);
		this.interfaceClass = clazz;
	}

	public ReferenceConfig(String interfaceName, int retry, int timeout, String cluster, String loadBalance) throws ClassNotFoundException {
		this.interfaceName = interfaceName;
		this.retry = retry;
		this.timeout = timeout;
		this.cluster = cluster;
		this.loadBalance = loadBalance;
		this.methodConfigs = new HashMap<>();
		this.interfaceClass = Class.forName(interfaceName);
	}

	public Class getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(Class interfaceClass) {
		this.interfaceName = interfaceClass.getName();
		this.interfaceClass = interfaceClass;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) throws ClassNotFoundException {
		this.interfaceName = interfaceName;
		this.interfaceClass = Class.forName(interfaceName);
	}
	
	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getLoadBalance() {
		return loadBalance;
	}

	public void setLoadBalance(String loadBalance) {
		this.loadBalance = loadBalance;
	}

	public String getLoadBalanceParameters() {
		return loadBalanceParameters;
	}

	public ReferenceConfig setLoadBalanceParameters(String loadBalanceParamters) {
		if (StringUtils.isBlank(loadBalanceParamters)) {
			return this;
		}
		this.loadBalanceParameters = loadBalanceParamters;
		Map<String, String> loadBalanceParametersMap = new HashMap<>();
		for (String loadBalanceParameter : loadBalanceParamters.split(";")) {
			String[] kv = loadBalanceParameter.trim().split("=");
			loadBalanceParametersMap.put(kv[0].trim(), kv[1].trim());
		}
		if (this.loadBalanceParametersMap == null) this.loadBalanceParametersMap = loadBalanceParametersMap;
		else this.loadBalanceParametersMap.putAll(loadBalanceParametersMap);
		return this;
	}

	public <T> T getLoadBalanceParameter(String key, T defaultValue, Class<T> returnType) {
		String value = this.loadBalanceParametersMap.get(key);
		if(value == null) return defaultValue;
		return ClassUtil.conversionStringType(value, returnType);
	}

	public Map<String, MethodConfig> getMethodConfigs() {
		return methodConfigs;
	}

	public void setMethodConfigs(Map<String, MethodConfig> methodConfigs) {
		this.methodConfigs = methodConfigs;
	}

	public void addMethodConfig(MethodConfig methodConfig) {
		this.methodConfigs.put(methodConfig.getName(), methodConfig);
	}

	public MethodConfig getMethodConfig(String name) {
		return this.methodConfigs.get(name);
	}

	@Override
	public String toString() {
		return "ReferenceConfig{" +
				"interfaceName='" + interfaceName + '\'' +
				", retry=" + retry +
				", timeout=" + timeout +
				", cluster='" + cluster + '\'' +
				", loadBalance='" + loadBalance + '\'' +
				", methodConfigs=" + methodConfigs +
				'}';
	}
}
