package org.dodo.rpc.serialize;

import java.io.IOException;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import javassist.scopedpool.SoftValueHashMap;
import org.dodo.rpc.Request;
import org.dodo.rpc.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author maxlim
 */
public class JSONSerialization implements Serialization {
	private final static Logger logger = LoggerFactory.getLogger(JSONSerialization.class);

	private static final SerializerFeature[] features = {
			SerializerFeature.DisableCircularReferenceDetect,
			SerializerFeature.WriteNullListAsEmpty,
	};
	private final Map<String, Class> LOADED_CLASSES = new SoftValueHashMap();

	@Override
	public byte[] serialize(Object src) throws IOException {
		return JSON.toJSONBytes(src, features);
	}

	private Class<?> findClass(String className) throws ClassNotFoundException {
		synchronized (LOADED_CLASSES) {
			Class clazz = LOADED_CLASSES.get(className);
			if (clazz == null) {
				LOADED_CLASSES.putIfAbsent(className, Class.forName(className));
				clazz = LOADED_CLASSES.get(className);
			}
			return clazz;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] bytes, Class<?> T) throws IOException {
		try {
			T res = (T) JSON.parseObject(new String(bytes), T);
			if(T == Request.class) {
				Request request = (Request) res;
				if(request.getArgs() != null) {
					for(int i=0; i<request.getArgs().length; i++) {
						request.getArgs()[i] = JSON.parseObject(String.valueOf(request.getArgs()[i]), findClass(request.getParameterTypes()[i]));
					}
				}
			}
			else if(T == Response.class) {
				Response response = (Response) res;
				if(response.getResultType() != null) {
					response.setResult(JSON.parseObject(String.valueOf(response.getResult()), findClass(response.getResultType())));
				}
			}
			return res;
		} catch (ClassNotFoundException ex) {
			throw new IOException(ex);
		} catch (Exception e) {
			logger.error(new String(bytes), e);
			throw e;
		}
	}
}
