package com.liyaos.concurrent.factorial;

import java.math.BigInteger;

/**
 * Created by lastland on 15/9/30.
 */
public class Factorial implements Runnable {

    @Override
    public void run() {
        BigInteger res = BigInteger.valueOf(1);
        for (long i = 1; i < 10000; i++) {
            res.multiply(BigInteger.valueOf(i));
        }
        MyBag m = new MyBag();
        m.addObject(res);
        m.getObject(res);
    }

    @Override
    public String toString() {
        return "com.liyaos.concurrent.factorial.Factorial" + hashCode();
    }
}
