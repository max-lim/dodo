package org.dodo.register;

public class RegisterException extends RuntimeException {
	private static final long serialVersionUID = -1055730863843245764L;

	public RegisterException(String message, Exception e) {
		super(message, e);
	}
}
