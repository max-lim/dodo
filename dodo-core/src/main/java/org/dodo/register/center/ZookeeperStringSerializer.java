package org.dodo.register.center;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.io.UnsupportedEncodingException;

/**
 * 字符串序列化
 * @author maxlim
 */
public class ZookeeperStringSerializer implements ZkSerializer {
    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        if (bytes == null){
            return null;
        }
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ZkMarshallingError(e);
        }
    }

    @Override
    public byte[] serialize(Object obj) throws ZkMarshallingError {
        try {
            return obj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ZkMarshallingError(e);
        }
    }
}
