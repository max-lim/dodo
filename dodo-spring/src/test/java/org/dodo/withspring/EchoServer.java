package org.dodo.withspring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author maxlim
 */
public class EchoServer {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] {"classpath*:echo_server.xml"});
    }
}
