package tetris.environment.engine.tetrimino;

import tetris.environment.engine.tetrimino.features.Color;
import tetris.environment.engine.tetrimino.features.Position;

/**
 * Smallest part of the {@code Tetrimino} block.
 */
public class Brick {
    private static int idCounter = 0;

    public final int id = idCounter++;
    public final Color color;
    private Position position;

    public Brick(Position position, Color color) {
        this.position = position;
        this.color = color;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
