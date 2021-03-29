package tetris.environment;


import tetris.ai.Agent;
import tetris.environment.engine.Action;
import tetris.environment.engine.StepResult;

public interface Environment {
    StepResult step(Action action);
    StepResult reset();
    void simulate(Agent aiAgent);
}
