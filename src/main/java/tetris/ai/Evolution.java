package tetris.ai;

import java.util.ArrayList;
import java.util.List;
import static tetris.ai.RandomGenerator.randomGenerator;

public class Evolution {
    private final int populationSize;
    private final int nbOfGenerations;
    private final double reproductionRate;
    private final double mutationRate;
    private final List<Agent> population = new ArrayList<>();


    public Evolution(int populationSize, int nbOfGenerations, double reproductionRate, double mutationRate) {
        this.populationSize = populationSize;
        this.nbOfGenerations = nbOfGenerations;
        this.reproductionRate = reproductionRate;
        this.mutationRate = mutationRate;
    }

    public void nextGeneration() {
        // select agents to reproduce
        int poolSize = (int)(populationSize * reproductionRate);
        List<Agent> reproductionPool = selectPoolByTournament(poolSize);
        // reproduce
        List<Agent> offspring = reproduce(reproductionPool, poolSize);
        // select dead pool
        List<Agent> deadPool = selectPoolByTournament(poolSize);
        // remove deadPool from population
        removeDeadPool(deadPool);
        // merge offspring with population
        population.addAll(offspring);
        // end of cycle
        System.out.println("population size: " + population.size());
    }

    /**
     * For every agent in pool combines two agents chromosomes and returns
     * list of new agent with combined chromosomes.
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

    private void removeDeadPool(List<Agent> deadPool) {
        for ( var agent : deadPool) {
            population.remove(agent);
        }
    }

    /**
     * Selects from {@code population} pool of agents. Randomly, but biased towards
     * better fitted agents. The tournament selection randomly picks two agents and
     * adds agent with better fitness to the returned pool.
     * One agent can be selected multiple times. It is fast and simple.
     * @param poolSize desired size of selected pool
     * @return selected pool
     */
    private ArrayList<Agent> selectPoolByTournament(int poolSize) {
        var selectedPool = new ArrayList<Agent>();
        while (selectedPool.size() < poolSize){
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
            // select better one
            if (agent1.getFitness() > agent2.getFitness()) {
                selectedPool.add(agent1);
            } else {
                selectedPool.add(agent2);
            }
        }
        return selectedPool;
    }

    private Agent crossingOver(Agent parent1, Agent parent2)
    {
        var parent1Chromosome = parent1.getChromosome();
        var parent2Chromosome = parent2.getChromosome();

        var newChromosome = new float[parent1Chromosome.length];

        for (int i = 0; i < parent1Chromosome.length; i++)
        {
            newChromosome[i] = parent1Chromosome[i] * parent1.getFitness() +
                    parent2Chromosome[i] * parent2.getFitness();
        }
        Agent offspring = new Agent(newChromosome);
        offspring.normalizeChromosome();
        if (mutationRate > randomGenerator.nextFloat()){
            offspring.mutate();
        }
        return offspring;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getNbOfGenerations() {
        return nbOfGenerations;
    }

    public double getReproductionRate() {
        return reproductionRate;
    }

    public double getMutationRate() {
        return mutationRate;
    }

}
