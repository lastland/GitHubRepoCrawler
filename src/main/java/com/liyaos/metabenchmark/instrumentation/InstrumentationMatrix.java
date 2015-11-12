package com.liyaos.metabenchmark.instrumentation;

/**
 * Created by lastland on 15/9/10.
 */

import ch.usi.dag.disl.annotation.*;
import ch.usi.dag.disl.annotation.ThreadLocal;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.marker.BytecodeMarker;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;

public class InstrumentationMatrix {

    @Before(marker = BodyMarker.class, guard = GuardUnitTest.class)
    static void onMethodEntry(MethodStaticContext msc) {
        Profiler.startTimer(msc.thisMethodFullName());
    }

    @After(marker = BodyMarker.class, guard = GuardUnitTest.class)
    static void onMethodExit(MethodStaticContext msc) {
        Profiler.endTimer(msc.thisMethodFullName());
    }


    @SyntheticLocal
    static int size0; //int[1024][2056], size0 = 2056

    @SyntheticLocal
    static int size1; //int[1024][2056], size1 = 1024

    @Before (
            marker = BytecodeMarker.class,
            args = "multianewarray",
            guard = Guard.TwoPrimitiveArrayAllocations.class
    )
    public static void beforeTwoPrimitiveArrayAllocation (
            final DynamicContext dc, final MultiArrayStaticContext masc
    ) {
        size0 = dc.getStackValue (0, int.class);
        size1 = dc.getStackValue (1, int.class);
        Profiler.addArraySize(size0 * size1);
    }


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
        size0 = dc.getStackValue (0, int.class);
        size1 = dc.getStackValue (1, int.class);

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
}
