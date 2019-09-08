package org.dodo.config;

/**
 * 注册中心配置
 * @author maxlim
 *
 */
public class RegisterConfig {
	private String name;
    private String address;
    private int connectTimeout;
    private int sessionTimeout;
    private int retryInterval;
    private int retryTimes;
    private String attachment;
    
	public RegisterConfig() {
		this(null, null, 30*1000, 60*1000, 5*1000, 30, null);
	}

	public RegisterConfig(String name, String address) {
		this(name, address, 30*1000, 60*1000, 5*1000, 30,null);
	}

	public RegisterConfig(String name, String address, int connectTimeout, int sessionTimeout, int retryInterval, int retryTimes,
						  String attachment) {
		this.name = name;
		this.address = address;
		this.connectTimeout = connectTimeout;
		this.sessionTimeout = sessionTimeout;
		this.retryInterval = retryInterval;
		this.retryTimes = retryTimes;
		this.attachment = attachment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public int getRetryInterval() {
		return retryInterval;
	}

	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return "RegisterConfig{" +
				"name='" + name + '\'' +
				", address='" + address + '\'' +
				", connectTimeout=" + connectTimeout +
				", sessionTimeout=" + sessionTimeout +
				", retryInterval=" + retryInterval +
				", retryTimes=" + retryTimes +
				", attachment='" + attachment + '\'' +
				'}';
	}
}
