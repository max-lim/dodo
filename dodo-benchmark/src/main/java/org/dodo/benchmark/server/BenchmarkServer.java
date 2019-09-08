package org.dodo.benchmark.server;

import org.dodo.ProviderBuilder;
import org.dodo.benchmark.service.BenchmarkService;
import org.dodo.benchmark.service.impl.BenchmarkServiceImpl;
import org.dodo.config.AppConfig;
import org.dodo.config.ProviderConfig;
import org.dodo.config.RegisterConfig;
import org.dodo.config.ServiceConfig;

/**
 *
 * @author maxlim
 */
public class BenchmarkServer {

    public static void main(String []args) throws Exception {
        //zookeeper 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183 0.0.0.0 8000 protostuff 16 16 1000

//        String registerType = args[0];
//        String registerHost = args[1];
//        String host = args[2];
//        int port = Integer.parseInt(args[3]);
//        String serialization = args[4];
//
//        int corePoolSize = Integer.parseInt(args[5]);
//        int maxPoolSize = Integer.parseInt(args[6]);
//        int workQueueSize = Integer.parseInt(args[7]);

        ProviderBuilder builder = new ProviderBuilder();
        builder.setAppConfig(new AppConfig("benchmark"))
                .setProviderConfig(new ProviderConfig("dodo","0.0.0.0",8000,50,50,1000,100000,"protostuff","javassist",null))
                .setRegisterConfig(new RegisterConfig("local4test","127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183"))
//                .setProviderConfig(new ProviderConfig("dodo",host, port, corePoolSize, maxPoolSize, workQueueSize,100000, serialization,null,null))
//                .setRegisterConfig(new RegisterConfig(registerType, registerHost))
                .addServiceConfig(new ServiceConfig(BenchmarkService.class, new BenchmarkServiceImpl()))
        ;
        builder.build();

        while(true) {
            Thread.sleep(1000L);
        }
    }
}
