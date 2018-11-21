import java.util.Random;

public class MyRandom {

    public static double random(){
        long longRandom = System.nanoTime();
        longRandom ^= (longRandom << 21);
        longRandom ^= (longRandom >>> 35);
        longRandom ^= (longRandom << 4);
        Random random = new Random();
        random.setSeed(longRandom);
        return random.nextDouble();
    }
}
