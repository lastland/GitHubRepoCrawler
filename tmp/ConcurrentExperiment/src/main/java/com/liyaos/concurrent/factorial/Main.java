package com.liyaos.concurrent.factorial;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Created by lastland on 15/10/6.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService s = Executors.newFixedThreadPool(2);
        Runnable r = new Factorial();
        s.execute(r);
        s.execute(r);
        s.execute(r);
        s.shutdown();
        while (!s.isTerminated()) {
            Thread.sleep(500);
        }
        MyBag m = new MyBag();
        m.addObject(s);
        System.out.println(m.getObject(s));
    }
}
