package com.liyaos.metabenchmark.instrumentation;

/**
 * Created by lastland on 15/9/10.
 */
import ch.usi.dag.disl.annotation.After;
import ch.usi.dag.disl.annotation.AfterReturning;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.annotation.SyntheticLocal;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;

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

    @SyntheticLocal
    static long start;

    @Before(marker = BodyMarker.class, guard = GuardUnitTest.class)
    static void onMethodEntry(MethodStaticContext msc) {
        Profiler.startTimer(msc.thisMethodFullName());
    }

    @After(marker = BodyMarker.class, guard = GuardUnitTest.class)
    static void onMethodExit(MethodStaticContext msc) {
		Profiler.endTimer(msc.thisMethodFullName());
        long executionTime = System.nanoTime() - start;

        try {
            try (Dumper dumper = new ArchiveDumper("tests" + java.lang.management.ManagementFactory.getRuntimeMXBean().getName(), false)) {
                dumper.println(msc.thisMethodFullName() + " executiont time of the test: " + executionTime);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//	@AfterReturning(marker = BytecodeMarker.class, args = "new", order = 0)
//	static void profileActualAllocation(EnhancedBytecodeStaticContext context) {
//		GraalDirectives.instrumentationBegin(-1);
//		Profiler.profileAllocation(context.bci(), GraalDirectives.runtimePath());
//		GraalDirectives.instrumentationEnd();
//	}
//
//	@AfterReturning(marker = BytecodeMarker.class, args = "new", order = 1)
//	static void profileAllocation(EnhancedBytecodeStaticContext context) {
//		GraalDirectives.instrumentationBegin(0);
//		Profiler.profileAllocation(context.bci(), 2);
//		GraalDirectives.instrumentationEnd();
//	}

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
