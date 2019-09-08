package org.dodo.reflect;

import org.dodo.rpc.serialize.UserBean;

public interface EchoService {
	public String hello(String name);
	public UserBean withBean(UserBean userBean);
	public void returnVoid();
}
