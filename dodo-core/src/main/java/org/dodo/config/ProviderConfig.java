package org.dodo.config;

/**
 * 提供者服务全局配置，也是一个默认分组
 * @author maxlim
 *
 */
public class ProviderConfig extends ProviderServerConfig {
	private String reflect;
	private String attachment;
	
	public ProviderConfig() {
		super.setName("default");
		super.setDefault(true);
	}

	public ProviderConfig(String protocol, String ip, int port, int corePoolSize, int maxPoolSize, int workQueueSize,
						  int accepts, String serialization, String reflect, String attachment) {
		super("default", protocol, ip, port, corePoolSize, maxPoolSize, workQueueSize, accepts, serialization, true);
		super.setDefault(super.isVaild());

		this.reflect = reflect;
		this.attachment = attachment;
	}

	public String getReflect() {
		return reflect;
	}

	public void setReflect(String reflect) {
		this.reflect = reflect;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	@Override
	public String toString() {
		return "ProviderConfig{" +
				"reflect='" + reflect + '\'' +
				", attachment='" + attachment + '\'' +
				"} " + super.toString();
	}
}
