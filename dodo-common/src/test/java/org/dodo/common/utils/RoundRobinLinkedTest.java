package org.dodo.common.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author maxlim
 */
public class RoundRobinLinkedTest {
    RoundRobinLinked roundRobinLinked;
    Map<String, Integer> nodesWithWeightsOrdered;
    int weights = 0;
    Map<String, AtomicInteger> selectedCounter = new HashMap<>();
    @Before
    public void init() {
        Map<String, Integer> nodesWithWeights = new HashMap<>();
        nodesWithWeights.put("a", 1);
        nodesWithWeights.put("b", 2);
        nodesWithWeights.put("c", 3);
        weights = 6;
        selectedCounter.put("a", new AtomicInteger());
        selectedCounter.put("b", new AtomicInteger());
        selectedCounter.put("c", new AtomicInteger());

        nodesWithWeightsOrdered = new LinkedHashMap<>(nodesWithWeights.size());
        nodesWithWeights.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).forEachOrdered(e -> nodesWithWeightsOrdered.put(e.getKey(), e.getValue()));
        roundRobinLinked = RoundRobinLinked.build(nodesWithWeightsOrdered);
        nodesWithWeightsOrdered.forEach((k,v)->{
            System.out.println(k + ":" + v);
        });
        System.out.println("=====init end=====");
    }

    @Test
    public void roundRobin() {
        int cnt = 10;
        for (int i = 0; i < cnt; i++) {
            System.out.println(roundRobinLinked.selectKey());
        }
    }

    @Test
    public void threads() throws InterruptedException {
        int cnt = weights * 10;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(cnt);
        CountDownLatch countDownLatch = new CountDownLatch(cnt);
        for (int i = 0; i < cnt; i++) {
            new Thread(()->{
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
                try {
                    String selected = roundRobinLinked.selectKey();
                    selectedCounter.get(selected).incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
        selectedCounter.forEach((k,v)->{
            System.out.println((k + ":" + v));
        });
    }
}
