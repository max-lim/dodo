import io.netty.util.concurrent.FastThreadLocal;

import java.nio.ByteBuffer;

public class Test {

    public static void main(String[] args) {
        FastThreadLocal<String> fastThreadLocal = new FastThreadLocal<>();

        int array[] = {1,2,3,4,5,6,7,8,9,0};
        int count = 10000;
        long start = System.nanoTime();

        start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            long id = 3;
            int val = array[(int) id];
        }
        System.out.println("to int "+(System.nanoTime() - start));

        start = System.nanoTime();
        for (int i = 0; i < count; i++) {
            int id = 3;
            int val = array[id];
        }
        System.out.println("just int "+(System.nanoTime() - start));

//        System.out.println(System.nanoTime());
//        System.out.println(System.nanoTime() / 1000);
//        System.out.println(System.nanoTime() / 1000 / 1000);
//        System.out.println("----");
//
//        long start = System.currentTimeMillis();
//        long startnano = System.nanoTime();
//        System.out.println("start:" + startnano);
//        try {
//            Thread.sleep(1L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        long endnano = System.nanoTime();
//        System.out.println("end  :"+endnano);
//        System.out.println((endnano - startnano));
//        System.out.println((endnano - startnano)/1000/1000);
//        System.out.println("millis=" + (System.currentTimeMillis() - start));
//
//        System.out.println("----");
//        System.out.println("add=" + startnano);
//        System.out.println("add=" + (startnano + 3 *1000*1000L));
//        System.out.println("add=" + (startnano + 3 *1000*1000L)/1000/1000);
//
//        //millis * 1000*1000 --> nano
    }
}
