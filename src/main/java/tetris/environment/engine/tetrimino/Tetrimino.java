package tetris.environment.engine.tetrimino;

import tetris.environment.engine.tetrimino.features.Color;
import tetris.environment.engine.tetrimino.features.Shape;


/**
 * {@code Tetrimino} block is made from 4 bricks, randomly in 1 of 7 arrangements.
 */
public class Tetrimino {
    private final Shape shape;
    private final Color color;
    private final Brick[] bricks;

    public Tetrimino(Shape shape, Color color, Brick[] bricks) {
        this.shape = shape;
        this.color = color;
        this.bricks = bricks;
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
}
