package tetris.environment.engine;

import javafx.geometry.Pos;

import java.util.Random;

import static tetris.environment.engine.Shape.*;
import static tetris.environment.Constants.EngineConst.GAME_FIELD_SIZE_X;


/**
 * Builds {@code Tetrimino} piece from 4 bricks,
 * randomly in 1 of 7 arrangements.
 */
public class Tetrimino {
    private static final Random random = new Random(); // or should be declared static only once for the whole project?
    private static final Shape[] shapes = {I, O, T};//, L, J, S, Z};
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
        shape = Z;// shapes[shape_index++];
        // construct tetrimino from bricks.
        // get game field middle for tetrimino spawn position
        int gameFieldMiddleX = GAME_FIELD_SIZE_X / 2;
        // build tetrimino
        if (shape == I) {
            color = Color.INDIAN_RED;
            int spawn_x = gameFieldMiddleX - 2;  // spawn on middle top
            for (int i = 0; i < 4; i++) {
                var position = new Position(spawn_x + i, 0);
                bricks[i] = new Brick(position, color);
            }
        } else if (shape == O) {
            color = Color.SLATE_GRAY;
            int spawn_x = gameFieldMiddleX - 1;
            for (int i = 0; i < 2; i++) {
                for (int j = 0; j < 2; j++) {
                    var position = new Position(spawn_x + i, j);
                    bricks[i * 2 + j] = new Brick(position, color);
                }
            }
        } else if (shape == T) {
            color = Color.MEDIUM_TURQUOISE;
            int spawn_x = gameFieldMiddleX - 1;
            int i;
            for (i = 0; i < 3; i++) {
                Position position = new Position(spawn_x + i, 0);
                bricks[i] = new Brick(position, color);
            }
            bricks[i] = new Brick(new Position(spawn_x + 1, 1), color);

        } else if (shape == L) {
            color = Color.DARK_GOLDENROD;
            int spawn_x = gameFieldMiddleX - 1;
            int i;
            for (i = 0; i < 3; i++) {
                Position position = new Position(spawn_x, i);
                bricks[i] = new Brick(position, color);
            }
            bricks[i] = new Brick(new Position(spawn_x + 1, i - 1), color);

        } else if (shape == J) {
            color = Color.MEDIUM_PURPLE;
            int spawn_x = gameFieldMiddleX - 1;
            int i;
            for (i = 0; i < 3; i++) {
                Position position = new Position(spawn_x + 1, i);
                bricks[i] = new Brick(position, color);
            }
            bricks[i] = new Brick(new Position(spawn_x, i - 1), color);

        } else if (shape == S) {
            color = Color.CORNFLOWER_BLUE;
            int spawn_x = gameFieldMiddleX - 1;
            int i;
            for (i = 0; i < 2; i++) {
                Position position = new Position(spawn_x + 1 + i, 0);
                bricks[i] = new Brick(position, color);
            }
            for (; i < 4; i++) {
                Position position = new Position(spawn_x + i, 1);
                bricks[i] = new Brick(position, color);
            }

        } else if (shape == Z) {
            color = Color.FOREST_GREEN;
            int spawn_x = gameFieldMiddleX -1;
            int i;
            for (i = 0; i < 2; i++) {
                Position position = new Position(spawn_x -1  + i, 0);
                bricks[i] = new Brick(position, color);
            }
            for (; i < 4; i++) {
                Position position = new Position(spawn_x + i, 1);
                bricks[i] = new Brick(position, color);
            }
        } else {
            color = Color.BLACK;
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
