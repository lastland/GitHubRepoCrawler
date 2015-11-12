package com.liyaos.metabenchmark.instrumentation;

/**
 * Created by lastland on 15/9/10.
 */

import ch.usi.dag.disl.annotation.*;
import ch.usi.dag.disl.annotation.ThreadLocal;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.marker.BytecodeMarker;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;

public class InstrumentationInvokeDynamic {

    @SyntheticLocal
    static boolean isInvokeDynamicUsed = false;

    @ThreadLocal
    static String testName;

    @Before(marker = BodyMarker.class, guard = GuardUnitTest.class)
    static void onMethodEntry(MethodStaticContext msc) {
        Profiler.startTimer(msc.thisMethodFullName());
        isInvokeDynamicUsed = false;
    }

    @After(marker = BodyMarker.class, guard = GuardUnitTest.class)
    static void onMethodExit(MethodStaticContext msc) {
        if (isInvokeDynamicUsed) {
            Profiler.endTimer(msc.thisMethodFullName());
        }
    }

    @AfterReturning (
            marker = BytecodeMarker.class,
            args = "invokedynamic"
    )
    public static void invokedynamicHappened (MethodStaticContext msc) {
        isInvokeDynamicUsed = true;
    }
}
