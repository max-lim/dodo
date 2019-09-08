package org.dodo.common.spi.phone.impl;

import javax.annotation.Resource;

import org.dodo.common.spi.phone.Cpu;
import org.dodo.common.spi.phone.IPhone;

public class HuaweiPhone implements IPhone {
	@Resource
	private Cpu cpu;
	
//	public Cpu getCpu() {
//		return cpu;
//	}
//
//	public void setCpu(Cpu cpu) {
//		this.cpu = cpu;
//	}

	@Override
	public void call(String number) {
		this.cpu.run();
		System.out.println("using huawei phone call:"+number);
		System.out.println("/////////////end");
	}

}
