package tetris.environment.engine;

/**
 * Smallest part of the {@code Tetrimino} block.
 */
public class Brick {
    private Position position;
    public final Color color;

    public Brick(Position position) {
        this.position = position;
        this.color = null; // is it bad?
    }

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
