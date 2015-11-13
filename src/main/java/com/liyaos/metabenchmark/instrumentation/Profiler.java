package com.liyaos.metabenchmark.instrumentation;

/**
 * Created by lastland on 15/9/10.
 */
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import com.liyaos.metabenchmark.MainArguments;
import com.liyaos.metabenchmark.UseCases;
import com.liyaos.metabenchmark.profiler.ArchiveDumper;
import com.liyaos.metabenchmark.profiler.Dumper;
import com.liyaos.metabenchmark.profiler.TTYDumper;

import static com.liyaos.metabenchmark.MainArguments.*;
import static com.liyaos.metabenchmark.UseCases.*;

public class Profiler {

	public static class ObjectAndMethod{
    	Object object;
    	String method;
    	
    	public ObjectAndMethod(Object object, String method) {
    		this.object = object;
    		this.method = method;
    	}
    	
    	public int hashCode() {
    	    return this.object.hashCode() ^ this.method.hashCode();
    	}

    	public boolean equals(Object obj) {
    	      if (this == obj)
    	            return true;
    	      if (obj == null)
    	            return false;
    	      if (getClass() != obj.getClass())
    	           return false;
    	      ObjectAndMethod other = (ObjectAndMethod) obj;
    	      if (this.object != other.object)
    	            return false;
    	      if (!this.method.equals(other.method))
    	            return false;
    	        return true;
    	    }
    	}
    	
    	
    	
    public static class Pair<T1, T2> {
        public T1 key;
        public T2 value;

        public Pair(T1 key, T2 value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return key.toString() + " " + value;
        }
    }

    public static class QueueState {
        public int postSize;
        public long time;

        public QueueState(int postSize) {
            this.time = System.nanoTime() - START;
            this.postSize = postSize;
        }

        @Override
        public String toString() {
            return "QueueState (postSize = " + postSize + ", time = " + time + ")";
        }
    }

    public static class RunningState {
        public Thread t;
        public long beginTime;
        public long endTime;

        public RunningState(Thread t) {
            this.beginTime = getNS();
            this.t = t;
        }

        public void end() {
            endTime = getNS();
        }
    }

    public static class QueueLog extends ConcurrentLinkedQueue<QueueState> {
        public QueueLog() {
            super();
        }

        public LinkedList<Long> arrivals() {
            long first = 0;
            LinkedList<Long> a = new LinkedList<>();
            while (!isEmpty()) {
                QueueState now = poll();
                if (now.postSize > 1) {
                    a.add(now.time - first);
                }
                first = now.time;
            }
            return a;
        }

        public LinkedList<Long> services() {
            long first = 0;
            LinkedList<Long> s = new LinkedList<>();
            while (!isEmpty()) {
                QueueState now = poll();
                if (first != 0)
                    s.add(now.time - first);
                first = now.time;
                if (now.postSize < 1) {
                    first = 0;
                }
            }
            return s;
        }
    }

    public static final long START = System.nanoTime();
    public static final long MS = 1000000;
    public static final long STRIDE = 100 * MS;

    public static AtomicLong idGenerator = new AtomicLong();

    public static ConcurrentHashMap<Runnable, ConcurrentLinkedDeque<RunningState>> runnings;

    public static ConcurrentHashMap<Object, Integer> count;

    public static ConcurrentHashMap<Object, Integer> objCount;

    public static ConcurrentHashMap<String, Long> startTimes;

    public static ConcurrentHashMap<String, Long> executionTimes;

    public static ConcurrentHashMap<Long, Integer> arraySizes;
    
    
    
        
    public static ConcurrentHashMap<ObjectAndMethod, Integer> methodInvocations;

    public static ConcurrentHashMap<String, Boolean> invokeDynamicUsed;
    
    

    static {
        runnings = new ConcurrentHashMap<>();

        count = new ConcurrentHashMap<>();

        objCount = new ConcurrentHashMap<>();

        startTimes = new ConcurrentHashMap<>();

        executionTimes = new ConcurrentHashMap<>();

        arraySizes = new ConcurrentHashMap<>();
        
        methodInvocations = new ConcurrentHashMap<>();

        invokeDynamicUsed = new ConcurrentHashMap<>();

        switch(MainArguments.selectedUseCase()) {

            case UseCases.Matrix:
                Runtime.getRuntime().addShutdownHook(new Thread(ProfilerMatrix::dump));
                break;

            default:
                Runtime.getRuntime().addShutdownHook(new Thread(Profiler::dump));
                break;
        }
    }

