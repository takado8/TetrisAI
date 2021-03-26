package tetris.environment.engine;

import tetris.environment.engine.tetrimino.Brick;
import tetris.environment.engine.tetrimino.Tetrimino;

import java.util.Arrays;
import java.util.List;

public class StepResult {
    public final boolean isFinalStep;

    public final List<Brick>  bricks;

    public StepResult(boolean isFinalStep, Tetrimino fallingTetrimino, List<Brick> staticBricks) {
        this.isFinalStep = isFinalStep;
        this.bricks = staticBricks;
        staticBricks.addAll(Arrays.asList(fallingTetrimino.getBricks()));
    }

    public boolean isFinalStep() {
        return isFinalStep;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

}
