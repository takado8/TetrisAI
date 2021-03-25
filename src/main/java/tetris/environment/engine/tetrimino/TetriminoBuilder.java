package tetris.environment.engine.tetrimino;

import tetris.environment.engine.Brick;
import tetris.environment.engine.Color;
import tetris.environment.engine.Position;
import tetris.environment.engine.Shape;

import static tetris.environment.Constants.EngineConst.GAME_FIELD_SIZE_X;
import static tetris.environment.engine.Shape.*;

/**
 * Builds {@code Tetrimino} piece from 4 {@code Bricks},
 * randomly in 1 of 7 arrangements.
 */
public class TetriminoBuilder {
    private final RandomShapeGenerator shapeGenerator;

    public TetriminoBuilder() {
        this.shapeGenerator = new RandomShapeGenerator();
    }

    /**
     * Builds new {@code Tetrimino}
     * @return {@code Tetrimino} with random shape
     */
    public Tetrimino buildNewTetrimino() {
        // get shape for new tetrimino
        Shape shape = shapeGenerator.getNextShape();
        // build and return tetrimino
        switch (shape) {
            case SHAPE_I:
                return buildTetriminoShapeI();
            case SHAPE_O:
                return buildTetriminoShapeO();
            case SHAPE_T:
                return buildTetriminoShapeT();
            case SHAPE_L:
                return buildTetriminoShapeL();
            case SHAPE_J:
                return buildTetriminoShapeJ();
            case SHAPE_S:
                return buildTetriminoShapeS();
            case SHAPE_Z:
                return buildTetriminoShapeZ();
            default:
                return buildTetriminoShapeIColorBlack();
        }
    }

    private Tetrimino buildTetriminoShapeI() {
        Color color = Color.INDIANRED;
        return buildTetriminoShapeIBase(color);
    }

    private Tetrimino buildTetriminoShapeIColorBlack() {
        Color color = Color.BLACK;
        return buildTetriminoShapeIBase(color);
    }

    private Tetrimino buildTetriminoShapeIBase(Color color) {
        Brick[] bricks = new Brick[4];
        int spawnX = getSpawnPositionX();
        spawnX--;  // longest tetrimino needs to spawn 1 field more to the left
        for (int i = 0; i < 4; i++) {
            var position = new Position(spawnX + i, 0);
            bricks[i] = new Brick(position, color);
        }
        return new Tetrimino(SHAPE_I, color, bricks);
    }

    private Tetrimino buildTetriminoShapeO() {
        Color color = Color.MEDIUMTURQUOISE;
        Brick[] bricks = new Brick[4];
        int spawnX = getSpawnPositionX();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                var position = new Position(spawnX + i, j);
                bricks[i * 2 + j] = new Brick(position, color);
            }
        }
        return new Tetrimino(SHAPE_O, color, bricks);
    }

    private Tetrimino buildTetriminoShapeT() {
        Color color = Color.SLATEGRAY;
        Brick[] bricks = new Brick[4];
        int spawnX = getSpawnPositionX();
        int i;
        for (i = 0; i < 3; i++) {
            Position position = new Position(spawnX + i, 0);
            bricks[i] = new Brick(position, color);
        }
        bricks[i] = new Brick(new Position(spawnX + 1, 1), color);
        return new Tetrimino(SHAPE_T, color, bricks);
    }

    private Tetrimino buildTetriminoShapeL() {
        Color color = Color.DARKGOLDENROD;
        Brick[] bricks = new Brick[4];
        int spawnX = getSpawnPositionX();
        int i;
        for (i = 0; i < 3; i++) {
            Position position = new Position(spawnX, i);
            bricks[i] = new Brick(position, color);
        }
        bricks[i] = new Brick(new Position(spawnX + 1, i - 1), color);
        return new Tetrimino(SHAPE_L, color, bricks);
    }

    private Tetrimino buildTetriminoShapeJ() {
        Color color = Color.MEDIUMPURPLE;
        Brick[] bricks = new Brick[4];
        int spawnX = getSpawnPositionX();
        int i;
        for (i = 0; i < 3; i++) {
            Position position = new Position(spawnX + 1, i);
            bricks[i] = new Brick(position, color);
        }
        bricks[i] = new Brick(new Position(spawnX, i - 1), color);
        return new Tetrimino(SHAPE_J, color, bricks);
    }

    private Tetrimino buildTetriminoShapeS() {
        Color color = Color.CORNFLOWERBLUE;
        Brick[] bricks = new Brick[4];
        int spawnX = getSpawnPositionX();
        int i;
        for (i = 0; i < 2; i++) {
            Position position = new Position(spawnX + 1 + i, 0);
            bricks[i] = new Brick(position, color);
        }
        for (; i < 4; i++) {
            Position position = new Position(spawnX + i, 1);
            bricks[i] = new Brick(position, color);
        }
        return new Tetrimino(SHAPE_S, color, bricks);
    }

    private Tetrimino buildTetriminoShapeZ() {
        Color color = Color.FORESTGREEN;
        Brick[] bricks = new Brick[4];
        int spawnX = getSpawnPositionX();
        int i;
        for (i = 0; i < 2; i++) {
            Position position = new Position(spawnX + i, 0);
            bricks[i] = new Brick(position, color);
        }
        for (; i < 4; i++) {
            Position position = new Position(spawnX + i - 1, 1);
            bricks[i] = new Brick(position, color);
        }
        return new Tetrimino(SHAPE_Z, color, bricks);
    }

    /**
     * @return x coordinate needed to spawn {@code Tetrimino} in the middle top of the game field.
     */
    private int getSpawnPositionX() {
        return GAME_FIELD_SIZE_X / 2 - 1;
    }


}
