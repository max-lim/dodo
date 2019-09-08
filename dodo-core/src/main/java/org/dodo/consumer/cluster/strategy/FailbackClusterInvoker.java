package org.dodo.consumer.cluster.strategy;

import org.dodo.common.spi.Wrapper;
import org.dodo.common.thread.NamedThreadFactory;
import org.dodo.consumer.cluster.AbstractClusterInvoker;
import org.dodo.consumer.invoker.InvokerRequest;
import org.dodo.rpc.Node;
import org.omg.CORBA.TIMEOUT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 故障重试自动恢复集群调用方式
 * @author maxlim
 *
 */
@Wrapper("wrapper")
public class FailbackClusterInvoker extends AbstractClusterInvoker {
    private final static Logger logger = LoggerFactory.getLogger(FailbackClusterInvoker.class);
    private ConcurrentLinkedQueue<InvokerRequest> requestsCached;
    private ScheduledExecutorService scheduledExecutorService;
    private ReentrantLock retryLock;

    @Override
    public Object invoke(InvokerRequest invokerRequest, Node serverNode) throws Exception {
        boolean async = this.isAsync(invokerRequest);
        long timeout = this.timeout(invokerRequest, serverNode);
        try {
            return serverNode.send(invokerRequest, timeout, async);
        } catch (Exception e) {
            onFail(invokerRequest);
        }
        return null;
    }

    private void init() {
        if(scheduledExecutorService == null) {
            synchronized (this) {
                if (scheduledExecutorService == null) {
                    requestsCached = new ConcurrentLinkedQueue<>();
                    retryLock = new ReentrantLock();

                    scheduledExecutorService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), new NamedThreadFactory("failback-retry-timer"));
                    scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(retryLock.isLocked()) return;
                                retryLock.lock();
                                doRetry();
                            } catch (Exception e) {
                                logger.warn(null, e);
                            }
                            finally {
                                retryLock.unlock();
                            }
                        }
                    }, 5, 5, TimeUnit.SECONDS);
                }
            }
        }
    }
    
    private void onFail(InvokerRequest invokerRequest) {
        init();
        if(requestsCached.size() > 10000) {
            logger.warn("failback request cahced queue is too much and reject add," + requestsCached.size());
            return;
        }
        if(requestsCached.size() > 1000) {
            logger.warn("failback request cahced queue is too much," + requestsCached.size());
        }
        requestsCached.offer(invokerRequest);
    }

    private void doRetry() {
        requestsCached.forEach(invokerRequest -> {
            try {
                FailbackClusterInvoker.super.invoke(invokerRequest);
            } catch (Exception e) {
                logger.warn("failback retry fail:"+invokerRequest.toString(), e);
            }
        });
    }
}
