package tetris.environment.engine.tetrimino;

import tetris.environment.engine.tetrimino.features.Color;
import tetris.environment.engine.tetrimino.features.Position;

/**
 * Smallest part of the {@code Tetrimino} block.
 */
public class Brick {
    private static int idCounter = 0;

    private final int id;
    private final Color color;
    private Position position;

    public Brick(Position position, Color color) {
        this.id = idCounter++;
        this.position = position;
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Color getColor() {
        return color;
    }

    protected static void resetIdCounter() {
        idCounter = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Brick brick = (Brick) o;

        return id == brick.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
