package com.liyaos.metabenchmark.instrumentation;

import ch.usi.dag.disl.annotation.GuardMethod;

public abstract class GuardUnitTest {

    @GuardMethod
    public static boolean isApplicable(MethodAnnotationContext mac) {
        return mac.isTest();
    }

}
