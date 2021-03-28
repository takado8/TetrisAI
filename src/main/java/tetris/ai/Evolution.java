package tetris.ai;

import java.util.*;

import static tetris.ai.RandomGenerator.randomGenerator;

public class Evolution {
    private final double populationSize;
    private final double nbOfGenerations;
    private final double reproductionRate;
    private final double mutationRate;
    private final List<Agent> population = new ArrayList<>();
    private int generation = 0;


    public Evolution(int populationSize, int nbOfGenerations, double reproductionRate, double mutationRate) {
        this.populationSize = populationSize;
        this.nbOfGenerations = nbOfGenerations;
        this.reproductionRate = reproductionRate;
        this.mutationRate = mutationRate;
        // generate random population
        generateRandomPopulation();

    }

    private void generateRandomPopulation() {
        while (population.size() < populationSize) {
            population.add(new Agent());
        }
    }

    public void nextGeneration() {
        // select agents to reproduce
        int poolSize = (int) (populationSize * reproductionRate);
//        System.out.println("pool size: " + poolSize);
        List<Agent> reproductionPool = selectPoolByTournament(poolSize);
//        System.out.println("reprpool size: " + reproductionPool.size());
        // reproduce
        List<Agent> offspring = reproduce(reproductionPool, poolSize);
//        System.out.println("offspring size: " + offspring.size());

        // select dead pool
        List<Agent> deadPool = selectPoolByTournament(poolSize, true);
//        System.out.println("dead pool size: " + deadPool.size());
        // remove deadPool from population
        removeDeadPool(deadPool);
        // merge offspring with population
        population.addAll(offspring);
        // end of cycle
        generation++;
        assert population.size() == populationSize;
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
        int poplen = population.size();
        int dedlen = deadPool.size();
        for (var agent : deadPool) {
            population.remove(agent);
        }
        assert poplen - dedlen == population.size() : "population0 len:" + poplen + " dead len: " + dedlen +
                " population after" + population.size();
    }

    /**
     * Selects from {@code population} pool of agents. Randomly, but biased towards
     * better fitted agents. The tournament selection randomly picks two agents and
     * adds agent with better fitness to the returned pool.
     *
     * @param poolSize desired size of selected pool
     * @return selected pool
     */
    private List<Agent> selectPoolByTournament(int poolSize) {
        var selectedPool = new HashSet<Agent>();
        while (selectedPool.size() < poolSize) {
//            System.out.println(selectedPool.size());
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
        return new ArrayList<>(selectedPool);
    }

    private List<Agent> selectPoolByTournament(int poolSize, boolean selectWeaker) {
        var selectedPool = new HashSet<Agent>();
        while (selectedPool.size() < poolSize) {
//            System.out.println("b");
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
            // select
            if (selectWeaker) {
                if (agent1.getFitness() < agent2.getFitness()) {
                    selectedPool.add(agent1);
                } else {
                    selectedPool.add(agent2);
                }
            } else {
                if (agent1.getFitness() > agent2.getFitness()) {
                    selectedPool.add(agent1);
                } else {
                    selectedPool.add(agent2);
                }
            }
        }
        return new ArrayList<>(selectedPool);
    }

    private Agent crossingOver(Agent parent1, Agent parent2) {
        var parent1Chromosome = parent1.getChromosome();
        var parent2Chromosome = parent2.getChromosome();

        var newChromosome = new double[parent1Chromosome.length];

        for (int i = 0; i < parent1Chromosome.length; i++) {
            newChromosome[i] = parent1Chromosome[i] * (parent1.getFitness() + 0.001) +
                    parent2Chromosome[i] * (parent2.getFitness() + 0.001);
        }
//        System.out.println(Arrays.toString(newChromosome));
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

    public double getNbOfGenerations() {
        return nbOfGenerations;
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
