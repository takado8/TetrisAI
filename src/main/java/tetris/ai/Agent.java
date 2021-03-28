package tetris.ai;

import java.util.Arrays;

import static tetris.ai.Constants.*;
import static tetris.ai.RandomGenerator.randomGenerator;

/**
 * Agent is a unit that interacts with the environment and is subjected to evolution process.
 */
public class Agent {
    private final double[] chromosome;
    private double fitness = 0.0;

    /**
     * Creates Agent with random chromosome gene values in range <0;1>
     */
    public Agent() {
        chromosome = new double[NUMBER_OF_GENES];
        for (int i = 0; i < NUMBER_OF_GENES; i++) {
            chromosome[i] = randomGenerator.nextDouble(-0.99,1);
        }
    }

    /**
     * Generates newborn Agent
     *
     * @param chromosome new chromosome
     */
    public Agent(double[] chromosome) {
        this.chromosome = chromosome;
    }

    /**
     *  Mutates random gene value by random value, fixed range for now.
     */
    public void mutate() {
        int mutationIndex = randomGenerator.nextInt(chromosome.length);
        chromosome[mutationIndex] += randomGenerator.nextDouble(-0.01, 0.01);
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

    public double[] getChromosome() {
        return Arrays.copyOf(chromosome, chromosome.length);
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double evaluateMove(double[] inputs) {
        double moveEvaluation = 0.0;
        for (int i = 0; i < inputs.length; i++) {
            moveEvaluation += inputs[i] * chromosome[i];
        }
//        System.out.println("move agent eval: " +moveEvaluation);
//        System.out.println(Arrays.toString(chromosome));
        return moveEvaluation;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

//    @Override
//    public int hashCode() {
//
//    }
}

