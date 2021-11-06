import tetris.ai.Agent;
import tetris.ai.evolution.Evolution;
import tetris.environment.Environment;
import tetris.environment.engine.TetrisEngine;

import java.util.Arrays;

import static tetris.ai.Constants.GUINNESS_RECORD;
import static tetris.ai.Constants.NEURAL_NETWORK_SHAPE;
import static tetris.environment.display.Constants.AGENT_CHROMOSOME;


public class HeadlessMain {

    public static void main(String[] args) {
        System.out.println("Neural net shape: " + Arrays.toString(NEURAL_NETWORK_SHAPE));
        Environment gameEnvironment = new TetrisEngine();

        final boolean countGuinnessRecordsBroken = true;
        final int maxGames = 100;
        double guinnessRecordsBroken = 0;


        var scores = Evolution.testAgent(new Agent(AGENT_CHROMOSOME),
                gameEnvironment, maxGames, countGuinnessRecordsBroken);

        for (int i = 0; i < scores.length; i++) {
            double score = scores[i];
            if (countGuinnessRecordsBroken) {
                boolean broken = score > GUINNESS_RECORD;
                if (broken) guinnessRecordsBroken++;
                System.out.println("Game " + (i + 1) + " score: " + score + " Guinness record broken: " + broken);
            } else {
                System.out.println("Game " + (i + 1) + " score: " + score);
            }
        }
        System.out.printf("\nGuinness records broken: " + (int) guinnessRecordsBroken + "/" + maxGames
                + "  %.2f%%", (guinnessRecordsBroken * 100 / (double) maxGames));
    }

}
