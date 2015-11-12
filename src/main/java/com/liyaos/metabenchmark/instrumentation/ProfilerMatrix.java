package com.liyaos.metabenchmark.instrumentation;

/**
 * Created by lastland on 15/9/10.
 */

import com.liyaos.metabenchmark.MainArguments;
import com.liyaos.metabenchmark.profiler.ArchiveDumper;
import com.liyaos.metabenchmark.profiler.Dumper;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class ProfilerMatrix extends Profiler {

    public static void dump() {
        try {
            Dumper dumper = new ArchiveDumper("results" + java.lang.management.ManagementFactory.getRuntimeMXBean().getName());
            runnings(dumper);

            dumper.println("====> Checking Matrix size");
            for (Map.Entry<Long, Integer> tuple : arraySizes.entrySet()) {
                if (tuple.getValue() > MainArguments.matrixSizeTreshold()) {
                    dumper.println("====> Found Matrix of size " + tuple.getValue());

                    break;
                } else {
                    dumper.println("====> Discarded Matrix of size " + tuple.getValue());
                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}
