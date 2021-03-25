package tetris.environment.engine;

import java.util.Random;

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
}
