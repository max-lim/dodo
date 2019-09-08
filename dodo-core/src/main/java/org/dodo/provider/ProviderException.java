package org.dodo.provider;

/**
 * 提供者内部异常
 * @author maxlim
 *
 */
public class ProviderException extends Exception {
    public final static String ERROR_SERVER_BUSY = "server-busy";
    public final static String ERROR_REQUEST_LIMITED = "request-limited";

    private String code;
    public ProviderException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public Throwable fillInStackTrace() {
        return this;
    }
}
