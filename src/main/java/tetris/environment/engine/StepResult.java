package tetris.environment.engine;

import tetris.environment.engine.tetrimino.Brick;
import tetris.environment.engine.tetrimino.Tetrimino;

import java.util.Arrays;
import java.util.List;

public class StepResult {
    private final boolean isFinalStep;
    private final List<Brick>  bricks;
    private final boolean tetriminoDropped;

    public StepResult(boolean isFinalStep, boolean tetriminoDropped, Tetrimino fallingTetrimino, List<Brick> staticBricks) {
        this.isFinalStep = isFinalStep;
        this.tetriminoDropped = tetriminoDropped;
        this.bricks = staticBricks;
        staticBricks.addAll(Arrays.asList(fallingTetrimino.getBricks()));
    }

    public StepResult(boolean isFinalStep, Tetrimino fallingTetrimino, List<Brick> staticBricks) {
        this.isFinalStep = isFinalStep;
        this.tetriminoDropped = false;
        this.bricks = staticBricks;
        staticBricks.addAll(Arrays.asList(fallingTetrimino.getBricks()));
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public boolean isFinalStep() {
        return isFinalStep;
    }

    public boolean isTetriminoDropped() {
        return tetriminoDropped;
    }
}
