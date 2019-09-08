package org.dodo.consumer.invoker;

import org.dodo.config.ReferenceConfig;
import org.dodo.rpc.Request;

import java.lang.reflect.Method;
import java.util.Map;

public class InvokerRequest extends Request {
	private Class<?> clazz;
	private Method method;
	private ReferenceConfig referenceConfig;
	private Map<String, Object> attachments;
	private long requestSeq;

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
		super.setClassName(clazz.getName());
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
		super.setMethodName(method.getName());
	}

	public ReferenceConfig getReferenceConfig() {
		return referenceConfig;
	}

	public void setReferenceConfig(ReferenceConfig referenceConfig) {
		this.referenceConfig = referenceConfig;
	}

	@Override
	public String getClassName() {
		if(clazz != null) return clazz.getName();
		return super.getClassName();
	}

	@Override
	public String getMethodName() {
		if(method != null) return method.getName();
		return super.getMethodName();
	}

	@Override
	public Map<String, Object> getAttachments() {
		return attachments;
	}

	@Override
	public InvokerRequest setAttachments(Map<String, Object> attachments) {
		this.attachments = attachments;
		return this;
	}

	public long getRequestSeq() {
		return requestSeq;
	}

	public void setRequestSeq(long requestSeq) {
		this.requestSeq = requestSeq;
	}

	@Override
	public String toString() {
		return "InvokerRequest{" +
				"clazz=" + clazz +
				", method=" + method +
				", referenceConfig=" + referenceConfig +
				", attachments=" + attachments +
				", requestSeq=" + requestSeq +
				"} " + super.toString();
	}
}
