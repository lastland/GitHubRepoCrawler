/**
 * Created by lastland on 15/9/30.
 */
import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.Test;
import static org.junit.Assert.*;

public class MyTest {

    @Test
    public void testPasses() throws InterruptedException {
        ExecutorService s = Executors.newFixedThreadPool(2);
        Runnable r = new Factorial();
        s.execute(r);
        s.execute(r);
        s.execute(r);
        s.shutdown();
        while (!s.isShutdown()) {
            Thread.sleep(500);
        }
        assertTrue(s instanceof ThreadPoolExecutor);
    }
}