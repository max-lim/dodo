package org.dodo.common.spi.phone.impl;

import org.dodo.common.spi.phone.Cpu;

public class HuaweiCpu implements Cpu {

	@Override
	public void run() {
		System.out.println("huawei cpu running");
	}

}
