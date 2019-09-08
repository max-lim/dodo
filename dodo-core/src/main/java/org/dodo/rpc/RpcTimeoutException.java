package org.dodo.rpc;

public class RpcTimeoutException extends RpcException {

	private static final long serialVersionUID = -3231446447802649797L;

	public RpcTimeoutException(String message, Exception e) {
		super(message, e);
	}

	public Throwable fillInStackTrace() {
		return this;
	}
}
