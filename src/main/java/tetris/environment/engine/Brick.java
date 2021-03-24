package tetris.environment.engine;

/**
 * Smallest part of the {@code Tetrimino} block.
 */
public class Brick {
    private static int idCounter = 0;

    public final int id = idCounter++;
    public final String color;
    private Position position;

    public Brick(Position position, String color) {
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
