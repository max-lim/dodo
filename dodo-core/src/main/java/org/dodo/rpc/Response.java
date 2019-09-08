package org.dodo.rpc;

public class Response {
	private Object result;
	private Exception exception;
	private String resultType;

	public Response() {
	}

	public Response(Exception exception) {
		this.exception = exception;
	}

	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	@Override
	public String toString() {
		return "Response{" +
				"result=" + result +
				", exception=" + exception +
				", resultType='" + resultType + '\'' +
				'}';
	}

}
