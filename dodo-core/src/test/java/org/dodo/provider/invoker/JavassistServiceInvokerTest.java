package org.dodo.provider.invoker;

import org.dodo.reflect.EchoService;
import org.dodo.reflect.EchoServiceImpl;
import org.dodo.rpc.serialize.UserBean;
import org.junit.Test;

/**
 * @author maxlim
 */
public class JavassistServiceInvokerTest {
    @Test
    public void test() throws Exception {
        JavassistServiceInvoker javassistServiceInvoker = new JavassistServiceInvoker();
        javassistServiceInvoker.register(EchoService.class.getName(), new EchoServiceImpl());
        System.out.println(javassistServiceInvoker.invoke(EchoService.class.getName(), "hello", new Object[]{"max lim"}));

        UserBean userBean = new UserBean();
        userBean.setAge(100);
        userBean.setName("max");
        userBean.setId(888);
        userBean.setPhone("13537567813");
        UserBean returnUserBean = (UserBean) javassistServiceInvoker.invoke(EchoService.class.getName(), "withBean", new Object[]{userBean});
        System.out.println(returnUserBean);

        javassistServiceInvoker.invoke(EchoService.class.getName(), "returnVoid", null);
    }
}
