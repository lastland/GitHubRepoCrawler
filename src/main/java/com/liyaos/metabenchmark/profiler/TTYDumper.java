package com.liyaos.metabenchmark.profiler;

/**
 * Created by lastland on 15/10/7.
 */
public class TTYDumper implements Dumper {
    public void close() {}
    public void println(String s) {
        System.out.println(s);
    }
}
