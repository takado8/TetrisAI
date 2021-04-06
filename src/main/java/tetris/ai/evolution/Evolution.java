package tetris.ai.evolution;

import tetris.ai.Agent;
import tetris.environment.Environment;
import tetris.environment.engine.Action;
import tetris.environment.engine.results.StepResult;

import java.util.*;

import static tetris.ai.Constants.CROSSING_OVER_BIAS;
import static tetris.ai.Constants.NUMBER_OF_GENES;
import static tetris.ai.evolution.RandomGenerator.randomGenerator;

public class Evolution {
    private final Environment engine;
    private final double targetPopulationSize;
    private final double initPopulationSize;
    private final double reproductionRate;
    private final double mutationRate;
    private final List<Agent> population = new ArrayList<>();

    public Evolution(Environment gameEnvironment, int initPopulationSize, int targetPopulationSize, double reproductionRate, double mutationRate) {
        this.engine = gameEnvironment;
        this.targetPopulationSize = targetPopulationSize;
        this.initPopulationSize = initPopulationSize;
        this.reproductionRate = reproductionRate;
        this.mutationRate = mutationRate;
        generateRandomPopulation();
    }

    public Agent evolve(int numberOfGenerations, int maxGames, int maxTurns) {
        double bestScore = -1;
        Agent bestAgent = new Agent();
        Agent bestGenerationAgent = new Agent();

        for (int g = 0; g < numberOfGenerations; g++) {
            double generationScore = 0;
            double generationBestScore = -1;
            for (Agent agent : population) {
                double agentScore = testAgent(agent, maxGames, maxTurns);
                generationScore += agentScore;
                agent.setFitness(agentScore);
                if (agentScore > generationBestScore) {
                    generationBestScore = agentScore;
                    bestGenerationAgent = agent;
                    if (agentScore >= bestScore) {
                        bestScore = agentScore;
                        bestAgent = agent;
                    }
                }
            }
            System.out.println("Generation " + g + " avg score: " + generationScore / (targetPopulationSize * maxGames)
                    + " generation best: " + generationBestScore + " total best score: " + bestScore);
            if (g % 10 == 0) {
                System.out.println("Best score ever chromosome: " + Arrays.toString(bestAgent.getChromosome()));
                System.out.println("Best score generation chromosome: " + Arrays.toString(bestGenerationAgent.getChromosome()));
            }
            nextGeneration();
        }
        System.out.println("Best score ever chromosome: " + Arrays.toString(bestAgent.getChromosome()));
        System.out.println("Best score generation chromosome: " + Arrays.toString(bestGenerationAgent.getChromosome()));
        return bestAgent;
    }

    private double testAgent(Agent agent, int maxGames, int maxTurns) {
        double agentScore = 0;
        for (int n = 0; n < maxGames; n++) {
            engine.reset();
            StepResult stepResult = null;
            boolean isFinalStep = false;
            int turnsPlayed = 0;
            while (!isFinalStep && turnsPlayed < maxTurns) {
                engine.runFullSimulation(agent);
                boolean tetriminoDropped = false;
                while (!tetriminoDropped) {
                    stepResult = engine.step(Action.END_TURN);
                    tetriminoDropped = stepResult.isTetriminoDropped();
                }
                isFinalStep = stepResult.isFinalStep();
                turnsPlayed++;
            }
            if (turnsPlayed < maxTurns) {
                agentScore = 0;
                break;
            }
            if (stepResult != null) {
                agentScore += stepResult.getGameScore();
            }
        }
        return agentScore;
    }

    public static double[] testAgent(Agent agent, Environment engine, int maxGames) {
        double[] scoreArray = new double[maxGames];
        for (int n = 0; n < maxGames; n++) {
            // set environment to initial state
            engine.reset();
            StepResult stepResult = null;
            // while game is not over
            boolean isFinalStep = false;
            double turns = 0;
            while (!isFinalStep) {
                turns++;
                // set new tetrimino in desired location
                engine.runFullSimulation(agent);
                // move tetrimino down until it drops
                boolean tetriminoDropped = false;
                while (!tetriminoDropped) {
                    // save results
                    stepResult = engine.step(Action.END_TURN);
                    tetriminoDropped = stepResult.isTetriminoDropped();
                }
                // tetrimino dropped, check if game is over
                isFinalStep = stepResult.isFinalStep();
                if (turns % 10000 == 0) {
                    System.out.println("Turns:" + turns + " Score: " + stepResult.getGameScore());
                }
            }
            // game is over, save agent results
            scoreArray[n] = stepResult.getGameScore();
            System.out.println("End of game " + (n + 1) + ". Turns taken: " + turns);
        }
        return scoreArray;
    }

    private void generateRandomPopulation() {
        while (population.size() < initPopulationSize) {
            population.add(new Agent());
        }
    }

    /**
     * Main training function. Goes through every step of evolution process.
     * Called every time after population evaluation by interacting with the environment.
     */
    public void nextGeneration() {
        int poolSize = (int) (targetPopulationSize * reproductionRate);
        List<Agent> reproductionPool = selectReproductionPool(poolSize);
        List<Agent> offspring = reproduce(reproductionPool, poolSize);
        // select dead pool
        int overpopulation = population.size() + offspring.size() - (int) targetPopulationSize;
        int deadPoolSize;
        if (overpopulation > 0) {
            System.out.println("overpopulation: " + overpopulation);
            deadPoolSize = (int) ((double) poolSize * 1.7);
            deadPoolSize = Math.min(deadPoolSize, overpopulation);
        } else {
            deadPoolSize = poolSize;
        }
        List<Agent> deadPool = selectDeadPool(deadPoolSize);
        // remove deadPool from population
        removeDeadPool(deadPool);
        // merge offspring with population
        population.addAll(offspring);
        System.out.println("population size: " + population.size());
    }

