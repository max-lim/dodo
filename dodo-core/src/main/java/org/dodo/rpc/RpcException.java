package org.dodo.rpc;

public class RpcException extends Exception {
	private static final long serialVersionUID = -961898154459560894L;

	public RpcException(String message) {
		super(message);
	}

	public RpcException(String message, Exception e) {
		super(message, e);
	}
}
