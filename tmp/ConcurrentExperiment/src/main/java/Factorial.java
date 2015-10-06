import java.math.BigInteger;

/**
 * Created by lastland on 15/9/30.
 */
public class Factorial implements Runnable {

    @Override
    public void run() {
        BigInteger res = BigInteger.valueOf(1);
        for (long i = 1; i < 1000000000; i++) {
            res.multiply(BigInteger.valueOf(i));
        }
    }

    @Override
    public String toString() {
        return "Factorial" + hashCode();
    }
}
