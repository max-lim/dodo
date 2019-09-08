package org.dodo.example.echo.impl;

import org.dodo.example.echo.EchoService;
import org.dodo.example.echo.UserBean;

public class EchoServiceImpl implements EchoService {

	@Override
	public String hello(String name) {
		System.out.println(System.currentTimeMillis() + " helooooooooooooo " + name);
//		if(name != null) throw new RuntimeException("abc");
		return "hello " + name;
	}

	@Override
	public UserBean withBean(UserBean bean) {
		bean.setName("return " + bean.getName());
		return bean;
	}

}
