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

    /*
    //@After(marker = BodyMarker.class, scope = "org.apache.commons.collections4.bag.HashBag.getCount")
    @After(marker = BodyMarker.class, scope = "com.liyaos.concurrent.factorial.*.*")
    public static void allOfthem(DynamicContext dc) {
        Object m = dc.getThis();
        if (m != null) {
            Profiler.log(m);
        }
    }

    @Before(marker = BodyMarker.class, scope="java.lang.ProcessImpl.start")
    static void profile(DynamicContext dc) {
        String[] cmd = (String[]) dc.getMethodArgumentValue(0, Object.class);
        for (String c : cmd) {
            System.out.println( c );
        }
    }
    */
}
