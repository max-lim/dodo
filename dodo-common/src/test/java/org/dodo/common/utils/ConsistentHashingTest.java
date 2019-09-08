package org.dodo.common.utils;

import com.sun.tools.javac.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author maxlim
 */
public class ConsistentHashingTest {
    int virtualNodesNumber = 100;
    ConsistentHashing consistentHashing;
    @Before
    public void init() {
        consistentHashing = new ConsistentHashing(List.of("192.168.1.86:8080","192.168.1.210:8080","192.168.1.21:8080"), virtualNodesNumber);
    }
    public void hash(String key) {
        for (int i = 0; i < virtualNodesNumber; i++) {
            String vkey = consistentHashing.virtualKey(key, i);
            System.out.println(vkey + "  " + consistentHashing.hash(vkey));
        }
    }
    @Test
    public void test() {
//        hash("a");
//        hash("b");
        String key = "org.dodo.test.service.EchoService.hello";
        String selected1 = consistentHashing.select(key);
        String selected2 = consistentHashing.select(key);
        System.out.println(selected1);
        System.out.println(consistentHashing.hash(key));
        Assert.assertEquals("两次查询不一致", selected1, selected2);
    }
}
