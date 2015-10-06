package com.liyaos.metabenchmark.profiler;

/**
 * Created by lastland on 15/10/6.
 */
public interface Dumper extends AutoCloseable {
    public void close();
    public void println(String s);
}
