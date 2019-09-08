package org.dodo.common.spi.phone.impl;

import javax.annotation.Resource;

import org.dodo.common.spi.phone.IPhone;

public class DiyPhoneWrapper implements IPhone {
	@Resource
	private IPhone iphone;
	
	@Override
	public void call(String number) {
		System.out.println("using diy phone call:"+number);
		iphone.call(number);
	}

}
