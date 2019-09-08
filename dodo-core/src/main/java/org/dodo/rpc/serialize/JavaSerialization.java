package org.dodo.rpc.serialize;


import java.io.*;

/**
 * JDK序列化
 * @author maxlim
 */
public class JavaSerialization implements Serialization {
    @Override
    public byte[] serialize(Object src) throws IOException {
        if( ! (src instanceof Serializable)){
            throw new IllegalArgumentException("source object must be implements java.io.Serializable");
        }

        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(src);
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<?> T) throws IOException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(byteArrayInputStream)) {
            return (T) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}
