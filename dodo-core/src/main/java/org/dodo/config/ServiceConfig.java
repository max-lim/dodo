package org.dodo.config;

/**
 * 接口服务配置
 * @author maxlim
 *
 */
public class ServiceConfig {
	private String interfaceName;
	private Object ref;
	private int retry;
	private int timeout;
	private String group;

	public ServiceConfig() {

	}

	public ServiceConfig(Class<?> interfaceName, Object ref) {
		this(interfaceName.getName(), ref, 3, 1500, null);
	}

	public ServiceConfig(String interfaceName, Object ref, int retry, int timeout, String group) {
		this.interfaceName = interfaceName;
		this.ref = ref;
		this.retry = retry;
		this.timeout = timeout;
		this.group = group;
	}

	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public Object getRef() {
		return ref;
	}
	public void setRef(Object ref) {
		this.ref = ref;
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

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "ServiceConfig{" +
				"interfaceName='" + interfaceName + '\'' +
				", instance=" + ref +
				", retry=" + retry +
				", timeout=" + timeout +
				", group='" + group + '\'' +
				'}';
	}
}
