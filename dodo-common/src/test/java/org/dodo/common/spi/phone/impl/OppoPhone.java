package org.dodo.common.spi.phone.impl;

import org.dodo.common.spi.Wrapper;
import org.dodo.common.spi.phone.IPhone;

@Wrapper("diy")
public class OppoPhone implements IPhone {

	@Override
	public void call(String number) {
		System.out.println("using oppo phone call:"+number);
		System.out.println("/////////////end");
	}

}
