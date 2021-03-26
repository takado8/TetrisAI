package tetris.ai;

import java.util.Arrays;

import static tetris.ai.Constants.*;
import static tetris.ai.RandomGenerator.randomGenerator;

/**
 * Agent is a unit that interacts with the environment and is subjected to evolution process.
 */
public class Agent {
    private final float[] chromosome;
    private float fitness = 0;

    /**
     * Generates Agent with random chromosome gene values in range <0;1>
     */
    public Agent() {
        chromosome = new float[NUMBER_OF_GENES];
        for (int i = 0; i < NUMBER_OF_GENES; i++) {
            chromosome[i] = randomGenerator.nextFloat();
        }
    }

    /**
     * Generates newborn Agent
     *
     * @param chromosome new chromosome
     */
    public Agent(float[] chromosome) {
        this.chromosome = chromosome;
    }

    /**
     *  Mutates random gene value by random value, fixed range for now.
     */
    public void mutate() {
        int mutationIndex = randomGenerator.nextInt(chromosome.length);
        chromosome[mutationIndex] += randomGenerator.nextFloat(-0.01f, 0.01f);
        normalizeChromosome();
    }

    public void normalizeChromosome() {
        double module = 0;
        for (var value : chromosome) {
            module += Math.pow(value, 2);
        }
        module = Math.sqrt(module);
        for (int i = 0; i < chromosome.length; i++) {
            chromosome[i] /= module;
        }
    }

    public float[] getChromosome() {
        return Arrays.copyOf(chromosome, chromosome.length);
    }

    public float getFitness() {
        return fitness;
    }

    public void setFitness(float fitness) {
        this.fitness = fitness;
    }

    public float evaluateMove(float[] inputs) {
        float moveEvaluation = 0;
        for (int i = 0; i < inputs.length; i++) {
            moveEvaluation += inputs[i] * chromosome[i];
        }
        return moveEvaluation;
    }
}
