package org.dodo.rpc.serialize;

import java.io.IOException;

import org.dodo.common.spi.Spi;

/**
 * 序列化接口
 * @author maxlim
 *
 */
@Spi("protostuff")
public interface Serialization {
	byte[] serialize(Object src) throws IOException;
	
    <T> T deserialize(byte[] bytes, Class<?> T) throws IOException;
}
