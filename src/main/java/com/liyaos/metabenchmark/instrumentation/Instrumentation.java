package com.liyaos.metabenchmark.instrumentation;

/**
 * Created by lastland on 15/9/10.
 */
import ch.usi.dag.disl.annotation.After;
import ch.usi.dag.disl.annotation.AfterReturning;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.annotation.SyntheticLocal;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.marker.BytecodeMarker;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import com.liyaos.metabenchmark.profiler.ArchiveDumper;
import com.liyaos.metabenchmark.profiler.Dumper;

import java.io.FileNotFoundException;

public class Instrumentation {

//    @Before(marker = BodyMarker.class, scope = "java.util.concurrent.ThreadPoolExecutor.beforeExecute")
//    public static void test(DynamicContext dc) {
//        Profiler.poolBegin(dc.getMethodArgumentValue(0, Thread.class),
//                dc.getMethodArgumentValue(1, Runnable.class));
//    }
//
//    @After(marker = BodyMarker.class, scope = "java.util.concurrent.ThreadPoolExecutor.afterExecute")
//    public static void end(DynamicContext dc) {
//        Profiler.poolEnd(dc.getMethodArgumentValue(0, Runnable.class));
//    }

    @SyntheticLocal
    static long start;

    @Before(marker = BodyMarker.class, guard = GuardUnitTest.class)
    static void onMethodEntry(MethodStaticContext msc) {
        Profiler.startTimer(msc.thisMethodFullName());
    }

    @After(marker = BodyMarker.class, guard = GuardUnitTest.class)
    static void onMethodExit(MethodStaticContext msc) {
	    Profiler.endTimer(msc.thisMethodFullName());
    }

    @Before (marker = BytecodeMarker.class, args = "newarray, anewarray")
    public static void beforeArray (final DynamicContext di) {
        int size = di.getStackValue (0, Integer.class);
        Profiler.addArraySize(size);
    }

    @SyntheticLocal
    static int size0; //int[1024][2056], size0 = 2056

    @SyntheticLocal
    static int size1; //int[1024][2056], size1 = 1024



    /**
     * Emits an <i>array allocation</i> event for all intermediate arrays of a
     * multi-dimensional (at least 2) array of primitive-type elements. This
     * does not include the leaf arrays, which contain the primitive values.
     * <p>
     * Relies on the {@link MethodInvocations#threadId thread id} to have been
     * cached on method entry.
     * <p>
     * <b>Note:</b> This instrumentation <b>MUST</b> use the @AfterReturning
     * annotation to avoid inserting exception handler for a bytecode that
     * cannot produce exception.
     */
    @AfterReturning (
            marker = BytecodeMarker.class,
            args = "multianewarray",
            guard = Guard.TwoPrimitiveArrayAllocations.class
    )
    public static void afterTwoPrimitiveArrayAllocation (
            final DynamicContext dc, final MultiArrayStaticContext masc
    ) {
        final Object ref = dc.getStackValue (StackIndex.STACK_TOP, Object.class);
    }

    @Before (
            marker = BytecodeMarker.class,
            args = "multianewarray",
            guard = Guard.TwoReferenceArrayAllocations.class
    )
    public static void beforeTwoReferenceArrayAllocation (
            final DynamicContext dc, final MultiArrayStaticContext masc
    ) {
        int size0 = dc.getStackValue (0, int.class);
        int size1 = dc.getStackValue (1, int.class);

        Profiler.addArraySize(size0 * size1);
    }
    /**
     * Emits an <i>array allocation</i> event for all intermediate arrays of a
     * multi-dimensional (at least 2) array of reference-type elements. This
     * also includes the leaf arrays, which contain the actual references and
     * where we want to track reference updates.
     * <p>
     * Relies on the {@link MethodInvocations#threadId thread id} to have been
     * cached on method entry.
     * <p>
     * <b>Note:</b> This instrumentation <b>MUST</b> use the @AfterReturning
     * annotation to avoid inserting exception handler for a bytecode that
     * cannot produce exception.
     */
    @AfterReturning (
            marker = BytecodeMarker.class,
            args = "multianewarray",
            guard = Guard.TwoReferenceArrayAllocations.class
    )
    public static void afterTwoReferenceArrayAllocation (
            final DynamicContext dc, final MultiArrayStaticContext masc
    ) {

        final Object ref = dc.getStackValue (StackIndex.STACK_TOP, Object.class);
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
