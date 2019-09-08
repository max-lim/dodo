package org.dodo.common.spi;

import org.dodo.common.spi.phone.IPhone;
import org.junit.Test;

public class TestSpi {

	@Test
	public void load() {
		System.out.println("++++++++++++++load++++++++++++++++++");
		ExtensionHolder<IPhone> holder = SpiLoader.getExtensionHolder(IPhone.class);
		holder.get().call("13537567813");
		holder.get("mi").call("13537567813");
		
	}

	@Test
	public void loadWrapper() {
		System.out.println("++++++++++++++loadWrapper++++++++++++++++++");
		ExtensionHolder<IPhone> holder = SpiLoader.getExtensionHolder(IPhone.class);
		holder.get("vivo").call("13537567813");
		holder.get("oppo").call("13537567813");
	}
}
