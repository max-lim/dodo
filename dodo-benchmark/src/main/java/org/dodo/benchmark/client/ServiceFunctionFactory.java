package org.dodo.benchmark.client;

import org.dodo.benchmark.service.BenchmarkService;
import org.dodo.benchmark.service.UserBean;
import org.dodo.benchmark.service.UserItemBean;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

public class ServiceFunctionFactory {
    public BenchmarkService service;
    private UserBean bean;

    public ServiceFunctionFactory(BenchmarkService service) {
        this.service = service;

        bean = new UserBean();
        bean.setAge(1);
        bean.setEmail("linbzh@163.com");
        bean.setId(8);
        bean.setItems(Arrays.asList(new UserItemBean(8, 1, 88, new Date(), new Date()),
                new UserItemBean(8, 2, 88, new Date(), new Date()),
                new UserItemBean(8, 3, 88, new Date(), new Date()),
                new UserItemBean(8, 4, 88, new Date(), new Date()),
                new UserItemBean(8, 5, 88, new Date(), new Date())));
        bean.setName("maxlim");
        bean.setPhone("13537567813");
        bean.setScore(88888888);
        bean.setSex(1);
    }

    public Function getFunction(String function) {
        switch (function) {
            case "empty": return emptyFunction();
            case "withString": return withStringFunction();
            case "withBean": return withBeanFunction();

        }
        return emptyFunction();
    }

    public Function emptyFunction() {
        return o -> {
            try {
                service.empty();
                return "";
            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    public Function withStringFunction() {
        return o -> {
            try {
                return service.withString("hello benchmark testing server");
            }catch (Exception e) {
                return null;
            }
        };
    }

    public Function withBeanFunction() {
        return o -> {
            try {
                return service.withBean(bean);
            }catch (Exception e) {
                return null;
            }
        };
    }

    public Function asyncEmptyFunction() {
        return o -> {
            try {
                service.asyncEmpty();
                return 1;
            } catch (Exception e) {
                return null;
            }
        };
    }

    public Function asyncWithStringFunction() {
        return o -> {
            try {
                service.asyncWithString("hello benchmark testing server");
                return 1;
            } catch (Exception e) {
                return null;
            }
        };
    }

    public Function asyncWithBeanFunction() {
        return o -> {
            try {
                service.asyncWithBean(bean);
                return 1;
            } catch (Exception e) {
                return null;
            }
        };
    }
}
