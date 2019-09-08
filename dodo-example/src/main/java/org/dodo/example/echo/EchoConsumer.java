package org.dodo.example.echo;

import org.dodo.ConsumerBuilder;
import org.dodo.config.AppConfig;
import org.dodo.config.ConsumerConfig;
import org.dodo.config.ReferenceConfig;
import org.dodo.config.RegisterConfig;

import java.util.Arrays;
import java.util.Date;

public class EchoConsumer {

	public static void main(String []args) throws Exception {
		ConsumerBuilder builder = new ConsumerBuilder();
		builder.setAppConfig(new AppConfig("echo"))
			.setConsumerConfig(new ConsumerConfig("javassist","protostuff"))
			.setRegisterConfig(new RegisterConfig("local4test","127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183"))
			.addReferenceConfig(new ReferenceConfig(EchoService.class))
			;
		builder.build();
		Thread.sleep(5 * 1000);
		EchoService echoService = builder.reflect(EchoService.class);



		UserBean bean = new UserBean();
		bean.setAge(1);
		bean.setEmail("linbzh@163.com");
		bean.setId(8);
		bean.setItems(Arrays.asList(new UserItemBean(8, 1, 88, new Date(), new Date()),
				new UserItemBean(8, 2, 88, new Date(), new Date()),
				new UserItemBean(8, 3, 88, new Date(), new Date()),
				new UserItemBean(8, 4, 88, new Date(), new Date()),
				new UserItemBean(8, 5, 88, new Date(), new Date())));
		bean.setName("maxlim");
		bean.setPhone("13537567813");
		bean.setScore(88888888);
		bean.setSex(1);
		int i = 0;
		while(true) {
			long start = System.nanoTime();
			try {
				UserBean res = echoService.withBean(bean);
				System.out.println(System.currentTimeMillis() + " "+ res);
				System.out.println("use time:" + (System.nanoTime() - start)/1000000);
				Thread.sleep(3*1000L);
			}catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}
}
