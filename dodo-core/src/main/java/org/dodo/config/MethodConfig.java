package org.dodo.config;

import org.dodo.common.utils.StringUtils;

import java.lang.reflect.Method;

/**
 * 消费端方法的配置
 * @author maxlim
 *
 */
public class MethodConfig {
	private String name;
	private Class refClass;
	private String onResponse;
	private String onException;
	private String onMock;
	private boolean forceMock = false;
	private Object ref;
	private Method onResponseMethod;
	private Method onExceptionMethod;
	private Method onMockMethod;

	public boolean isAsync() {
		return StringUtils.isNotBlank(onResponse) && StringUtils.isNotBlank(onException);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class getRefClass() {
		return refClass;
	}

	public void setRefClass(Class refClass) {
		this.refClass = refClass;
		if (ref == null) {
		}
	}

	public String getOnResponse() {
		return onResponse;
	}

	public void setOnResponse(String onResponse) throws Exception {
		if (StringUtils.isBlank(onResponse)) {
			return;
		}
		onResponse = onResponse.trim();
		for(Method method: ref.getClass().getMethods()) {
			if(method.getName().equals(onResponse)) {
				onResponseMethod = method;
				break;
			}
		}
		if(onResponseMethod == null) {
			throw new Exception("init MethodConfig fail:" + refClass +"."+ onResponse + " not found!");
		}
		this.onResponse = onResponse;
	}

	public String getOnException() {
		return onException;
	}

	public void setOnException(String onException) throws Exception {
		if (StringUtils.isBlank(onException)) {
			return;
		}
		onException = onException.trim();
		for(Method method: ref.getClass().getMethods()) {
			if(method.getName().equals(onException)) {
				onExceptionMethod = method;
				break;
			}
		}
		if(onExceptionMethod == null) {
			throw new Exception("init MethodConfig fail:" + refClass +"."+ onException + " not found!");
		}
		this.onException = onException;
	}

	public String getOnMock() {
		return onMock;
	}

	public void setOnMock(String onMock) throws Exception {
		if (StringUtils.isBlank(onMock)) {
			return;
		}
		onMock = onMock.trim();
		for(Method method: ref.getClass().getMethods()) {
			if(method.getName().equals(onMock)) {
				onMockMethod = method;
				break;
			}
		}
		if(onMockMethod == null) {
			throw new Exception("init MethodConfig fail:" + refClass +"."+ onMock + " not found!");
		}
		this.onMock = onMock;
	}

	public boolean isForceMock() {
		return forceMock;
	}

	public void setForceMock(boolean forceMock) {
		this.forceMock = forceMock;
	}

	public Object getRef() {
		return ref;
	}

	public void setRef(Object ref) {
		this.ref = ref;
		this.refClass = ref.getClass();
	}

	public Method getOnResponseMethod() {
		return onResponseMethod;
	}

	public void setOnResponseMethod(Method onResponseMethod) {
		this.onResponseMethod = onResponseMethod;
		this.onResponse = onResponseMethod.getName();
	}

	public Method getOnExceptionMethod() {
		return onExceptionMethod;
	}

	public void setOnExceptionMethod(Method onExceptionMethod) {
		this.onExceptionMethod = onExceptionMethod;
		this.onException = onExceptionMethod.getName();
	}

	public Method getOnMockMethod() {
		return onMockMethod;
	}

	public void setOnMockMethod(Method onMockMethod) {
		this.onMockMethod = onMockMethod;
		this.onMock = onMockMethod.getName();
	}

	public boolean isMock() {
		return ref != null && onMockMethod != null;
	}

	@Override
	public String toString() {
		return "MethodConfig{" +
				"name='" + name + '\'' +
				", refClass=" + refClass.getName() +
				", onResponse='" + onResponse + '\'' +
				", onException='" + onException + '\'' +
				", onMock='" + onMock + '\'' +
				", ref=" + ref.getClass().getName() +
				'}';
	}
}
