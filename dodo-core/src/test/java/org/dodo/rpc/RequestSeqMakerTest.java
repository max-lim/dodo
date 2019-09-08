package org.dodo.rpc;

import org.dodo.rpc.remoting.RequestSeqMaker;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;

/**
 * @author maxlim
 */
public class RequestSeqMakerTest {
    @Test
    public void bits() {
        long nowtime = System.currentTimeMillis();
        System.out.println(Long.toBinaryString(Long.MAX_VALUE));
        System.out.println(Long.toBinaryString(Long.MIN_VALUE));
        System.out.println(Long.toBinaryString(nowtime));
        System.out.println(Long.toBinaryString(nowtime).length());
        System.out.println(Long.toBinaryString(nowtime << 20));
    }
    @Test
    public void seq() {
        RequestSeqMaker maker = new RequestSeqMaker();
        long nowtime = System.currentTimeMillis();

        System.out.println(nowtime);

        long seq = maker.incrementAndGet(nowtime);
        System.out.println(seq);

        long pre = (seq >> RequestSeqMaker.MOVE_BITS);
        System.out.println(pre);

        Assert.assertEquals("cannot decode seq", nowtime, pre);
    }
}
