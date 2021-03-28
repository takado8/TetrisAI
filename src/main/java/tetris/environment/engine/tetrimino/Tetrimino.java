package tetris.environment.engine.tetrimino;

import tetris.environment.engine.tetrimino.features.Color;
import tetris.environment.engine.tetrimino.features.Orientation;
import tetris.environment.engine.tetrimino.features.Shape;


/**
 * {@code Tetrimino} block is made from 4 bricks, randomly in 1 of 7 arrangements.
 */
public class Tetrimino {
    private final Shape shape;
    private final Color color;
    private final int possibleOrientationsNb;
    private final Brick[] bricks;
    private Orientation orientation;

    public Tetrimino(Shape shape, Color color, Brick[] bricks, int possibleOrientationsNb) {
        this.possibleOrientationsNb = possibleOrientationsNb;
        this.shape = shape;
        this.color = color;
        this.bricks = bricks;
        this.orientation = Orientation.HORIZONTAL_DOWN;
    }

    // bricks in array are mutable on purpose
    public Brick[] getBricks() {
        return bricks;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public int getPossibleOrientationsNb() {
        return possibleOrientationsNb;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
