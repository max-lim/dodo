package org.dodo.rpc.serialize;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TestSeria {
    UserBean bean;
    int threadCnt = 2000;
    int cnt = 100;
    @Before
    public void init() {
        bean = bean();
    }
    private UserBean bean() {
        UserBean bean = new UserBean();
        bean.setAge(1);
        bean.setEmail("linbzh@163.com");
        bean.setId(8);
        bean.setName("maxlim");
        bean.setPhone("13537567813");
        bean.setScore(88888888);
        bean.setSex(1);
//        bean.setCreateAt(new Date());
        bean.setOther(new UserItemBean(8, 6, 88, new Date(), new Date()));
//        bean.setItems(Arrays.asList(new UserItemBean(8, 1, 88, new Date(), new Date()),
//            new UserItemBean(8, 2, 88, new Date(), new Date()),
//            new UserItemBean(8, 3, 88, new Date(), new Date()),
//            new UserItemBean(8, 4, 88, new Date(), new Date()),
//            new UserItemBean(8, 5, 88, new Date(), new Date())));
//        bean.getItemsMap().put(1, new UserItemBean(8, 7, 88, new Date(), new Date()));
//        bean.setBig(new BigDecimal("99999999999999999"));
        return bean;
    }

    @Test
    public void fastjson_usetime_multithread() throws IOException {
        AtomicInteger seriaUsetime = new AtomicInteger();
        AtomicInteger deseriaUsetime = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);
        JSONSerialization jsonSerialization = new JSONSerialization();
        jsonSerialization.serialize(bean());
        for (int j = 0; j < threadCnt; j++) {
            new Thread(() -> {

                try {
                    long start = System.nanoTime();
                    for(int i=0; i<cnt; i++) {
                        jsonSerialization.serialize(bean());
                    }
                    seriaUsetime.addAndGet((int) ((System.nanoTime() - start)/1000/1000));

                    byte[] bytes = jsonSerialization.serialize(bean());
                    start = System.nanoTime();
                    for(int i=0; i<cnt; i++) {
                        jsonSerialization.deserialize(bytes, UserBean.class);
                    }
                    deseriaUsetime.addAndGet((int) ((System.nanoTime() - start)/1000/1000));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    countDownLatch.countDown();
                }

            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("fastjson serialize,usetime="+ seriaUsetime.get());
        System.out.println("fastjson deserialize,usetime="+ deseriaUsetime.get());
    }
    @Test
    public void fastjson_usetime() throws IOException {
        JSONSerialization jsonSerialization = new JSONSerialization();
        byte[] bytes = jsonSerialization.serialize(bean());
//        System.out.println("fastjson " + new String(bytes));
        long start = System.nanoTime();
        for(int i=0; i<cnt; i++) {
            jsonSerialization.serialize(bean());
        }
        System.out.println("fastjson serialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);

        jsonSerialization.deserialize(bytes, UserBean.class);
        start = System.nanoTime();
        for(int i=0; i<cnt; i++) {
            jsonSerialization.deserialize(bytes, UserBean.class);
        }
        System.out.println("fastjson deserialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);

        byte[] exBytes = jsonSerialization.serialize(new IOException("test io throw"));
        Exception ex = jsonSerialization.deserialize(exBytes, Exception.class);
        System.out.println(ex.getClass()  + " " + ex.getMessage());
    }

    @Test
    public void fst_usetime_multithread() throws IOException {
        AtomicInteger seriaUsetime = new AtomicInteger();
        AtomicInteger deseriaUsetime = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);
        FSTSerialization fstSerialization = new FSTSerialization();
        for (int j = 0; j < threadCnt; j++) {
            new Thread(() -> {

                try {
                    long start = System.nanoTime();
                    for(int i=0; i<cnt; i++) {
                        fstSerialization.serialize(bean());
                    }
                    seriaUsetime.addAndGet((int) ((System.nanoTime() - start)/1000/1000));

                    byte[] bytes = fstSerialization.serialize(bean());
                    start = System.nanoTime();
                    for(int i=0; i<cnt; i++) {
                        fstSerialization.deserialize(bytes, UserBean.class);
                    }
                    deseriaUsetime.addAndGet((int) ((System.nanoTime() - start)/1000/1000));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    countDownLatch.countDown();
                }

            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("fst serialize,usetime="+ seriaUsetime.get());
        System.out.println("fst deserialize,usetime="+ deseriaUsetime.get());
    }
    @Test
    public void fst_usetime() throws IOException {
        FSTSerialization fstSerialization = new FSTSerialization();
        byte[] bytes = fstSerialization.serialize(bean());
        long start = System.nanoTime();
        for(int i=0; i<cnt; i++) {
            fstSerialization.serialize(bean());
        }
        System.out.println("fst serialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);

        fstSerialization.deserialize(bytes, UserBean.class);
        start = System.nanoTime();
        for(int i=0; i<cnt; i++) {
            fstSerialization.deserialize(bytes, UserBean.class);
        }
        System.out.println("fst deserialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);

        byte[] exBytes = fstSerialization.serialize(new IOException("test io throw"));
        Exception ex = fstSerialization.deserialize(exBytes, Exception.class);
        System.out.println(ex.getClass()  + " " + ex.getMessage());
    }

    @Test
    public void protostuff_usetime_multithread() throws IOException {
        AtomicInteger seriaUsetime = new AtomicInteger();
        AtomicInteger deseriaUsetime = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);
        ProtostuffSerialization protostuffSerialization = new ProtostuffSerialization();
        for (int j = 0; j < threadCnt; j++) {
            new Thread(() -> {

                try {
                    protostuffSerialization.serialize(bean());
                    long start = System.nanoTime();
                    for(int i=0; i<cnt; i++) {
                        protostuffSerialization.serialize(bean());
                    }
                    seriaUsetime.addAndGet((int) ((System.nanoTime() - start)/1000/1000));

                    byte[] bytes = protostuffSerialization.serialize(bean());
                    start = System.nanoTime();
                    for(int i=0; i<cnt; i++) {
                        protostuffSerialization.deserialize(bytes, UserBean.class);
                    }
                    deseriaUsetime.addAndGet((int) ((System.nanoTime() - start)/1000/1000));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    countDownLatch.countDown();
                }

            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("protostuff serialize,usetime="+ seriaUsetime.get());
        System.out.println("protostuff deserialize,usetime="+ deseriaUsetime.get());
    }
    @Test
    public void protostuff_usetime() throws IOException {
        ProtostuffSerialization protostuffSerialization = new ProtostuffSerialization();
        byte[] bytes = protostuffSerialization.serialize(bean());
        long start = System.nanoTime();
        for(int i=0; i<cnt; i++) {
            protostuffSerialization.serialize(bean());
        }
        System.out.println("protostuff serialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);

        protostuffSerialization.deserialize(bytes, UserBean.class);
        start = System.nanoTime();
        for(int i=0; i<cnt; i++) {
            protostuffSerialization.deserialize(bytes, UserBean.class);
        }
        System.out.println("protostuff deserialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);

        byte[] exBytes = protostuffSerialization.serialize(new IOException("test io throw"));
        Exception ex = protostuffSerialization.deserialize(exBytes, Exception.class);
        System.out.println(ex.getClass()  + " " + ex.getMessage());
    }

    @Test
    public void java_usetime_multithread() throws IOException {
        AtomicInteger seriaUsetime = new AtomicInteger();
        AtomicInteger deseriaUsetime = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);
        JavaSerialization javaSerialization = new JavaSerialization();
        for (int j = 0; j < threadCnt; j++) {
            new Thread(() -> {

                try {
                    long start = System.nanoTime();
                    for(int i=0; i<cnt; i++) {
                        javaSerialization.serialize(bean());
                    }
                    seriaUsetime.addAndGet((int) ((System.nanoTime() - start)/1000/1000));

                    byte[] bytes = javaSerialization.serialize(bean());
                    start = System.nanoTime();
                    for(int i=0; i<cnt; i++) {
                        javaSerialization.deserialize(bytes, UserBean.class);
                    }
                    deseriaUsetime.addAndGet((int) ((System.nanoTime() - start)/1000/1000));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    countDownLatch.countDown();
                }

            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("java serialize,usetime="+ seriaUsetime.get());
        System.out.println("java deserialize,usetime="+ deseriaUsetime.get());
    }

    @Test
    public void java_usetime() throws IOException {
        JavaSerialization javaSerialization = new JavaSerialization();
        byte[] bytes = javaSerialization.serialize(bean());
        long start = System.nanoTime();
        for(int i=0; i<cnt; i++) {
            javaSerialization.serialize(bean());
        }
        System.out.println("java serialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);

        javaSerialization.deserialize(bytes, UserBean.class);
        start = System.nanoTime();
        for(int i=0; i<cnt; i++) {
            javaSerialization.deserialize(bytes, UserBean.class);
        }
        System.out.println("java deserialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);

        byte[] exBytes = javaSerialization.serialize(new IOException("test io throw"));
        Exception ex = javaSerialization.deserialize(exBytes, Exception.class);
        System.out.println(ex.getClass()  + " " + ex.getMessage());
    }

//    @Test
//    public void jsoniter_usetime() throws IOException {
//        JsoniterSerialization jsoniterSerialization = new JsoniterSerialization();
//        byte[] bytes = jsoniterSerialization.serialize(bean());
////        System.out.println("jsoniter " + new String(bytes));
//        long start = System.nanoTime();
//        for(int i=0; i<cnt; i++) {
//            jsoniterSerialization.serialize(bean());
//        }
//        System.out.println("jsoniter serialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);
//
//        //date类型支持得不好
////        jsoniterSerialization.deserialize(bytes, UserBean.class);
////        start = System.nanoTime();
////        for(int i=0; i<cnt; i++) {
////            jsoniterSerialization.deserialize(bytes, UserBean.class);
////        }
////        System.out.println("jsoniter deserialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);
//
//        //不支持exception序列化
////        byte[] exBytes = jsoniterSerialization.serialize(new IOException("test io throw"));
////        Exception ex = jsoniterSerialization.deserialize(exBytes, Exception.class);
////        System.out.println(ex.getClass()  + " " + ex.getMessage());
//    }


    @Test
    public void dsljson_usetime_multithread() throws IOException {
        AtomicInteger seriaUsetime = new AtomicInteger();
        AtomicInteger deseriaUsetime = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(threadCnt);
        DSLJsonSerialization dslJsonSerialization = new DSLJsonSerialization();
        for (int j = 0; j < threadCnt; j++) {
            new Thread(() -> {

                try {
                    dslJsonSerialization.serialize(bean());
                    long start = System.nanoTime();
                    for(int i=0; i<cnt; i++) {
                        dslJsonSerialization.serialize(bean());
                    }
                    seriaUsetime.addAndGet((int) ((System.nanoTime() - start)/1000/1000));

                    byte[] bytes = dslJsonSerialization.serialize(bean());
                    start = System.nanoTime();
                    for(int i=0; i<cnt; i++) {
                        dslJsonSerialization.deserialize(bytes, UserBean.class);
                    }
                    deseriaUsetime.addAndGet((int) ((System.nanoTime() - start)/1000/1000));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    countDownLatch.countDown();
                }

            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("dsljson serialize,usetime="+ seriaUsetime.get());
        System.out.println("dsljson deserialize,usetime="+ deseriaUsetime.get());
    }
    @Test
    public void dsljson_usetime() throws IOException {
        DSLJsonSerialization dslJsonSerialization = new DSLJsonSerialization();
        byte[] bytes = dslJsonSerialization.serialize(bean());
        System.out.println("dsljson " + new String(bytes));
        long start = System.nanoTime();
        for(int i=0; i<cnt; i++) {
            dslJsonSerialization.serialize(bean());
        }
        System.out.println("dsljson serialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);

        dslJsonSerialization.deserialize(bytes, UserBean.class);
        start = System.nanoTime();
        for(int i=0; i<cnt; i++) {
            dslJsonSerialization.deserialize(bytes, UserBean.class);
        }
        System.out.println("dsljson deserialize,size="+bytes.length+",usetime="+(System.nanoTime() - start)/1000/1000);

        //不支持exception序列化
//        byte[] exBytes = dslJsonSerialization.serialize(new IOException("test io throw"));
//        Exception ex = dslJsonSerialization.deserialize(exBytes, Exception.class);
//        System.out.println(ex.getClass()  + " " + ex.getMessage());
    }

}
