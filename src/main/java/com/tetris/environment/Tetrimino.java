package com.tetris.environment;

import java.util.Random;

import static com.tetris.environment.Shape.*;
import static com.tetris.environment.Constants.GAME_FIELD_SIZE_X;


/**
 * Builds {@code Tetrimino} piece from 4 bricks,
 * randomly in 1 of 7 arrangements.
 */
public class Tetrimino {
    private static final Random random = new Random(); // or should be declared static only once for the whole project?
    private static final Shape[] shapes = {I, O};  //, T, L, J, S, Z};
    private static int shape_index = shapes.length;

    public final Shape shape;
    public final Color color;

    public final Brick[] bricks = new Brick[4];

    public Tetrimino() {
        // determine tetrimino shape
        if (shape_index == shapes.length) {
            Shuffle();
            shape_index = 0;
        }
        shape = shapes[shape_index++];
        // construct tetrimino from bricks.
        // get game field middle for tetrimino spawn position
        int gameFieldMiddleX = GAME_FIELD_SIZE_X / 2;
        // build tetrimino
        if (shape == I) {
            color = Color.blue;
            int spawn_x = gameFieldMiddleX - 2;  // spawn on middle top
            for (int i = 0; i < 4; i++) {
                var position = new Position(spawn_x + i, 0);
                bricks[i] = new Brick(position, color);
            }
        } else if (shape == O) {
            color = Color.red;
            int spawn_x = gameFieldMiddleX - 1;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    var position = new Position(spawn_x + i, j);
                    bricks[i * 2 + j] = new Brick(position);
                }
            }
        } else {
            color = Color.black;
            int spawn_x = 0;  // Spawn in corner
            for (int i = 0; i < 4; i++) {
                var position = new Position(spawn_x + i, 0);
                bricks[i] = new Brick(position, color);
            }
        }
    }

    public Brick[] getBricks() {
        return bricks;
    }

    private void Shuffle() {
        int n = shapes.length;
        while (n > 1) {
            n--;
            int k = random.nextInt(n + 1);
            var value = shapes[k];
            shapes[k] = shapes[n];
            shapes[n] = value;
        }
    }

}
