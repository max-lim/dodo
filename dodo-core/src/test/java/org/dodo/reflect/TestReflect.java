package org.dodo.reflect;

import org.dodo.consumer.reflect.JDKReflectHandler;
import org.dodo.consumer.reflect.JavassistReflectHandler;
import org.junit.Test;

public class TestReflect {
	private int createCnt = 1000;
	private int cnt = 1000;
	@Test
	public void testJavassistReflectProxy() throws Exception {
		JavassistReflectHandler javassistReflectHandler = new JavassistReflectHandlerTest();
		EchoService echoService = null;

		{
			long start = System.nanoTime();
			for (int i = 0; i < createCnt; i++) {
				echoService = javassistReflectHandler.proxy(EchoService.class);
			}
			System.out.println("javassist create proxy use time:" + (System.nanoTime() - start)/1000/1000);
		}

		System.out.println(echoService.hello("max.lim"));

		{
			long start = System.nanoTime();
			for (int i = 0; i < cnt; i++) {
				echoService.hello("max.lim");
			}
			System.out.println("javassist use time:" + (System.nanoTime() - start) / 1000);
		}
	}

	@Test
	public void testJdkReflectProxy() {
		JDKReflectHandler jdkReflectHandler = new JDKReflectHandlerTest();
		EchoService echoService = null;
		{
			long start = System.nanoTime();
			for (int i = 0; i < createCnt; i++) {
				echoService = jdkReflectHandler.proxy(EchoService.class);
			}
			System.out.println("jdk create proxy use time:" + (System.nanoTime() - start) / 1000 / 1000);
		}

		System.out.println(echoService.hello("max.lim"));

		{
			long start = System.nanoTime();
			for (int i = 0; i < cnt; i++) {
				echoService.hello("max.lim");
			}
			System.out.println("jdk use time:" + (System.nanoTime() - start) / 1000);
		}
	}

}
