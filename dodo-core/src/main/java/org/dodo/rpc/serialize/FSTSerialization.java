package org.dodo.rpc.serialize;

import org.nustaq.serialization.FSTConfiguration;

import java.io.IOException;
/**
 * @author maxlim
 */
public class FSTSerialization implements Serialization {
    private static final FSTConfiguration configuration = FSTConfiguration.createStructConfiguration();

    @Override
    public byte[] serialize(Object src) throws IOException {
        return configuration.asByteArray(src);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<?> T) throws IOException {
        return (T) configuration.asObject(bytes);
    }
}
