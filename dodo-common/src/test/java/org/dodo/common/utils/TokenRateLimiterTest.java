package org.dodo.common.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author maxlim
 */
public class TokenRateLimiterTest {
    TokenRateLimiter limiter;
    ConcurrentHashMap<Long, AtomicInteger> counterSum = new ConcurrentHashMap<>();
    @Before
    public void init() {
        limiter = new TokenRateLimiter(1000, 1000);
    }

    @Test
    public void threads() throws InterruptedException {
        int cnt = 100;
        CountDownLatch countDownLatch = new CountDownLatch(cnt);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(cnt);
        ConcurrentHashMap<Long, AtomicInteger> counter = new ConcurrentHashMap<>();
        ConcurrentHashMap<Long, AtomicInteger> failCounter = new ConcurrentHashMap<>();
        for (int i = 0; i < cnt; i++) {
            new Thread(()->{
                try {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    boolean acquire = limiter.acquire();
                    ConcurrentHashMap<Long, AtomicInteger> statCounter = failCounter;
                    if (acquire) {
                        statCounter = counter;
                        counter(counterSum);
                    }
                    counter(statCounter);
                } finally {
                    countDownLatch.countDown();
                }

            }).start();
        }
        countDownLatch.await();
//        counter.forEach((k, v)->{
//            System.out.println("success:" + k + " " + v);
//        });
//        failCounter.forEach((k, v)->{
//            System.out.println("fail:" + k + " " + v);
//        });
//        System.out.println("=====");
    }

    public void counter(ConcurrentHashMap<Long, AtomicInteger> statCounter) {
        long currentTime = System.currentTimeMillis()/1000;
        AtomicInteger itemCounter = statCounter.get(currentTime);
        if (itemCounter == null) {
            statCounter.putIfAbsent(currentTime, new AtomicInteger());
            itemCounter = statCounter.get(currentTime);
        }
        itemCounter.incrementAndGet();
    }

    @Test
    public void runTime() throws InterruptedException {
        long start = System.currentTimeMillis();
        long runTime = 10 * 1000;
        do {
            threads();
            if (System.currentTimeMillis() - start >= runTime) {
                break;
            }
        } while (true);
        counterSum.forEach((k, v)->{
            System.out.println("success:" + k + " " + v);
        });
    }
}
