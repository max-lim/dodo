package org.dodo.register;

import org.dodo.register.URL;
import org.junit.Assert;
import org.junit.Test;

public class TestURL {

	@Test
	public void withquerystring() {
		String urlstring = "dodo://localhost:8080/org.dodo.service.call?app=test&here=yes&ver=1&a-p_p=n-o_";
		URL url = URL.build(urlstring);
		System.out.println(url.toString());
		System.out.println(url.getUniq());
		Assert.assertEquals("编译后的url非期望值0", urlstring, url.toString());
	}
	
	@Test
	public void withoutquerystring() {
		String urlstring = "dodo://127.0.0.1:8080/org.dodo.service1.call0?";
		URL url = URL.build(urlstring);
		System.out.println(url.toString());
		System.out.println(url.getUniq());
		Assert.assertEquals("编译后的url非期望值1", urlstring, url.toString());
		
		urlstring = "dodo://localhost-abc_def:8080/org.dodo.service_1.call_abc0?";
		url = URL.build(urlstring);
		System.out.println(url.toString());
		System.out.println(url.getUniq());
		Assert.assertEquals("编译后的url非期望值2", urlstring, url.toString());
	}
}
