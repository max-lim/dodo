package org.dodo.reflect;


import org.dodo.rpc.serialize.UserBean;

public class EchoServiceImpl implements EchoService {

	@Override
	public String hello(String name) {
//		if(name != null) throw new RuntimeException("abc");
		return "hello " + name;
	}

	@Override
	public UserBean withBean(UserBean userBean) {
		UserBean res = userBean;
		res.setEmail("linbzh@163.com");
		res.setScore(8888888);
		res.setOther("nothing");
		return res;
	}

	@Override
	public void returnVoid() {
		System.out.println("returnVoid invoked");
	}
}
