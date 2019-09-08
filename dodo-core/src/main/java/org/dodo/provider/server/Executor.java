package org.dodo.provider.server;

import io.protostuff.Rpc;
import org.dodo.common.spi.SpiLoader;
import org.dodo.common.thread.NamedThreadFactory;
import org.dodo.common.thread.PrioritizeIncreasingThreadsThreadPool;
import org.dodo.common.thread.ThreadUtils;
import org.dodo.config.ConfigManager;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.context.RpcContext;
import org.dodo.provider.ProviderException;
import org.dodo.provider.invoker.Invoker;
import org.dodo.rpc.Request;
import org.dodo.rpc.Response;
import org.dodo.rpc.RpcException;
import org.dodo.rpc.remoting.Message;
import org.dodo.rpc.serialize.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 执行器
 * @author maxlim
 *
 */
public class Executor implements Closeable {
	private static final Logger logger = LoggerFactory.getLogger(Executor.class);
	private String name;
	private ThreadPoolExecutor executor;
	private Serialization serialization;
	private Invoker invoker;
	public static final Executor INSTANCE = new Builder().build();

	private Executor() {
	}

	private void buildExecutor(String name, final int corePoolSize, final int maxPoolSize, final int workQueueSize) {
		this.name = name;
		this.executor = new PrioritizeIncreasingThreadsThreadPool(corePoolSize, maxPoolSize, workQueueSize,
				new NamedThreadFactory(name + "-executor"),
				(r, executor) -> {
					//拒绝策略
					if(r instanceof ExecutorRunnable) {
						ExecutorRunnable executorRunnable = (ExecutorRunnable) r;
						Response response = new Response(new ProviderException("request be rejected", ProviderException.ERROR_SERVER_BUSY));
						try {
							executorRunnable.responseMessageConsumer.accept(executorRunnable.requestMessage.buildResponseByRequest(serialization.serialize(response)));
						} catch (IOException ex) {
							logger.error(response.toString(), ex);
						}
					}
				});
	}

	public static class Builder {
		String name;
		int corePoolSize;
		int maxPoolSize;
		int workQueueSize;
		Serialization serialization;
		Invoker invoker;

		public Builder() {
			name = "default";
			corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
			maxPoolSize = Runtime.getRuntime().availableProcessors() * 4;
			workQueueSize = 1000;
			serialization = SpiLoader.getExtensionHolder(Serialization.class).get(ConfigManager.instance().getProviderConfig().getSerialization());
			invoker = SpiLoader.getExtensionHolder(Invoker.class).get();
		}
		public Executor build() {
			Executor executor = new Executor();
			executor.invoker = invoker;
			executor.serialization = serialization;
			executor.buildExecutor(name, corePoolSize, maxPoolSize, workQueueSize);
			return executor;
		}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setCorePoolSize(int corePoolSize) {
			this.corePoolSize = corePoolSize;
			return this;
		}

		public Builder setMaxPoolSize(int maxPoolSize) {
			this.maxPoolSize = maxPoolSize;
			return this;
		}

		public Builder setWorkQueueSize(int workQueueSize) {
			this.workQueueSize = workQueueSize;
			return this;
		}

		public Builder setSerialization(Serialization serialization) {
			this.serialization = serialization;
			return this;
		}

		public Builder setInvoker(Invoker invoker) {
			this.invoker = invoker;
			return this;
		}
	}

	public void execute(final Message requestMessage, final Consumer<Message> responseMessageConsumer) {
		executor.execute(new ExecutorRunnable(requestMessage, responseMessageConsumer));
    }

    public void execute(final long seq, final Request request, final Consumer<Message> responseMessageConsumer) {
		Response response = null;
		try {
			response = execute(seq, request);
			byte[] responseBytes = serialization.serialize(response);
			responseMessageConsumer.accept(Message.buildResponse(seq, responseBytes));
		} catch (IOException e) {
			logger.error(response != null ? response.toString() : request.toString(), e);
		}
    }
	
	public Response execute(final long seq, final Request request) {
		InvokerRequest invokerRequest = new InvokerRequest();
		invokerRequest.setClassName(request.getClassName());
		invokerRequest.setMethodName(request.getMethodName());
		invokerRequest.setArgs(request.getArgs());
		invokerRequest.setParameterTypes(request.getParameterTypes());
		invokerRequest.setAttachments(request.getAttachments());
		invokerRequest.setRequestSeq(seq);
		try {
			return (Response) invoker.invoke(invokerRequest);
		} catch (Exception e) {
			logger.error(null, e);
			return new Response(new RpcException("execute class:"+invokerRequest.getClassName()+"."+invokerRequest.getMethodName(), e));
		}
	}
	
	private class ExecutorRunnable implements Runnable {
		Message requestMessage;
		Consumer<Message> responseMessageConsumer;

		public ExecutorRunnable(Message requestMessage, Consumer<Message> responseMessageConsumer) {
			this.requestMessage = requestMessage;
			this.responseMessageConsumer = responseMessageConsumer;
		}

		@Override
		public void run() {
			try {
				RpcContext.getContext().setValue(RpcContext.CONTEXT_GROUP, name);
				RpcContext.getContext().setValue(RpcContext.CONTEXT_PROTOCOL, "dodo");
				RpcContext.getContext().setValue(RpcContext.CONTEXT_REMOTE, requestMessage.getRemoteIp());
				Request request = serialization.deserialize(requestMessage.getBody(), Request.class);
				execute(requestMessage.getSeq(), request, responseMessageConsumer);
			}
			catch (Exception e) {
				logger.error(requestMessage.toString(), e);
				Response response = new Response(e instanceof ProviderException ? e : new RpcException("execute message:"+requestMessage.getSeq()+" fail", e));
				try {
					responseMessageConsumer.accept(requestMessage.buildResponseByRequest(serialization.serialize(response)));
				} catch (IOException ex) {
					logger.error(response.toString(), ex);
				}
			} finally {
				RpcContext.getContext().clear();
			}
		}
	}

	@Override
	public void close() throws IOException {
		ThreadUtils.shutdown(this.executor,3000, TimeUnit.MILLISECONDS);
	}
}
