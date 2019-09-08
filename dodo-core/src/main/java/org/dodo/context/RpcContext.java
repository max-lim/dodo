package org.dodo.context;

import com.alibaba.fastjson.JSONObject;
import org.dodo.common.thread.NamedThreadFactory;
import org.dodo.common.utils.StringBuilderBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 执行请求上下文
 * @author maxlim
 *
 */
public class RpcContext {
    private final static ThreadLocal<RpcContext> THREAD_LOCAL = ThreadLocal.withInitial(()-> new RpcContext());

    public static RpcContext getContext() {
        return THREAD_LOCAL.get();
    }

    //thread local 上下文暂存数据=================================================start
    public static final String CONTEXT_CONSUMER_INVOKE_USETIME = "consumer-invoke-usetime:";
    public static final String CONTEXT_PROTOCOL = "protocol";
    public static final String CONTEXT_GROUP = "group";
    public static final String CONTEXT_REMOTE = "remote";
    private Map<String, Object> values = new ConcurrentHashMap<>();

    public void setValue(String key, Object value) {
        values.put(key, value);
    }
    public Object getValue(String key) {
        return values.get(key);
    }
    public void clear() {
        values.clear();
    }
    //thread local 上下文暂存数据=================================================end

    //调用提供服务耗时统计for consumer=================================================start
    private static Map<String, Map<String, AtomicInteger>> serviceNodesInvokeUseTime = new ConcurrentHashMap<>();
    public static Integer getServiceInvokeNodeUseTime(String service, String node) {
        Map<String, AtomicInteger> nodesInvokeUseTime = serviceNodesInvokeUseTime.get(service);
        if(nodesInvokeUseTime != null) {
            AtomicInteger useTime = nodesInvokeUseTime.get(node);
            return useTime == null ? -1: useTime.get();
        }
        return -1;
    }
    public static void putServiceInvokeNodeUseTime(String service, String node, int useTime) {
        Map<String, AtomicInteger> nodesInvokeUseTime = serviceNodesInvokeUseTime.get(service);
        if(nodesInvokeUseTime == null) {
            serviceNodesInvokeUseTime.putIfAbsent(node, new ConcurrentHashMap<>());
            nodesInvokeUseTime = serviceNodesInvokeUseTime.get(service);
        }
        AtomicInteger useTimeIncr = nodesInvokeUseTime.get(node);
        if (useTimeIncr == null) {
            nodesInvokeUseTime.putIfAbsent(node, new AtomicInteger(0));
            useTimeIncr = nodesInvokeUseTime.get(node);
        }
        useTimeIncr.getAndAdd(useTime);
    }

    public static void clearServiceInvokeNodeUseTime(String service) {
        serviceNodesInvokeUseTime.remove(service);
    }
    //调用提供服务耗时统计for consumer=================================================end

    //服务被调用统计for provider=================================================start

