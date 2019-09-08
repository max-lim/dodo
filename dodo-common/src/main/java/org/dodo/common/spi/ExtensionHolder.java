package org.dodo.common.spi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 扩展类存储
 * @author maxlim
 *
 * @param <E>
 */
public class ExtensionHolder<E> {
	protected Map<String, E> name2Object = new ConcurrentHashMap<>();
	protected Map<String, E> name2Wrapper = new ConcurrentHashMap<>();
	protected String defaultName;
	
	/**
	 * 如果是有指定wrapper，则返回被装饰的实例
	 * @param name
	 * @return
	 */
	public E get(String name) {
		name = name == null || name.trim().length() == 0 ? defaultName: name;
		return Optional.ofNullable(this.name2Wrapper.get(name)).orElse(getDirect(name));
	}
	
	/**
	 * 如果是有指定wrapper，则返回被装饰的默认实例
	 * @return
	 */
	public E get() {
		return Optional.ofNullable(this.name2Wrapper.get(defaultName)).orElseGet(() -> getDirect());
	}

	public Collection<E> getAll() {
		return Collections.unmodifiableCollection(name2Wrapper.isEmpty() ? name2Object.values() : name2Wrapper.values());
	}
	/**
	 * 未被装饰器wrapper过的原始实例
	 * @param name
	 * @return
	 */
	public E getDirect(String name) {
		name = name == null || name.trim().length() == 0 ? defaultName: name;
		return this.name2Object.get(name);
	}
	
	/**
	 * 未被装饰器wrapper过的原始默认实例
	 * @return
	 */
	public E getDirect() {
		return this.name2Object.get(defaultName);
	}
}
