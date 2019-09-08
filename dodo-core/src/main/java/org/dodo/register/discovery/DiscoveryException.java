package org.dodo.register.discovery;

public class DiscoveryException extends RuntimeException {
	private static final long serialVersionUID = -8726506389840235680L;

	public DiscoveryException(String message, Exception e) {
		super(message, e);
	}
}
