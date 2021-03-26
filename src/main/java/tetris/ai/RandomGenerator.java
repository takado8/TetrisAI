package tetris.ai;

import java.util.List;
import java.util.Random;

public class RandomGenerator extends Random {
    public static final RandomGenerator randomGenerator = new RandomGenerator();

    private RandomGenerator() {}

    public float nextFloat(float min, float max) {
        return min + nextFloat() * (max - min);
    }

    /**
     * Selects and returns random item from a list
     */
    public <T> T select(List<T> pool) {
        return pool.get(nextInt(pool.size()));
    }
}
