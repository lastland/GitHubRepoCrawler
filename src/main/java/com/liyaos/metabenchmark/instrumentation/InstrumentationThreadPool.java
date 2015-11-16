package com.liyaos.metabenchmark.instrumentation;

/**
 * Created by lastland on 15/9/10.
 */

import ch.usi.dag.disl.annotation.*;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;
import com.liyaos.metabenchmark.instrumentation.Guard.ThreadPoolOnly;

public class InstrumentationThreadPool {

	 @Before(marker = BodyMarker.class, guard = ThreadPoolOnly.class)    
	     static void updateMethodInvocation(final DynamicContext dc, final MethodStaticContext msc) {
	     
	     Profiler.setMethodInvocation(dc.getThis(),msc.thisMethodFullName());
	     	
	     }
}
