package org.dodo.benchmark.service;

public interface BenchmarkService {

    public void empty();

    public UserBean withBean(UserBean bean);

    public String withString(String string);

    public void asyncEmpty();

    public void asyncWithBean(UserBean bean);

    public void asyncWithString(String string);

}
