package tetris.environment.engine.results;

import tetris.environment.engine.tetrimino.Brick;
import tetris.environment.engine.tetrimino.Tetrimino;

import java.util.Arrays;
import java.util.List;

public class StepResult {
    private final List<Brick> bricks;
    private final double gameScore;
    private final boolean isFinalStep;
    private final boolean tetriminoDropped;

    public StepResult(boolean isFinalStep, boolean tetriminoDropped, double gameScore, Tetrimino fallingTetrimino, List<Brick> staticBricks) {
        this.isFinalStep = isFinalStep;
        this.tetriminoDropped = tetriminoDropped;
        this.bricks = staticBricks;
        this.gameScore = gameScore;
        staticBricks.addAll(Arrays.asList(fallingTetrimino.getBricks()));
    }

    public StepResult(boolean isFinalStep, double gameScore, Tetrimino fallingTetrimino, List<Brick> staticBricks) {
        this.isFinalStep = isFinalStep;
        this.tetriminoDropped = false;
        this.bricks = staticBricks;
        this.gameScore = gameScore;
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

    public double getGameScore() {
        return gameScore;
    }

    @Override
    public String toString() {
        return "StepResult{" +
                ", gameScore=" + gameScore +
                ", isFinalStep=" + isFinalStep +
                ", tetriminoDropped=" + tetriminoDropped +
                '}';
    }
}
