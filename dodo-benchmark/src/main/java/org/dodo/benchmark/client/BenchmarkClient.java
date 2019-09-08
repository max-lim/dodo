package org.dodo.benchmark.client;

import org.dodo.ConsumerBuilder;
import org.dodo.benchmark.service.BenchmarkService;
import org.dodo.config.AppConfig;
import org.dodo.config.ConsumerConfig;
import org.dodo.config.ReferenceConfig;
import org.dodo.config.RegisterConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.function.Function;

/**
 *
 * @author maxlim
 */
public class BenchmarkClient {

    private static ConsumerBuilder builder;
    private static ServiceFunctionFactory serviceFunctionFactory;
    public static void init() throws Exception {
        builder = new ConsumerBuilder();
        builder.setAppConfig(new AppConfig("benchmark"))
                .setConsumerConfig(new ConsumerConfig("javassist","protostuff"))
//                .setRegisterConfig(new RegisterConfig("zookeeper","127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183"))
                .setRegisterConfig(new RegisterConfig("local4test",""))
                .addReferenceConfig(new ReferenceConfig(BenchmarkService.class))
        ;
        builder.build();
        BenchmarkService benchmarkService = builder.reflect(BenchmarkService.class);
        serviceFunctionFactory = new ServiceFunctionFactory(benchmarkService);

        Thread.sleep(5 * 1000);
    }

    public static void main(String[] args) throws Exception {

//        if(args.length < 4) {
//            System.out.println("usage: warmupTime(seconds) runTime(seconds) threadNumber function");
//            System.exit(1);
//        }
//        int warmupTime = Integer.parseInt(args[0]);
//        int runTime = Integer.parseInt(args[1]);
//        int threadNumber = Integer.parseInt(args[2]);
//        String function = args[3];

        int warmupTime = 10;
        int runTime = 30;
        int threadNumber = 20;
        String function = "empty";

        init();

        Function serviceCall = serviceFunctionFactory.getFunction(function);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(threadNumber);
        CountDownLatch countDownLatch = new CountDownLatch(threadNumber);

        List<RunnableClient> clients = new ArrayList<>();
        for(int i=0; i<threadNumber; i++) {
            RunnableClient client = new RunnableClient(serviceCall, warmupTime, runTime, cyclicBarrier, countDownLatch);
            clients.add(client);

            new Thread(client,"RunnableClient-"+i).start();
        }

        countDownLatch.await();

        List<RunnableStatistics> statisticsList = new ArrayList<>();
        for (RunnableClient client: clients) {
            statisticsList.add(client.getStatistics());
        }

        BenchmarkStatistics statistics = new BenchmarkStatistics(runTime, statisticsList);
        statistics.compute();
        statistics.print();

        System.exit(0);
    }
}
