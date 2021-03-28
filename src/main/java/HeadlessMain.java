import tetris.ai.Agent;
import tetris.ai.Evolution;
import tetris.environment.engine.Action;
import tetris.environment.engine.Engine;
import tetris.environment.engine.StepResult;

import java.util.Arrays;


public class HeadlessMain {

    public static void main(String[] args) {
        System.out.println("Headless main()");
        int nbOfGenerations = 600;
        int populationSize = 200;
        int maxTurns = 10_000;
        double bestScore = -Double.MAX_VALUE;

        Evolution evolution = new Evolution(populationSize, nbOfGenerations, 0.5, 0.05);
        Engine engine = new Engine(false);

        // for number of generations
        for (int g = 0; g < nbOfGenerations; g++) {
            // track generation performance
            double generationScores = 0;
            double generationBestScore = -1;

            // each agent in population plays game
            for (Agent agent : evolution.getPopulation()) {
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
                agent.setFitness(stepResult.getGameScore());
                generationScores += stepResult.getGameScore();
                if (stepResult.getGameScore() > generationBestScore) {
                    generationBestScore = stepResult.getGameScore();

                    if (stepResult.getGameScore() > bestScore) {
                        bestScore = stepResult.getGameScore();
                    }
                }
                // next agent
            }
            // next generation
            // print some info about last generation performance
            System.out.println("Generation " + g + " avg score: " + generationScores / populationSize
                    + " generation best: "+ generationBestScore +" total best score: " + bestScore );
            // evolve
            evolution.nextGeneration();
//            System.out.println(Arrays.toString(evolution.getPopulation().get(0).getChromosome()));
        }
    }
}