package org.dodo.rpc.remoting;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 请求序号生成器：long的前43位为时间戳，后20位为递增，也就是每一毫秒有100多万（1048576）的id容量
 * @author maxlim
 *
 */
public class RequestSeqMaker {
    private AtomicInteger incr = new AtomicInteger();
    public static final int MOVE_BITS = 20;
    public static final int INCR_MAX = 1 << MOVE_BITS;

    public long incrementAndGet(long preLong) {
        int postfix = incr.incrementAndGet();
//        if (postfix > INCR_MAX) {
//            synchronized (this) {
//                if(postfix > INCR_MAX) {
//                    incr.set(0);
//                }
//            }
//            postfix = incr.incrementAndGet();
//        }
        while (postfix > INCR_MAX) {
            incr.compareAndSet(postfix, 0);
            postfix = incr.incrementAndGet();
        }
        return (preLong << MOVE_BITS) + postfix;
    }

    public long incrementAndGet() {
        return incrementAndGet((System.currentTimeMillis()));
    }
}
