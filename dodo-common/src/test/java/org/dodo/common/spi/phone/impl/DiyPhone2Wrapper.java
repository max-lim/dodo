package org.dodo.common.spi.phone.impl;

import org.dodo.common.spi.phone.IPhone;

public class DiyPhone2Wrapper implements IPhone {
	private IPhone iphone;
	
	public IPhone getIphone() {
		return iphone;
	}

	public void setIphone(IPhone iphone) {
		this.iphone = iphone;
	}

	@Override
	public void call(String number) {
		System.out.println("using diy2 phone call:"+number);
		iphone.call(number);
	}


}
