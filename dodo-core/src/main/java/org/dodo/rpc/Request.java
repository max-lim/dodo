package org.dodo.rpc;

import java.util.Arrays;
import java.util.Map;

public class Request {
	private String className;
	private String methodName;
	private Object args[];
	private String parameterTypes[];
	private Map<String, Object> attachments;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public String[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Map<String, Object> getAttachments() {
		return attachments;
	}

	public Request setAttachments(Map<String, Object> attachments) {
		this.attachments = attachments;
		return this;
	}

	@Override
	public String toString() {
		return "Request{" +
				"className='" + className + '\'' +
				", methodName='" + methodName + '\'' +
				", args=" + Arrays.toString(args) +
				", parameterTypes=" + Arrays.toString(parameterTypes) +
				", attachments=" + attachments +
				'}';
	}
}
