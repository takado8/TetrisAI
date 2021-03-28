package tetris.environment.engine;

import tetris.environment.engine.tetrimino.Brick;
import tetris.environment.engine.tetrimino.Tetrimino;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StepResult {
    private final List<Brick>  bricks;
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
        this.gameScore =  gameScore;
        staticBricks.addAll(Arrays.asList(fallingTetrimino.getBricks()));
    }

    public StepResult(boolean isFinalStep, double gameScore) {
        this.isFinalStep = isFinalStep;
        this.tetriminoDropped = true;
        this.gameScore = gameScore;
        this.bricks = new ArrayList<>();
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
                "bricks=" + bricks +
                ", gameScore=" + gameScore +
                ", isFinalStep=" + isFinalStep +
                ", tetriminoDropped=" + tetriminoDropped +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

}
