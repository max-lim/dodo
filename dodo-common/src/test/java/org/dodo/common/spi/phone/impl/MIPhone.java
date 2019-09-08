package org.dodo.common.spi.phone.impl;

import org.dodo.common.spi.phone.IPhone;

public class MIPhone implements IPhone {

	@Override
	public void call(String number) {
		System.out.println("using MI phone call:"+number);
		System.out.println("/////////////end");
	}

}
