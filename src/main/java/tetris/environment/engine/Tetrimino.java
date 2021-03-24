package tetris.environment.engine;

import java.util.Random;

import static tetris.environment.engine.Shape.*;
import static tetris.environment.Constants.EngineConst.GAME_FIELD_SIZE_X;


/**
 * Builds {@code Tetrimino} piece from 4 bricks,
 * randomly in 1 of 7 arrangements.
 */
public class Tetrimino {
    private static final Random random = new Random();
    private static final Shape[] shapes = {I, O, T, L, J, S, Z};
    private static int shape_index = shapes.length;

    public final Shape shape;
    public final String colorHex;

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
            colorHex = Color.INDIANRED;
            int spawn_x = gameFieldMiddleX - 2;  // spawn on middle top
            for (int i = 0; i < 4; i++) {
                var position = new Position(spawn_x + i, 0);
                bricks[i] = new Brick(position, colorHex);
            }
        } else if (shape == O) {
            colorHex = Color.MEDIUMTURQUOISE;
            int spawn_x = gameFieldMiddleX - 1;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    var position = new Position(spawn_x + i, j);
                    bricks[i * 2 + j] = new Brick(position, colorHex);
                }
            }
        } else if (shape == T) {
            colorHex = Color.SLATEGRAY;
            int spawn_x = gameFieldMiddleX - 1;
            int i;
            for (i = 0; i < 3; i++) {
                Position position = new Position(spawn_x + i, 0);
                bricks[i] = new Brick(position, colorHex);
            }
            bricks[i] = new Brick(new Position(spawn_x + 1, 1), colorHex);

        } else if (shape == L) {
            colorHex = Color.DARKGOLDENROD;
            int spawn_x = gameFieldMiddleX - 1;
            int i;
            for (i = 0; i < 3; i++) {
                Position position = new Position(spawn_x, i);
                bricks[i] = new Brick(position, colorHex);
            }
            bricks[i] = new Brick(new Position(spawn_x + 1, i - 1), colorHex);

        } else if (shape == J) {
            colorHex = Color.MEDIUMPURPLE;
            int spawn_x = gameFieldMiddleX - 1;
            int i;
            for (i = 0; i < 3; i++) {
                Position position = new Position(spawn_x + 1, i);
                bricks[i] = new Brick(position, colorHex);
            }
            bricks[i] = new Brick(new Position(spawn_x, i - 1), colorHex);

        } else if (shape == S) {
            colorHex = Color.CORNFLOWERBLUE;
            int spawn_x = gameFieldMiddleX - 1;
            int i;
            for (i = 0; i < 2; i++) {
                Position position = new Position(spawn_x + 1 + i, 0);
                bricks[i] = new Brick(position, colorHex);
            }
            for (; i < 4; i++) {
                Position position = new Position(spawn_x + i, 1);
                bricks[i] = new Brick(position, colorHex);
            }

        } else if (shape == Z) {
            colorHex = Color.FORESTGREEN;
            int spawn_x = gameFieldMiddleX -1;
            int i;
            for (i = 0; i < 2; i++) {
                Position position = new Position(spawn_x + i, 0);
                bricks[i] = new Brick(position, colorHex);
            }
            for (; i < 4; i++) {
                Position position = new Position(spawn_x + i -1, 1);
                bricks[i] = new Brick(position, colorHex);
            }
        } else {
            colorHex = Color.BLACK;
            int spawn_x = 0;  // Spawn in corner
            for (int i = 0; i < 4; i++) {
                var position = new Position(spawn_x + i, 0);
                bricks[i] = new Brick(position, colorHex);
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