    private static int maximal(ConcurrentHashMap<Integer, Integer> distribute) {
        return distribute.values().stream().mapToInt(Integer::intValue).max().getAsInt();
    }

    public static Pair<Object, Integer> max(
            ConcurrentHashMap<Object, ConcurrentHashMap<Integer, Integer>> map) {
        return map.entrySet().stream()
                .map(t -> new Pair<Object, Integer>(t.getKey(), maximal(t.getValue())))
                .max((p1, p2) -> p1.value - p2.value).get();
    }

    public static void runnings(Dumper dumper, Object r, ConcurrentLinkedDeque<RunningState> q) {
        long n_threads = q.stream().map(s -> s.t).count();
        long n_d_threads = q.stream().map(s -> s.t).distinct().count();
        long max_time = q.stream().mapToLong(s -> s.endTime - s.beginTime).max().getAsLong();
        double avg_time = q.stream().mapToLong(s -> s.endTime - s.beginTime).filter(l -> l > 0).average().getAsDouble();
        dumper.println(r + " on " + n_threads + " threads, " + n_d_threads + " distinct threads, max time: " + max_time + ", average running time: " + avg_time);
    }

    public static void runnings(Dumper dumper) {
        runnings.entrySet().forEach(r -> runnings(dumper, r.getKey(), r.getValue()));
    }
    
    public static void dumpMethodInvocations(Dumper dumper) {
    	for (ObjectAndMethod key: methodInvocations.keySet())
    	{	
    		dumper.println("MethodsInvoked,Object,"+ key.object.toString() + ",Class,"+ key.object.getClass() + ",Method," + key.method + ",NInvok," + methodInvocations.get(key));
    	}
    }

    public static void dump() {
            try (Dumper dumper = new ArchiveDumper("results" + java.lang.management.ManagementFactory.getRuntimeMXBean().getName())) {
                runnings(dumper);
                dumpMethodInvocations(dumper);
                
//                for (Map.Entry<Long, Integer> tuple : arraySizes.entrySet()) {
//                    if (tuple.getValue() > MainArguments.matrixSizeTreshold()){
//                        dumper.println("====> Found Matrix of size " + tuple.getValue());
//                        executionTimes.entrySet().forEach(r ->
//                                dumper.println("Execution time for " + r.getKey() + ": " + r.getValue()));
//                        break;
//                    }
//                    else{
//                        dumper.println("====> Discarded Matrix of size " + tuple.getValue());
//                    }
//                }
//            }


                executionTimes.entrySet().forEach(r ->
                        dumper.println("Execution time for " + r.getKey() + ": " + r.getValue()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void log(Object ob) {
        objCount.computeIfPresent(ob, (k, v) -> v + 1);
        objCount.computeIfAbsent(ob, k -> 1);
    }

    private static int getStride() {
        return (int) ((System.nanoTime() - START) / STRIDE);
    }

    private static int getMS() {
        return (int) ((System.nanoTime() - START) / MS);
    }

    private static long getNS() {
        return System.nanoTime() - START;
    }

    public static void poolBegin(Thread t, Runnable r) {
        runnings.computeIfAbsent(r, k -> new ConcurrentLinkedDeque<>()).addLast(new RunningState(t));
    }

    public static void poolEnd(Runnable r) {
        runnings.get(r).peekLast().end();
    }

    public static void startTimer(String m) {
        startTimes.put(m, Long.valueOf(System.nanoTime()));
    }

    public static void endTimer(String m) {
        long startTime = startTimes.get(m);
        executionTimes.put(m, Long.valueOf(System.nanoTime() - startTime));
    }

    public static void addArraySize(int size) {
        arraySizes.put(idGenerator.getAndIncrement(), size);
    }

    public static void invokeDynamicUsed(int size) {
        arraySizes.put(idGenerator.getAndIncrement(), size);
    }

    public static void test(Object ob) {
        count.computeIfPresent(ob, (k, v) -> v + 1);
        count.computeIfAbsent(ob, k -> 1);
    }

	public static void setMethodInvocation(Object ob, String method) {
		if (ob != null) { 
			ObjectAndMethod om = new ObjectAndMethod(ob,method);
			Integer invocations = methodInvocations.get(om);
			if (invocations==null)
				invocations = new Integer(1);
			else invocations = new Integer(invocations.intValue()+1);
			methodInvocations.put(om,invocations);
		}	
		
	}
}
