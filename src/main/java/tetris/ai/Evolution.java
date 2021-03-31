package tetris.ai;

import tetris.environment.Environment;
import tetris.environment.engine.Action;
import tetris.environment.engine.StepResult;

import java.util.*;

import static tetris.ai.Constants.CROSSING_OVER_BIAS;
import static tetris.ai.Constants.NUMBER_OF_GENES;
import static tetris.ai.RandomGenerator.randomGenerator;

public class Evolution {
    private final Environment engine;
    private final double populationSize;
    private final double reproductionRate;
    private final double mutationRate;
    private final List<Agent> population = new ArrayList<>();

    public Evolution(Environment gameEnvironment, int populationSize, double reproductionRate, double mutationRate) {
        this.engine = gameEnvironment;
        this.populationSize = populationSize;
        this.reproductionRate = reproductionRate;
        this.mutationRate = mutationRate;
        generateRandomPopulation();
    }

    public Agent evolve(int numberOfGenerations, int maxGames, int maxTurns) {
        double bestScore = -1;
        Agent bestAgent = new Agent();
        Agent bestGenerationAgent = new Agent();

        // for number of generations
        for (int g = 0; g < numberOfGenerations; g++) {
            // track generation performance
            double generationScores = 0;
            double generationBestScore = -1;
            // each agent in population plays n games
            for (Agent agent : population) {
                double agentScore = testAgent(agent, maxGames, maxTurns);
                generationScores += agentScore;
                agentScore /= maxGames;
                agent.setFitness(agentScore);
                // count high score etc
                if (agentScore > generationBestScore) {
                    generationBestScore = agentScore;
                    bestGenerationAgent = agent;
                    if (agentScore >= bestScore) {
                        bestScore = agentScore;
                        bestAgent = agent;
                    }
                }
                // next agent
            }
            // next generation
            // print some info about last generation performance
            System.out.println("Generation " + g + " avg score: " + generationScores / (populationSize * maxGames)
                    + " generation best: " + generationBestScore + " total best score: " + bestScore);
            if (g % 10 == 0) {
                System.out.println("Best score ever chromosome: " + Arrays.toString(bestAgent.getChromosome()));
                System.out.println("Best score generation chromosome: " + Arrays.toString(bestGenerationAgent.getChromosome()));
            }// evolve
            nextGeneration();
        }
        System.out.println("Best score ever chromosome: " + Arrays.toString(bestAgent.getChromosome()));
        System.out.println("Best score generation chromosome: " + Arrays.toString(bestGenerationAgent.getChromosome()));
        return bestAgent;
    }

    private double testAgent(Agent agent, int maxGames, int maxTurns) {
        double agentScore = 0;
        for (int n = 0; n < maxGames; n++) {
            // set environment to initial state
            engine.reset();
            StepResult stepResult = null;
            // while game is not over
            boolean isFinalStep = false;
            int turnsPlayed = 0;
            while (!isFinalStep && turnsPlayed < maxTurns) {
                // set new tetrimino in desired location
                engine.simulate(agent);
                // move tetrimino down until it drops
                boolean tetriminoDropped = false;
                while (!tetriminoDropped) {
                    // save results
                    stepResult = engine.step(Action.END_TURN);
                    tetriminoDropped = stepResult.isTetriminoDropped();
                }
                // tetrimino dropped, check if game is over
                isFinalStep = stepResult.isFinalStep();
                turnsPlayed++;
            }
            // game is over, save agent results
            if (stepResult != null) {
                agentScore += stepResult.getGameScore();
            }
        }
        return agentScore;
    }

    public double[] testAgent(Agent agent, int maxGames) {
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
                if (turns % 1000 == 0) {
                    System.out.println("Turns: " + turns);
                }
                // set new tetrimino in desired location
                engine.simulate(agent);
                // move tetrimino down until it drops
                boolean tetriminoDropped = false;
                while (!tetriminoDropped) {
                    // save results
                    stepResult = engine.step(Action.END_TURN);
                    tetriminoDropped = stepResult.isTetriminoDropped();
                }
                // tetrimino dropped, check if game is over
                isFinalStep = stepResult.isFinalStep();
            }
            // game is over, save agent results
            scoreArray[n] = stepResult.getGameScore();
            System.out.println("End of game " + (n + 1) + ". Turns taken: " + turns);
        }
        return scoreArray;
    }

    private void generateRandomPopulation() {
        while (population.size() < populationSize) {
            population.add(new Agent());
        }
    }

    /**
     * Main training function. Goes through every step of evolution process.
     * Called every time after population evaluation by interacting with the environment.
     */
    public void nextGeneration() {
        int poolSize = (int) (populationSize * reproductionRate);
        // select agents to reproduce
        CompareOperator takeWinner = (a, b) -> a > b;
        List<Agent> reproductionPool = selectPoolByTournament(poolSize, takeWinner);
        // reproduce
        List<Agent> offspring = reproduce(reproductionPool, poolSize);
        // select dead pool
        CompareOperator takeLooser = (a, b) -> a < b;
        List<Agent> deadPool = selectPoolByTournament(poolSize, takeLooser);
        // remove deadPool from population
        removeDeadPool(deadPool);
        // merge offspring with population
        population.addAll(offspring);
        // end of cycle
        assert population.size() == populationSize;
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
            while (parent1 == parent2) {
                parent2 = randomGenerator.select(reproductionPool);
            }
            Agent child = crossingOver(parent1, parent2);
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
        int populationSize = population.size();
        int deadPoolSize = deadPool.size();
        for (var agent : deadPool) {
            population.remove(agent);
        }
        assert populationSize - deadPoolSize == population.size() : "population0 len:" + populationSize + " dead len: " + deadPoolSize +
                " population after" + population.size();
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
            // select two random agents
            var agent1Index = randomGenerator.nextInt(population.size());
            var agent2Index = randomGenerator.nextInt(population.size());
            // and not the same one by accident
            while (agent2Index == agent1Index) {
                agent2Index = randomGenerator.nextInt(population.size());
            }
            // get the agents
            Agent agent1 = population.get(agent1Index);
            Agent agent2 = population.get(agent2Index);

            // compare and agent to the pool
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
     * range <0;1>. Newly derived Agent is subjected to the mutation process.
     *
     * @param parent1 Agent
     * @param parent2 Agent
     * @return new Agent with chromosome derived from parents.
     */
    private Agent crossingOver(Agent parent1, Agent parent2) {
        var parent1Chromosome = parent1.getChromosome();
        var parent2Chromosome = parent2.getChromosome();

        var newChromosome = new double[NUMBER_OF_GENES];

        for (int i = 0; i < newChromosome.length; i++) {
            // add little bias to avoid multiplying by zero.
            newChromosome[i] = parent1Chromosome[i] * (parent1.getFitness() + CROSSING_OVER_BIAS) +
                    parent2Chromosome[i] * (parent2.getFitness() + CROSSING_OVER_BIAS);
        }
        Agent offspring = new Agent(newChromosome);
        offspring.normalizeChromosome();

        if (mutationRate > randomGenerator.nextDouble()) {
            offspring.mutate();
        }
        return offspring;
    }

    public double getPopulationSize() {
        return populationSize;
    }

    public double getReproductionRate() {
        return reproductionRate;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public List<Agent> getPopulation() {
        return population;
    }
}
