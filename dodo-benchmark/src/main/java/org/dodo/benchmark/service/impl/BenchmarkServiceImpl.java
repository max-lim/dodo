package org.dodo.benchmark.service.impl;

import org.dodo.benchmark.service.BenchmarkService;
import org.dodo.benchmark.service.UserBean;

public class BenchmarkServiceImpl implements BenchmarkService {
    @Override
    public void empty() {
//        System.out.println("empty");
    }

    @Override
    public UserBean withBean(UserBean bean) {
//        System.out.println("withBean");
        return bean;
    }

    @Override
    public String withString(String string) {
//        System.out.println("withString");
        return string;
    }

    @Override
    public void asyncEmpty() {
//        System.out.println("asyncEmpty");
    }

    @Override
    public void asyncWithBean(UserBean bean) {
//        System.out.println("asyncWithBean");
    }

    @Override
    public void asyncWithString(String string) {
//        System.out.println("asyncWithString");
    }
}
