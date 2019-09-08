package org.dodo.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程生成thread工厂
 * @author maxlim
 *
 */
public class NamedThreadFactory implements ThreadFactory {
	private final AtomicInteger seq = new AtomicInteger(1);
	private final String prefix;
	private final boolean isDaemo;
	private final ThreadGroup group;

	public NamedThreadFactory(String prefix) {
		this(prefix, false);
	}

	public NamedThreadFactory(String prefix, boolean isDaemo) {
		this.prefix = prefix;
		this.isDaemo = isDaemo;
		SecurityManager s = System.getSecurityManager();
		this.group = s == null ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
	}

	public Thread newThread(Runnable runnable) {
		String name = prefix + "-" + seq.getAndIncrement();
		Thread ret = new Thread(group, runnable, name, 0);
		ret.setDaemon(isDaemo);
		return ret;
	}

}