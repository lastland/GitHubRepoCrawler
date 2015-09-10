package com.liyaos.metabenchmark.instrumentation;

/**
 * Created by lastland on 15/9/10.
 */
import ch.usi.dag.disl.annotation.After;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import ch.usi.dag.disl.marker.BodyMarker;

public class Instrumentation {

    @Before(marker = BodyMarker.class, scope = "java.util.concurrent.ThreadPoolExecutor.beforeExecute")
    public static void test(DynamicContext dc) {
        System.out.println(dc.getMethodArgumentValue(0, Thread.class));
        //Profiler.poolBegin(dc.getMethodArgumentValue(0, Thread.class),
        //dc.getMethodArgumentValue(1, TaskInvocation.class));
    }

    @After(marker = BodyMarker.class, scope = "java.util.concurrent.ThreadPoolExecutor.afterExecute")
    public static void end(DynamicContext dc) {
        System.out.println(dc.getMethodArgumentValue(0, Thread.class));
        //Profiler.poolEnd(dc.getMethodArgumentValue(0, TaskInvocation.class));
    }
}
