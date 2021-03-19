package com.tetris.environment;

import java.util.ArrayList;

/**
 * Smallest part of the {@code Tetrimino} block.
 */
public class Brick {
    public final Color color;
    private Position position;

    public Brick(Position position) {
        this.position = position;
        this.color = null;
    }

    public Brick(Color color, Position position) {
        this.color = color;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
