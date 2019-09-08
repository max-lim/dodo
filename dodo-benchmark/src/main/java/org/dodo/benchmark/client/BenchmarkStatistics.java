package org.dodo.benchmark.client;

import java.text.MessageFormat;
import java.util.List;

/**
 * 测试统计
 * @author maxlim
 */
public class BenchmarkStatistics {
    public int runTime;
    public long maxTps;
    public long minTps;
    public long successTps;
    public long successRt;
    public long errTps;
    public long errRt;
    public long allRequest;
    public long allRt;

    public long bw0_1;
    public long bw1_5;
    public long bw5_10;
    public long bw10_50;
    public long bw50_100;
    public long bw100_500;
    public long bw500_1000;
    public long bw1000_1500;
    public long bw1500_2000;
    public long bw2000_2500;
    public long bw2500_3000;
    public long bw3000_more;

    public List<RunnableStatistics> runnableStatisticsList;

    public BenchmarkStatistics(int runTime, List<RunnableStatistics> runnableStatisticsList) {
        this.runTime = runTime;
        this.runnableStatisticsList = runnableStatisticsList;
    }

    public void compute() {
        for (RunnableStatistics runnableStatistics : runnableStatisticsList) {
            bw0_1 += runnableStatistics.bw0_1;
            bw1_5 += runnableStatistics.bw1_5;
            bw5_10 += runnableStatistics.bw5_10;
            bw10_50 += runnableStatistics.bw10_50;
            bw50_100 += runnableStatistics.bw50_100;
            bw100_500 += runnableStatistics.bw100_500;
            bw500_1000 += runnableStatistics.bw500_1000;
            bw1000_1500 += runnableStatistics.bw1000_1500;
            bw1500_2000 += runnableStatistics.bw1500_2000;
            bw2000_2500 += runnableStatistics.bw2000_2500;
            bw2500_3000 += runnableStatistics.bw2500_3000;
            bw3000_more += runnableStatistics.bw3000_more;
        }

        for (int i=0; i<runTime; i++) {
            long runnableTps = 0L;
            for (RunnableStatistics runnableStatistics : runnableStatisticsList) {
                runnableTps += runnableStatistics.tps[i] + runnableStatistics.errTps[i];
                successTps += runnableStatistics.tps[i];
                successRt += runnableStatistics.rt[i];
                errTps += runnableStatistics.errTps[i];
                errRt += runnableStatistics.errRt[i];
            }
            if(runnableTps > maxTps) {
                maxTps = runnableTps;
            }
            if(runnableTps < minTps || minTps == 0) {
                minTps = runnableTps;
            }
        }
        allRequest = successTps + errTps;
        allRt = successRt + errRt;
    }

    public void print() {
        System.out.println("benchmark run time: " + runTime);

        System.out.println(MessageFormat.format("allRequest: {0,number,#.##}, success: {1,number,#.##}%({2,number,#.##}), err: {3,number,#.##}%({4,number,#.##})", allRequest, successTps * 100 / allRequest, successTps, errTps * 100 / allRequest, errTps));
        System.out.println(MessageFormat.format("avg tps: {0,number,#.##}, max tps: {1,number,#.##}, min tps: {2,number,#.##}", (allRequest / runTime), maxTps, minTps));
        System.out.println(MessageFormat.format("avg response time: {0,number,#.##}ms", allRt / allRequest * 1.0D));

        System.out.println(MessageFormat.format("response time [0,1]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw0_1 * 100 / allRequest, bw0_1, allRequest));
        System.out.println(MessageFormat.format("response time (1,5]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw1_5 * 100 / allRequest, bw1_5, allRequest));
        System.out.println(MessageFormat.format("response time (5,10]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw5_10 * 100 / allRequest, bw5_10, allRequest));
        System.out.println(MessageFormat.format("response time (10,50]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw10_50 * 100 / allRequest, bw10_50, allRequest));
        System.out.println(MessageFormat.format("response time (50,100]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw50_100 * 100 / allRequest, bw50_100, allRequest));
        System.out.println(MessageFormat.format("response time (100,500]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw100_500 * 100 / allRequest, bw100_500, allRequest));
        System.out.println(MessageFormat.format("response time (500,1000]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw500_1000 * 100 / allRequest, bw500_1000, allRequest));
        System.out.println(MessageFormat.format("response time (1000, 1500]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw1000_1500 * 100 / allRequest, bw1000_1500, allRequest));
        System.out.println(MessageFormat.format("response time (1500, 2000]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw1500_2000 * 100 / allRequest, bw1500_2000, allRequest));
        System.out.println(MessageFormat.format("response time (2000, 2500]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw2000_2500 * 100 / allRequest, bw2000_2500, allRequest));
        System.out.println(MessageFormat.format("response time (2500, 3000]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw2500_3000 * 100 / allRequest, bw2500_3000, allRequest));
        System.out.println(MessageFormat.format("response time (3000, ----]: {0,number,#.##}% {1,number,#.##}/{2,number,#.##}", bw3000_more * 100 / allRequest, bw3000_more, allRequest));
    }
}
