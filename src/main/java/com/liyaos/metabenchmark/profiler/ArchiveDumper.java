package com.liyaos.metabenchmark.profiler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by lastland on 15/10/6.
 */
public class ArchiveDumper implements Dumper {
    public PrintWriter p;

    public ArchiveDumper(String fileName) throws FileNotFoundException {
        p = new PrintWriter(fileName);
    }

    public void close() {
        p.close();
    }

    public void println(String s) {
        p.println(s);
    }
}
