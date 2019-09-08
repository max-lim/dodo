package org.dodo.common.thread;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 优先爆满线程池，再入队列
 * @author maxlim
 *
 */
public class PrioritizeIncreasingThreadsThreadPool extends ThreadPoolExecutor {
    public static int QUEUE_CAPACITY = 2000;
    public static int KEEP_ALIVE_MILLIS_TIME = 30 * 1000;
    private int queueCapacity;
    private AtomicInteger submittedCounter;
    private Queue queue;

    //此构造函数覆盖父类构造函数的总入口
    public PrioritizeIncreasingThreadsThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                (workQueue==null||workQueue.size()==0)?QUEUE_CAPACITY:workQueue.size(),
                threadFactory, handler);
    }

    public PrioritizeIncreasingThreadsThreadPool(int corePoolSize, int maximumPoolSize, int queueCapacity, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        this(corePoolSize, maximumPoolSize, KEEP_ALIVE_MILLIS_TIME, TimeUnit.MILLISECONDS, queueCapacity, threadFactory, handler);
    }

    public PrioritizeIncreasingThreadsThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, int queueCapacity, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new Queue(),
                (threadFactory != null ? threadFactory : new NamedThreadFactory("increasing-threads-pool")),
                handler == null ? new AbortPolicy() : handler);
        this.queueCapacity = queueCapacity <= 0 ? QUEUE_CAPACITY : queueCapacity;
        this.submittedCounter = new AtomicInteger(0);
        this.queue = (Queue) super.getQueue();
        this.queue.setThreadPool(this);
    }

    @Override
    public void execute(Runnable command) {
        submittedCounter.incrementAndGet();
        if (submittedCounter.get() >= super.getMaximumPoolSize() + queueCapacity) {
            submittedCounter.decrementAndGet();
            super.getRejectedExecutionHandler().rejectedExecution(command, this);
            return;
        }
        try {
            super.execute(command);
        } catch (RejectedExecutionException e) {
            if ( ! queue.offerForce(command)) {
                submittedCounter.decrementAndGet();
                super.getRejectedExecutionHandler().rejectedExecution(command, this);
            }
        }
    }

    @Override
    public void afterExecute(Runnable r, Throwable t) {
        submittedCounter.decrementAndGet();
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public int getSubmittedCounter() {
        return submittedCounter.get();
    }

    public static class Queue extends LinkedTransferQueue<Runnable> {
        PrioritizeIncreasingThreadsThreadPool threadPool;
        public void setThreadPool(PrioritizeIncreasingThreadsThreadPool threadPool) {
            this.threadPool = threadPool;
        }

        public boolean offerForce(Runnable command) {
            if (this.threadPool.isShutdown()) {
                throw new RejectedExecutionException("executor is shutdown, cannot put any commands into the queue");
            }
            return super.offer(command);
        }

        public boolean offer(Runnable command) {
            int alivePoolSize = threadPool.getPoolSize();
            if (alivePoolSize >= threadPool.getMaximumPoolSize()) {
                return super.offer(command);
            }
            //说明有空闲线程，只需加入队列即可，空闲线程会消费队列中的任务
            if (alivePoolSize > threadPool.getSubmittedCounter()) {
                return super.offer(command);
            }
            //存活线程数还未到瓶颈，不入队列
            if (alivePoolSize < threadPool.getMaximumPoolSize()) {
                return false;
            }
            return super.offer(command);
        }
    }
}
