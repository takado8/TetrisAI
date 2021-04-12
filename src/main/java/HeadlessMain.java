import tetris.ai.Agent;
import tetris.ai.evolution.Evolution;
import tetris.environment.Environment;
import tetris.environment.engine.TetrisEngine;

import java.util.Arrays;

import static tetris.ai.Constants.NEURAL_NETWORK_SHAPE;
import static tetris.environment.display.Constants.AGENT_CHROMOSOME;


public class HeadlessMain {

    public static void main(String[] args) {
        System.out.println("Neural net shape: " + Arrays.toString(NEURAL_NETWORK_SHAPE));
        Environment gameEnvironment = new TetrisEngine();
//
//        Evolution evolution = new Evolution(gameEnvironment, 6000,
//                200, 0.4, 0.1);
//
//        var evolvedAgent = evolution.evolve(2000, 1, 300);
//        Evolution.testAgent(evolvedAgent, gameEnvironment, 3);

        var scores = Evolution.testAgent(new Agent(AGENT_CHROMOSOME), gameEnvironment, 3);
        for (int i = 0; i < scores.length; i++) {
            double score = scores[i];
            System.out.println("Game " + i + " score: " + score);
        }
    }
}
