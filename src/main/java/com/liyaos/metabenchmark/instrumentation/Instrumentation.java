package com.liyaos.metabenchmark.instrumentation;

/**
 * Created by lastland on 15/9/10.
 */
import com.liyaos.metabenchmark.instrumentation.Profiler;
import ch.usi.dag.disl.annotation.After;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import ch.usi.dag.disl.marker.BodyMarker;

public class Instrumentation {

    @Before(marker = BodyMarker.class, scope = "java.util.concurrent.ThreadPoolExecutor.beforeExecute")
    public static void test(DynamicContext dc) {
        Profiler.poolBegin(dc.getMethodArgumentValue(0, Thread.class),
                dc.getMethodArgumentValue(1, Runnable.class));
    }

    @After(marker = BodyMarker.class, scope = "java.util.concurrent.ThreadPoolExecutor.afterExecute")
    public static void end(DynamicContext dc) {
        Profiler.poolEnd(dc.getMethodArgumentValue(0, Runnable.class));
    }
}
