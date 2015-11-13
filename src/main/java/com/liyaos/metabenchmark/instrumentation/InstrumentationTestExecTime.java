package com.liyaos.metabenchmark.instrumentation;

/**
 * Created by lastland on 15/9/10.
 */
import ch.usi.dag.disl.annotation.After;
import ch.usi.dag.disl.annotation.AfterReturning;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.annotation.SyntheticLocal;
import ch.usi.dag.disl.annotation.ThreadLocal;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.marker.BytecodeMarker;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import com.liyaos.metabenchmark.profiler.ArchiveDumper;
import com.liyaos.metabenchmark.profiler.Dumper;

import java.io.FileNotFoundException;

public class InstrumentationTestExecTime {

    @Before(marker = BodyMarker.class, guard = GuardUnitTest.class)
    static void onMethodEntry(MethodStaticContext msc) {
        Profiler.startTimer(msc.thisMethodFullName());
    }

    @After(marker = BodyMarker.class, guard = GuardUnitTest.class)
    static void onMethodExit(MethodStaticContext msc) {
            Profiler.endTimer(msc.thisMethodFullName());
    }
}
