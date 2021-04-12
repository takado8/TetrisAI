package tetris.environment.engine.tetrimino;

import tetris.environment.engine.tetrimino.features.Color;
import tetris.environment.engine.tetrimino.features.Position;
import tetris.environment.engine.tetrimino.features.Shape;


/**
 * {@code Tetrimino} block is made from 4 bricks, randomly in 1 of 7 arrangements.
 */
public class Tetrimino {
    private static int idCounter = 0;

    private final int id;
    private final Shape shape;
    private final Color color;
    private final int possibleOrientationsNb;
    private final Brick[] bricks;

    public Tetrimino(Shape shape, Color color, Brick[] bricks, int possibleOrientationsNb) {
        this.id = idCounter++;
        this.possibleOrientationsNb = possibleOrientationsNb;
        this.shape = shape;
        this.color = color;
        this.bricks = bricks;
    }

    public Tetrimino(Tetrimino tetrimino) {
        this.id = idCounter++;
        this.possibleOrientationsNb = tetrimino.getPossibleOrientationsNb();
        this.shape = tetrimino.getShape();
        this.color = tetrimino.getColor();
        this.bricks = tetrimino.getBricks();
    }

    public Brick[] getBricks() {
        return bricks;
    }

    public Shape getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public Position[] getPositions() {
        Position[] bricksStartingPositions = new Position[4];
        for (int i = 0; i < 4; i++) {
            bricksStartingPositions[i] = new Position(bricks[i].getPosition());
        }
        return bricksStartingPositions;
    }

    public int getPossibleOrientationsNb() {
        return possibleOrientationsNb;
    }

    public static void resetIdCounter() {
        idCounter = 0;
        Brick.resetIdCounter();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tetrimino tetrimino = (Tetrimino) o;

        return id == tetrimino.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
