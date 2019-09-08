package org.dodo.common.spi.phone;

import org.dodo.common.spi.Spi;

//@Spi(value = "huawei", wrapper = "diy")
@Spi(value = "huawei")
public interface IPhone {
	public void call(String number);
}