    private List<Agent> selectReproductionPool(int poolSize) {
        CompareOperator takeWinner = (a, b) -> a > b;
        return selectPoolByTournament(poolSize, takeWinner);
    }

    private List<Agent> selectDeadPool(int poolSize) {
        CompareOperator takeLooser = (a, b) -> a < b;
        return selectPoolByTournament(poolSize, takeLooser);
    }

    /**
     * Randomly draws from pool two agents, combines their chromosomes in the crossing-over process
     * and returns list of new agents with combined chromosomes;
     *
     * @param reproductionPool pool to reproduce from
     * @param offspringNumber  size of returned list.
     */
    private List<Agent> reproduce(List<Agent> reproductionPool, int offspringNumber) {
        List<Agent> offspring = new ArrayList<>();
        while (offspring.size() < offspringNumber) {
            Agent parent1 = randomGenerator.select(reproductionPool);
            Agent parent2 = randomGenerator.select(reproductionPool);
            while (parent1 == parent2 && reproductionPool.size() > 1) {
                parent2 = randomGenerator.select(reproductionPool);
            }
            Agent child = randomGenerator.percentChance(0.5) ? crossingOverChromosomes(parent1, parent2)
                    : crossingOverGenes(parent1, parent2);

            if (randomGenerator.percentChance(mutationRate)) {
                child.mutate();
            }
            offspring.add(child);
        }
        return offspring;
    }

    /**
     * Removes deadPool from the population.
     *
     * @param deadPool pool to remove.
     */
    private void removeDeadPool(List<Agent> deadPool) {
        for (var agent : deadPool) {
            population.remove(agent);
        }
    }

    /**
     * Selects from {@code population} pool of agents. Randomly, but biased towards
     * better fitted agents. The tournament selection randomly picks two agents and
     * adds agent with better fitness to the returned pool.
     *
     * @param poolSize        desired size of selected pool
     * @param compareOperator lambda expression to compare agents fitness.
     * @return selected pool
     */
    private List<Agent> selectPoolByTournament(int poolSize, CompareOperator compareOperator) {
        Set<Agent> selectedPool = new HashSet<>();
        while (selectedPool.size() < poolSize) {
            // select two random agents index
            int agent1Index = randomGenerator.nextInt(population.size());
            int agent2Index = randomGenerator.nextInt(population.size());
            // and not the same one by accident
            while (agent2Index == agent1Index) {
                agent2Index = randomGenerator.nextInt(population.size());
            }
            // get the agents
            Agent agent1 = population.get(agent1Index);
            Agent agent2 = population.get(agent2Index);
            // compare and add agent to the pool
            if (compareOperator.compare(agent1.getFitness(), agent2.getFitness())) {
                selectedPool.add(agent1);
            } else {
                selectedPool.add(agent2);
            }
        }
        return new ArrayList<>(selectedPool);
    }

    /**
     * Creates new Agent from two parent Agents. Combines their chromosomes, by
     * summing both parents corresponding chromosome values multiplied by the parents fitness and normalizing it to
     * range <0;1>. Small bias is introduced to avoid multiplication by zero.
     *
     * @param parent1 Agent
     * @param parent2 Agent
     * @return new Agent with chromosome derived from parents.
     */
    private Agent crossingOverGenes(Agent parent1, Agent parent2) {
        var parent1Chromosome = parent1.getChromosome();
        var parent2Chromosome = parent2.getChromosome();
        var newChromosome = new double[NUMBER_OF_GENES];

        for (int i = 0; i < newChromosome.length; i++) {
            newChromosome[i] = parent1Chromosome[i] * (parent1.getFitness() + CROSSING_OVER_BIAS) +
                    parent2Chromosome[i] * (parent2.getFitness() + CROSSING_OVER_BIAS);
        }
        Agent offspring = new Agent(newChromosome);
        offspring.normalizeChromosome();

        return offspring;
    }

    /**
     * Creates new Agent from two parent Agents. Combines their chromosomes, by
     * copying and merging random parts of their chromosome into child's chromosome
     */
    private Agent crossingOverChromosomes(Agent parent1, Agent parent2) {
        var parent1Chromosome = parent1.getChromosome();
        var parent2Chromosome = parent2.getChromosome();

        var newChromosome = new double[NUMBER_OF_GENES];

        int i = 0;
        while (i < newChromosome.length) {
            int range = (int) ((double) newChromosome.length * randomGenerator.nextDouble(0.15, 0.4));
            var currentDonor = randomGenerator.percentChance(0.5) ? parent1Chromosome : parent2Chromosome;
            while (range > 0 && i < newChromosome.length) {
                newChromosome[i] = currentDonor[i];
                i++;
                range--;
            }
        }
        Agent offspring = new Agent(newChromosome);
        offspring.normalizeChromosome();

        return offspring;
    }
}
