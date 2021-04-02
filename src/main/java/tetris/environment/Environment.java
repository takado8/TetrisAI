package tetris.environment;


import tetris.ai.Agent;
import tetris.environment.engine.Action;
import tetris.environment.engine.results.StepResult;

public interface Environment {
    StepResult step(Action action);
    StepResult reset();
    void runFullSimulation(Agent aiAgent);
}
