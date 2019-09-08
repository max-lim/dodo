package org.dodo.rpc.serialize;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.Config;
import com.jsoniter.spi.DecodingMode;
import com.jsoniter.spi.Encoder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

///**
// * jsoniter json序列化
// * @author maxlim
// *
// */
//public class JsoniterSerialization implements Serialization {
//    static {
//        Any.registerEncoders();
//        JsonIterator.setMode(DecodingMode.REFLECTION_MODE);
//        JsonStream.setMode(EncodingMode.REFLECTION_MODE);
//        JsonStream.registerNativeEncoder(Date.class, new Encoder.ReflectionEncoder() {
//
//            @Override
//            public void encode(Object obj, JsonStream stream) throws IOException {
//                if(obj == null) {
//                    stream.writeNull();
//                    return;
//                }
//                stream.writeObjectStart();
//                stream.write(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format((Date)obj).getBytes());
//                stream.writeObjectEnd();
//            }
//
//            @Override
//            public Any wrap(Object obj) {
//                return null;
//            }
//        });
//    }
//    @Override
//    public byte[] serialize(Object src) throws IOException {
//        return JsonStream.serialize(src).getBytes();
//    }
//
//    @Override
//    public <T> T deserialize(byte[] bytes, Class<?> T) throws IOException {
//        return (T) JsonIterator.deserialize(bytes, T);
//    }
//}
