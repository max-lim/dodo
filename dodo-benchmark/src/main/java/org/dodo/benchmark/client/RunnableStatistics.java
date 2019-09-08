package org.dodo.benchmark.client;

public class RunnableStatistics {
    public long[] tps;
    public long[] errTps;
    public long[] rt;
    public long[] errRt;

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


    public RunnableStatistics(int runTime) {
        tps = new long[runTime];
        rt = new long[runTime];
        errTps = new long[runTime];
        errRt = new long[runTime];
    }
}
