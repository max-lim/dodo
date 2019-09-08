package org.dodo.rpc.serialize;

/**
 * @author maxlim
 *
 */

import com.alibaba.fastjson.JSON;
import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonReader;
import com.jsoniter.JsonIterator;
import com.jsoniter.ValueType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @desc: dsl-json  fastjson  jackson  jsoniter 效率比较
 * @author: zengxc
 * @date: 2018/4/4
 */
public class JSONTest {

    public static String[] factors = {};
    private static String JSON_STR = null;
    private static int count = 0;
    private final DslJson<Object> dslJson = new DslJson<Object>();

    @Before
    public void init() throws Exception {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i <20; i++) {
            map.put("a"+i, "b"+i);
        }
        JSON_STR = JSON.toJSONString(map);
    }

    @Test
    public void testFastJson() {
        Map<String, String> map = JSON.parseObject(JSON_STR, Map.class);
        long startTime = System.currentTimeMillis();
        JSON.parseObject(JSON_STR, Map.class);
        System.out.println("fastjson "+ (System.currentTimeMillis() - startTime));
    }

    @Test
    public void testJsoniter03() throws IOException {
        JsonIterator jsonIterator = JsonIterator.parse(JSON_STR);
        long startTime = System.currentTimeMillis();
        Map<String, String> map = jsonIterator.readAny().as(Map.class);
        System.out.println("jsoniter "+(System.currentTimeMillis() - startTime));
    }

    @Test
    public void testDslJson() throws IOException {
        long startTime = System.currentTimeMillis();
        final byte[] buff = JSON_STR.getBytes("UTF-8");
        JsonReader<Object> jsonReader = dslJson.newReader(buff);
        Map map = jsonReader.next(Map.class);
        System.out.println("dsl json " + (System.currentTimeMillis() - startTime));

    }
}