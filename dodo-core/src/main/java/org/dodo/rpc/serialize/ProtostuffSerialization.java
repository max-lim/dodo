package org.dodo.rpc.serialize;

import io.protostuff.GraphIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
//import org.dodo.common.thread.DThread;

import java.io.IOException;

/**
 * protostuff是protobuf封装版本，不需要提前定义proto文件
 * @author maxlim
 */
public class ProtostuffSerialization implements Serialization {
    private final static ThreadLocal<LinkedBuffer> THREAD_SAFE_BUFFER = ThreadLocal.withInitial(()->LinkedBuffer.allocate());
//    //常驻线程对应的buffer cached
//    private final static LinkedBuffer[] BUFFER_CACHED = new LinkedBuffer[1024];
//    static {
//        for (int i = 0; i < BUFFER_CACHED.length; i++) {
//            BUFFER_CACHED[i] = LinkedBuffer.allocate();
//        }
//    }
//
//    private LinkedBuffer linkedBuffer() {
//        Thread currentThread = Thread.currentThread();
//        int id = currentThread instanceof DThread ? ((DThread) currentThread).getIntId(): (int)currentThread.getId();
//        if(id >= 0 && id < BUFFER_CACHED.length) {
//            return BUFFER_CACHED[id];
//        }
//        return THREAD_SAFE_BUFFER.get();
//    }

    @Override
    public byte[] serialize(Object src) throws IOException {
        LinkedBuffer linkedBuffer = THREAD_SAFE_BUFFER.get();
        try {
            Schema schema = RuntimeSchema.getSchema(src.getClass());
            byte[] dataBytes = GraphIOUtil.toByteArray(src, schema, linkedBuffer);
            return dataBytes;
        } finally {
            linkedBuffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<?> T) throws IOException {
        Schema schema = RuntimeSchema.getSchema(T);
        Object result = schema.newMessage();
        GraphIOUtil.mergeFrom(bytes, result, schema);
        return (T) result;
    }
}
