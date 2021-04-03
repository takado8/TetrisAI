import tetris.ai.Agent;
import tetris.ai.evolution.Evolution;
import tetris.environment.Environment;
import tetris.environment.engine.TetrisEngine;

import static tetris.environment.display.Constants.AGENT_CHROMOSOME;


public class HeadlessMain {

    public static void main(String[] args) {
        System.out.println("Crossing over genes test");
        Environment gameEnvironment = new TetrisEngine();

//        Evolution evolution = new Evolution(gameEnvironment, 420,
//                120, 0.6, 0.07);
//
//        var evolvedAgent = evolution.evolve(600, 3, 200);
//        Evolution.testAgent(evolvedAgent, gameEnvironment, 3);

        var scores = Evolution.testAgent(new Agent(AGENT_CHROMOSOME), gameEnvironment, 3);
        for (int i = 0; i < scores.length; i++) {
            double score = scores[i];
            System.out.println("Game " + i + " score: " + score);
        }
    }
}
