package org.dodo.rpc;

import org.dodo.common.spi.ExtensionHolder;
import org.dodo.common.spi.SpiLoader;
import org.dodo.config.ConfigManager;
import org.dodo.config.MethodConfig;
import org.dodo.config.ReferenceConfig;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.register.URL;
import org.dodo.rpc.remoting.HeartbeatData;
import org.dodo.rpc.remoting.Message;
import org.dodo.rpc.remoting.client.Client;
import org.dodo.rpc.remoting.client.ClientHeartbeat;
import org.dodo.rpc.remoting.client.RequestFuture;
import org.dodo.rpc.serialize.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class RpcClient implements Node {
	private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
	private Client client;
	private ConcurrentHashMap<String, URL> serviceUrls = new ConcurrentHashMap<>();

	public RpcClient(String host, int port) {
		this.client = new Client(host, port, 3000, new RpcClientListener(this));
	}

	@Override
	public void putURL(URL url) {
		serviceUrls.put(url.getPath(), url);
	}

	@Override
	public URL getURL(String service) {
		return serviceUrls.get(service);
	}

	@Override
    public void connect() {
		this.client.connect();
	}

	@Override
	public boolean isConnecting() {
		return this.client.isConnecting();
	}

	@Override
	public void ping() {
		this.client.ping();
	}

	@Override
	public String key() {
		return this.address();
	}

	@Override
	public String address() {
		return client.getIp()+":"+client.getPort();
	}

	/**
	 * rpc发送入口
	 * @param invokerRequest
	 * @param timeout 超时时间
	 * @param async 是否异步，是异步，需要配置接收异步结果的函数
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T send(InvokerRequest invokerRequest, long timeout, boolean async) throws Exception {
		Request request = new Request();
		request.setMethodName(invokerRequest.getMethodName());
		request.setClassName(invokerRequest.getClassName());
		request.setArgs(invokerRequest.getArgs());
		//jdk reflect && json serialize 时需要带上参数类型
		if(invokerRequest.getMethod() != null && invokerRequest.getArgs() != null && invokerRequest.getArgs().length > 0) {
			Class<?> parameterTypes[] = invokerRequest.getMethod().getParameterTypes();
			String[] types = new String[invokerRequest.getArgs().length];
			for (int i = 0; i < invokerRequest.getArgs().length; i++) {
				types[i] = parameterTypes[i].getName();
			}
			request.setParameterTypes(types);
		}

		ExtensionHolder<Serialization> holder = SpiLoader.getExtensionHolder(Serialization.class);
		Serialization serialization = holder.get(ConfigManager.instance().getConsumerConfig().getSerialization());
		byte[] body = serialization.serialize(request);
		Message message = Message.buildRequest(body);
		if(async) {
			this.client.send(message, responseMessage -> {
				ReferenceConfig referenceConfig = ConfigManager.instance().getReferenceConfig(request.getClassName());
				MethodConfig methodConfig = referenceConfig.getMethodConfig(request.getMethodName());
				try {
					Response response = serialization.deserialize(responseMessage.getBody(), Response.class);
					if(response.getException() != null) {
						methodConfig.getOnExceptionMethod().invoke(methodConfig.getRef(), response.getException());
					}
					else {
						methodConfig.getOnResponseMethod().invoke(methodConfig.getRef(), response.getResult());
					}
				} catch (Exception e) {
					logger.error("request:" + request.getClassName()+"."+request.getMethodName(), e);
					try {
						methodConfig.getOnExceptionMethod().invoke(methodConfig.getRef(), e);
					} catch (Exception e1) {
						logger.error("request:" + request.getClassName()+"."+request.getMethodName(), e1);
					}
				}
			});
			return null;
		}
		else {
			RequestFuture future = this.client.send(message);
			if(future == null) {
				throw new RpcException("the client is closed:" + request.getClassName()+"."+request.getMethodName()) ;
			}
			try {
				Message responseMessage = future.get(timeout, TimeUnit.MILLISECONDS);
				Response response = serialization.deserialize(responseMessage.getBody(), Response.class);
				if(response.getException() != null) {
					throw response.getException();
				}
				return (T) response.getResult();
			} catch (TimeoutException e) {
				logger.error("request:" + request.getClassName()+"."+request.getMethodName() + ",timeout:"+timeout, e);
				throw new RpcTimeoutException("request:" + request.getClassName()+"."+request.getMethodName() + ",timeout:"+timeout, e) ;
			} catch (Exception e) {
				logger.error("request:" + request.getClassName()+"."+request.getMethodName(), e);
				throw new RpcException("request:" + request.getClassName()+"."+request.getMethodName(), e) ;
			}
		}
	}

	@Override
	public void close() {
		if (client != null) {
			client.close();
		}
	}

}

