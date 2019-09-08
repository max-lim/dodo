package org.dodo.withspring.impl;

import org.dodo.withspring.EchoService;

/**
 * @author maxlim
 */
public class EchoServiceImpl implements EchoService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
