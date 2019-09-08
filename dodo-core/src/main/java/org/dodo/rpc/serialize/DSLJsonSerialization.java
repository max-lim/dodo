package org.dodo.rpc.serialize;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.runtime.Settings;

import java.io.IOException;

/**
 * dsl json 序列化方式
 * @author maxlim
 */
public class DSLJsonSerialization implements Serialization {
    private final static DslJson<Object> dslJson = new DslJson<>(Settings.withRuntime());
    private final static ThreadLocal<JsonWriter> THREAD_SAFE_WRITER = ThreadLocal.withInitial(()-> dslJson.newWriter(512));

    @Override
    public byte[] serialize(Object src) throws IOException {
        JsonWriter writer = THREAD_SAFE_WRITER.get();
        dslJson.serialize(writer, src);
        byte[] buffer = writer.getByteBuffer();
        //buffer长度可能会比实践长，如果是进行buffer截取write.size()长度，性能会大打折扣
        return buffer;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<?> T) throws IOException {
        return (T) dslJson.deserialize(T, bytes, bytes.length);
    }
}
