package org.dodo.common.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程相关工具类
 * @author maxlim
 *
 */
public class ThreadUtils {
    private static final Logger logger = LoggerFactory.getLogger(ThreadUtils.class);

    /**
     * shutdown线程池
     * @param executor
     * @param timeout
     * @param timeUnit
     */
    public static void shutdown(ThreadPoolExecutor executor, long timeout, TimeUnit timeUnit) {
        try {
            if(executor == null) return;

            executor.shutdown();
            if (!executor.awaitTermination(timeout, timeUnit)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(timeout, timeUnit)) {
                    logger.warn("{} didn't terminate", executor.toString());
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * shutdown线程
     * @param thread
     * @param interrupt
     * @param timeoutMillis
     */
    public static void shutdown(Thread thread, boolean interrupt, long timeoutMillis) {
        if(thread == null) return;
        if (interrupt) {
            thread.interrupt();
        }
        if ( ! thread.isDaemon()) {
            try {
                thread.join(timeoutMillis);
            } catch (InterruptedException e) {
            }
        }
    }
}
