//import tetris.ai.Agent;
import tetris.ai.Evolution;
import tetris.environment.Environment;
import tetris.environment.engine.Engine;

public class HeadlessMain {

    public static void main(String[] args) {
        Environment gameEnvironment = new Engine(false);

        Evolution evolution = new Evolution(gameEnvironment,
                200, 0.5, 0.2);

        var evolvedAgent = evolution.evolve(120, 3, 100);
        evolution.testAgent(evolvedAgent, 30);
    }
}