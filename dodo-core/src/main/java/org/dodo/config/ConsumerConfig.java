package org.dodo.config;

import org.dodo.common.utils.ClassUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 消费端全局配置
 * @author maxlim
 *
 */
public class ConsumerConfig {
	private String reflect;
	private String serialization;
	private String cluster;
	private String loadBalance;
	private String loadBalanceParameters;
	private Map<String, String> loadBalanceParametersMap = new HashMap<>();

	public final static String LOAD_BALANCE_WEIGHTED_RESPONSE_TIME_INTERVAL = "weight-response-time.interval";
	public final static String LOAD_BALANCE_CONSISTENT_HASHING_VIRTUAL = "consistent-hashing.virtual";

	public ConsumerConfig() {

	}

	public ConsumerConfig(String reflect) {
		this.reflect = reflect;
	}

	public ConsumerConfig(String reflect, String serialization) {
		this.reflect = reflect;
		this.serialization = serialization;
	}

	public ConsumerConfig(String reflect, String serialization, String cluster) {
		this.reflect = reflect;
		this.serialization = serialization;
		this.cluster = cluster;
	}

	public ConsumerConfig(String reflect, String serialization, String cluster, String loadBalance) {
		this.reflect = reflect;
		this.serialization = serialization;
		this.cluster = cluster;
		this.loadBalance = loadBalance;
	}

	public ConsumerConfig(String reflect, String serialization, String cluster, String loadBalance, String loadBalanceParameters) {
		this.reflect = reflect;
		this.serialization = serialization;
		this.loadBalance = loadBalance;
		this.cluster = cluster;
		this.setLoadBalanceParameters(loadBalanceParameters);
	}

	public String getReflect() {
		return reflect;
	}

	public void setReflect(String reflect) {
		this.reflect = reflect;
	}

	public String getSerialization() {
		return serialization;
	}

	public void setSerialization(String serialization) {
		this.serialization = serialization;
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

	public ConsumerConfig setLoadBalanceParameters(String loadBalanceParamters) {
		this.loadBalanceParameters = loadBalanceParamters;
		Map<String, String> loadBalanceParametersMap = new HashMap<>();
		for (String loadBalanceParameter : loadBalanceParamters.split(",")) {
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

	@Override
	public String toString() {
		return "ConsumerConfig{" +
				"reflect='" + reflect + '\'' +
				", serialization='" + serialization + '\'' +
				", cluster='" + cluster + '\'' +
				", loadBalance='" + loadBalance + '\'' +
				'}';
	}
}