    private static Map<String, ServiceInvokedStatItem> serviceInvokedStatMap = new ConcurrentHashMap<>();
    //统计信息日志输出
    private static ScheduledExecutorService serviceInvokedStatLogTimer = Executors.newScheduledThreadPool(1, new NamedThreadFactory("serviceInvokedStatLogTimer"));
    private static final Logger STAT_LOG = LoggerFactory.getLogger("stat");
    static {
        serviceInvokedStatLogTimer.scheduleWithFixedDelay(() -> {
            serviceInvokedStatMap.forEach((service, serviceInvokedStatItem) -> {
                StringBuilder stringBuilder = StringBuilderBuffer.getStringBuilder();
                //参考prometheus输出格式
                ServiceInvokedStatItem.CounterAndUsetimeStat totalCounterAndUsetime = serviceInvokedStatItem.getTotalCounterAndUsetime();
                stringBuilder.append("service_invoked_stat_total").append(new JSONObject().fluentPut("service", service).fluentPut("usetime", totalCounterAndUsetime.getUsetime()).fluentPut("start", totalCounterAndUsetime.startAt).toJSONString()).append(" ").append(totalCounterAndUsetime.getCount());
                STAT_LOG.info(stringBuilder.toString());
                stringBuilder.setLength(0);

                stringBuilder.append("service_invoked_stat_fail").append(new JSONObject().fluentPut("service", service).toJSONString()).append(" ").append(serviceInvokedStatItem.failCount());
                STAT_LOG.info(stringBuilder.toString());
                stringBuilder.setLength(0);

                ServiceInvokedStatItem.CounterAndUsetimeStat latestCounterAndUsetime = serviceInvokedStatItem.getLatestCounterAndUsetime();
                stringBuilder.append("service_invoked_stat_latest").append(new JSONObject().fluentPut("service", service).fluentPut("usetime", latestCounterAndUsetime.getUsetime()).fluentPut("start", latestCounterAndUsetime.startAt).toJSONString()).append(" ").append(latestCounterAndUsetime.getCount());
                serviceInvokedStatItem.resetLatestCountAndUsetimeStat();
                STAT_LOG.info(stringBuilder.toString());
                stringBuilder.setLength(0);

                stringBuilder.append("service_invoked_stat_running").append(new JSONObject().fluentPut("service", service).toJSONString()).append(" ").append(serviceInvokedStatItem.getRunningCount());
                STAT_LOG.info(stringBuilder.toString());
                stringBuilder.setLength(0);
            });
        }, 30, 30, TimeUnit.SECONDS);
    }
    public static ServiceInvokedStatItem getServiceInvokedStat(String service) {
        ServiceInvokedStatItem serviceInvokedStatItem = serviceInvokedStatMap.get(service);
        if(serviceInvokedStatItem == null) {
            serviceInvokedStatMap.putIfAbsent(service, new ServiceInvokedStatItem());
            serviceInvokedStatItem = serviceInvokedStatMap.get(service);
        }
        return serviceInvokedStatItem;
    }
    public static void onServiceInvokedStatBefore(String service) {
        //防止配置了不同的限速器重复统计
        if(RpcContext.getContext().getValue("ServiceInvokedStat-Flag") != null) {
            return;
        }
        RpcContext.getContext().setValue("ServiceInvokedStat-Flag", 1);

        ServiceInvokedStatItem serviceInvokedStatItem = getServiceInvokedStat(service);
        serviceInvokedStatItem.runningCounter.incrementAndGet();
    }
    public static void onServiceInvokedStatAfter(String service, boolean isSuccess, long usetimeMillis) {
        ServiceInvokedStatItem serviceInvokedStatItem = getServiceInvokedStat(service);
        serviceInvokedStatItem.runningCounter.decrementAndGet();
        if (!isSuccess) {
            serviceInvokedStatItem.failCounter.incrementAndGet();
        }
        serviceInvokedStatItem.totalCounterAndUsetime.stat(1, usetimeMillis);
        serviceInvokedStatItem.latestCounterAndUsetime.stat(1, usetimeMillis);
    }
    public static long getRunningCount(String service) {
        ServiceInvokedStatItem serviceInvokedStatItem = getServiceInvokedStat(service);
        return serviceInvokedStatItem.getRunningCount();
    }
    public static class ServiceInvokedStatItem {
        private AtomicLong runningCounter = new AtomicLong();
        private AtomicLong failCounter = new AtomicLong();
        private CounterAndUsetimeStat totalCounterAndUsetime = new CounterAndUsetimeStat();
        private CounterAndUsetimeStat latestCounterAndUsetime = new CounterAndUsetimeStat();

        public long getRunningCount() {
            return runningCounter.get();
        }

        public long failCount() {
            return failCounter.get();
        }

        public CounterAndUsetimeStat getTotalCounterAndUsetime() {
            return totalCounterAndUsetime;
        }

        public CounterAndUsetimeStat getLatestCounterAndUsetime() {
            return latestCounterAndUsetime;
        }

        public void resetLatestCountAndUsetimeStat() {
            latestCounterAndUsetime.reset();
        }

        public static class CounterAndUsetimeStat {
            private long startAt;
            private AtomicLong counter = new AtomicLong();
            private AtomicLong usetime = new AtomicLong();
            CounterAndUsetimeStat() {
                startAt = System.currentTimeMillis();
            }
            void stat(int count, long usetime) {
                counter.addAndGet(count);
                this.usetime.addAndGet(usetime);
            }
            public long getCount() {
                return counter.get();
            }
            public long getUsetime() {
                return usetime.get();
            }
            void reset() {
                startAt = System.currentTimeMillis();
                counter.set(0);
                usetime.set(0);
            }
        }
    }
    //限速统计for provider=================================================end
}
