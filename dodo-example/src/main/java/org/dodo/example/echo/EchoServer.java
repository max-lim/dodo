package org.dodo.example.echo;

import org.dodo.ProviderBuilder;
import org.dodo.config.AppConfig;
import org.dodo.config.ProviderConfig;
import org.dodo.config.RegisterConfig;
import org.dodo.config.ServiceConfig;
import org.dodo.example.echo.impl.EchoServiceImpl;

public class EchoServer {

	public static void main(String []args) throws Exception {
		ProviderBuilder builder = new ProviderBuilder();
		builder.setAppConfig(new AppConfig("echo"))
				.setProviderConfig(new ProviderConfig("dodo","127.0.0.1",8000,16,16,100,10000,"protostuff",null,null))
				.setRegisterConfig(new RegisterConfig("local4test","127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183"))
				.addServiceConfig(new ServiceConfig(EchoService.class, new EchoServiceImpl()))
			;
		builder.build();
		
		while(true) {
			Thread.sleep(1000L);
		}
	}
}
