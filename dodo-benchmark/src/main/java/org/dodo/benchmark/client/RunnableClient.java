package org.dodo.benchmark.client;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Function;

public class RunnableClient implements Runnable {
    private Function serviceCall;
    private int warmupTime;
    private int runTime;
    private CyclicBarrier cyclicBarrier;
    private CountDownLatch countDownLatch;
    private RunnableStatistics statistics;

    public RunnableClient(Function serviceCall, int warmupTime, int runTime, CyclicBarrier cyclicBarrier, CountDownLatch countDownLatch) {
        this.serviceCall = serviceCall;
        this.warmupTime = warmupTime;
        this.runTime = runTime;
        this.cyclicBarrier = cyclicBarrier;
        this.countDownLatch = countDownLatch;

        this.statistics = new RunnableStatistics(runTime);
    }

    public RunnableStatistics getStatistics() {
        return statistics;
    }

    @Override
    public void run() {
        try {
            this.cyclicBarrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        doRun();

        this.countDownLatch.countDown();

    }

    private void doRun() {
        long nowTime = System.nanoTime();
        long warmUpEnd = System.nanoTime() + warmupTime *1000*1000*1000L;
        while(warmUpEnd >= nowTime) {
            call();
            nowTime = System.nanoTime();
        }
        nowTime = System.nanoTime();
        long startAt = System.nanoTime();
        long runEnd = System.nanoTime() + runTime *1000*1000*1000L;
        while (runEnd >= nowTime) {
            long runStartAt = System.nanoTime();
            Object res = call();
            long useTime = (System.nanoTime() - runStartAt)/1000/1000;
            collectUseTime(useTime);

            int index = (int) ((System.nanoTime() - startAt) / 1000000000L);
            if(index >= statistics.tps.length) {
                index = statistics.tps.length - 1;
            }
            if(res != null) {
                //System.out.println("statistics.rt="+statistics.rt.length+",statistics.tps="+statistics.tps.length+",index="+index);
                statistics.tps[index]++;
                statistics.rt[index] += useTime;
            }
            else {
                statistics.errTps[index]++;
                statistics.errRt[index] += useTime;
            }
            nowTime = System.nanoTime();
        }

    }

    private void collectUseTime(long useTime) {
        if(useTime >= 0 && useTime <= 1) {
            statistics.bw0_1 ++;
        }
        else if(useTime > 1 && useTime <= 5) {
            statistics.bw1_5 ++;
        }
        else if(useTime > 5 && useTime <= 10) {
            statistics.bw5_10 ++;
        }
        else if(useTime > 10 && useTime <= 50) {
            statistics.bw10_50 ++;
        }
        else if(useTime > 50 && useTime <= 100) {
            statistics.bw50_100 ++;
        }
        else if(useTime > 100 && useTime <= 500) {
            statistics.bw100_500 ++;
        }
        else if(useTime > 500 && useTime <= 1000) {
            statistics.bw500_1000 ++;
        }
        else if(useTime > 1000 && useTime <= 1500) {
            statistics.bw1000_1500 ++;
        }
        else if(useTime > 1500 && useTime <= 2000) {
            statistics.bw1500_2000 ++;
        }
        else if(useTime > 2000 && useTime <= 2500) {
            statistics.bw2000_2500 ++;
        }
        else if(useTime > 2500 && useTime <= 3000) {
            statistics.bw2500_3000 ++;
        }
        else if(useTime > 3000) {
            statistics.bw3000_more ++;
        }
    }
    private Object call() {
        return serviceCall.apply(null);
    }
}
