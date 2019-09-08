package org.dodo.withspring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author maxlim
 */
public class EchoClient {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] {"classpath*:echo_client.xml"});
        EchoService echoService = applicationContext.getBean(EchoService.class);
        System.out.println(echoService.hello("maxlim"));
        Thread.sleep(10 * 1000);
    }
}
