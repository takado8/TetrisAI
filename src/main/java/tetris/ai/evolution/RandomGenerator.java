package tetris.ai.evolution;

import java.util.List;
import java.util.Random;

public class RandomGenerator extends Random {
    public static final RandomGenerator randomGenerator = new RandomGenerator();

    private RandomGenerator() {
    }

    public boolean percentChance(double chance){
        return nextDouble() < chance;
    }

    /**
     * Returns random double in range: min inclusive; max exclusive
     */
    public double nextDouble(double min, double max) {
        return min + nextDouble() * (max - min);
    }

    /**
     * Selects and returns random item from a list
     */
    public <T> T select(List<T> pool) {
        return pool.get(nextInt(pool.size()));
    }
}
