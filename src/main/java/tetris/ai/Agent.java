package tetris.ai;

import tetris.ai.neural.SimpleNeuralNet;

import java.util.Arrays;

import static tetris.ai.Constants.*;
import static tetris.ai.evolution.RandomGenerator.randomGenerator;

/**
 * Unit that interacts with the environment and is subjected to evolution process.
 */
public class Agent {
    private final double[] chromosome;
    private double fitness = 0.0;
    private final SimpleNeuralNet neuralNet;

    /**
     * Creates Agent with random chromosome gene values in range <0;1>
     */
    public Agent() {
        chromosome = new double[NUMBER_OF_GENES];
        for (int i = 0; i < NUMBER_OF_GENES; i++) {
            chromosome[i] = randomGenerator.nextDouble(-0.99, 1);
        }
        neuralNet = new SimpleNeuralNet(NEURAL_NETWORK_SHAPE, chromosome);
    }

    /**
     * Generates newborn Agent
     *
     * @param chromosome new chromosome
     */
    public Agent(double[] chromosome) {
        this.chromosome = chromosome;
        neuralNet = new SimpleNeuralNet(NEURAL_NETWORK_SHAPE, chromosome);
    }

    /**
     * Multiplies each input by the corresponding chromosome value and sums the results.
     *
     * @param inputs array of values of the game field features
     * @return move evaluation
     */
    public double evaluateMove(double[] inputs) {
        double[] evaluation = neuralNet.feedNet(inputs);
        return evaluation[0];
//        double moveEvaluation = 0.0;
//        for (int i = 0; i < inputs.length; i++) {
//            moveEvaluation += inputs[i] * chromosome[i];
//        }
//        return moveEvaluation;
    }

    /**
     * Mutates random gene value by random value, in range of +/- MUTATION_VALUE
     */
    public void mutate() {
        int mutationIndex = randomGenerator.nextInt(chromosome.length);
        chromosome[mutationIndex] += randomGenerator.nextDouble(-MUTATION_VALUE, MUTATION_VALUE);
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

}

