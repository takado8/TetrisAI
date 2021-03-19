package com.tetris.environment;

import java.util.Random;

import static com.tetris.environment.Shape.*;

/**
 * {@code Tetrimino} class builds Tetrimino piece from 4 bricks,
 * randomly in 1 of 7 arrangements.
 */
public class Tetrimino {
    static Random random = new Random();
    static Shape[] shapes = {I, O, T, L, J, S, Z};
    static int shape_index = 7;

    public final Shape shape;
//    public final Color color;
    private Brick[] bricks = new Brick[4];

    public Tetrimino() {
        if (shape_index == shapes.length) {
            Shuffle();
            shape_index = 0;
        }
        shape = shapes[shape_index++];
        buildTetrimino();
    }

    /**
     * create 4 {@code Brick}s in correct positions according to Tetrimino shape and color and add them to {@code bricks} array.
     */
    private void buildTetrimino() {
        // need to know tetrimino spawn position, and game field size
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
