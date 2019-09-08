package org.dodo.config;

/**
 * 应用配置
 * @author maxlim
 *
 */
public class AppConfig {
	private String name;

	public AppConfig() {
		
	}

	public AppConfig(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "AppConfig{" +
				"name='" + name + '\'' +
				'}';
	}
}
