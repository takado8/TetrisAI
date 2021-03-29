import tetris.ai.Agent;
import tetris.ai.Evolution;
import tetris.environment.engine.Action;
import tetris.environment.engine.Engine;
import tetris.environment.engine.StepResult;

import java.util.Arrays;


public class HeadlessMain {

    public static void main(String[] args) {
        System.out.println("Headless main()");
        int nbOfGenerations = 2000;
        int populationSize = 240;
        int maxTurns = 1000; // max 400 score
        int maxGames = 10;
        double bestScore = -Double.MAX_VALUE;
        Agent bestAgent = new Agent();
        Agent bestGenerationAgent = new Agent();

        Evolution evolution = new Evolution(populationSize, nbOfGenerations, 0.5, 0.15);
        Engine engine = new Engine(false);

        // for number of generations
        for (int g = 0; g < nbOfGenerations; g++) {
            // track generation performance
            double generationScores = 0;
            double generationBestScore = -1;

            // each agent in population plays n games
            for (Agent agent : evolution.getPopulation()) {
                double agentScores = 0;
                for (int n = 0; n < maxGames; n++) {
                    // set environment to initial state
                    engine.reset();
                    StepResult stepResult = null;
                    // until game is not over
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
                    agentScores += stepResult.getGameScore();
                }
                generationScores += agentScores;
                agentScores /= maxGames;
                agent.setFitness(agentScores);
                // count high score etc
                if (agentScores > generationBestScore) {
                    generationBestScore = agentScores;
                    bestGenerationAgent = agent;
                    if (agentScores >= bestScore) {
                        bestScore = agentScores;
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
                System.out.println("Best score ever chromosome: " + Arrays.toString(bestAgent.getChromosome()) );
                System.out.println("Best score generation chromosome: " + Arrays.toString(bestGenerationAgent.getChromosome()) );
            }            // evolve
            evolution.nextGeneration();
//            System.out.println(Arrays.toString(evolution.getPopulation().get(0).getChromosome()));
        }
        System.out.println("Best score ever chromosome: " + Arrays.toString(bestAgent.getChromosome()) );
        System.out.println("Best score generation chromosome: " + Arrays.toString(bestGenerationAgent.getChromosome()) );
    }
}