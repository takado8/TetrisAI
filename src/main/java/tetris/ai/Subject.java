package tetris.ai;

import java.util.Arrays;

public class Subject {
    private final float[] chromosome;
    private float adjustment = 0;

    public Subject(float[] chromosome) {
        this.chromosome = chromosome;
    }

    public float[] getChromosome() {
        return Arrays.copyOf(chromosome, chromosome.length);
    }

    public float getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(float adjustment) {
        this.adjustment = adjustment;
    }
}
