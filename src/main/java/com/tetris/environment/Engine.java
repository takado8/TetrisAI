package com.tetris.environment;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static com.tetris.environment.Constants.*;

/**
 * Central class of tetris environment, responsible for game logic - maintaining game field,
 * moving Tetrimino etc.
 */
public class Engine {
    int[][] gameFieldArr = new int[GAME_FIELD_SIZE_Y][GAME_FIELD_SIZE_X];
    Tetrimino fallingTetrimino;

    public Engine() {
        System.out.println("\nInitializing game engine...");
        initGameField();
        addNewFallingTetriminoToGameField();
        System.out.println("Finished initializing game engine\n");
    }

    /**
     * Take next step in environment
     */
    public void step() {
        while (moveDown()) {
            printGameFieldArr();
            try {
                TimeUnit.SECONDS.sleep(2);
            }
            catch (InterruptedException e) {
                // how to actually raise that exception?
                System.out.println(e.getMessage());
            }
        }
        printGameFieldArr();

    }

    /**
     * Moves down {@code fallingTetrimino}
     */
    private boolean moveDown() {
        if (canMoveDown()) {   // move down tetrimino
            Brick[] bricks = fallingTetrimino.getBricks();
            for (int i = 0; i < bricks.length; i++) {
                Brick brick = bricks[i];
                var position = brick.getPosition();
                var x = position.getX();
                var y = position.getY();
                // draw brick in new position
                var new_y = y + 1;
                gameFieldArr[new_y][x] = GAME_FIELD_BRICK_FALLING;
                // erase old brick from array
                gameFieldArr[y][x] = GAME_FIELD_EMPTY;
                // change position in brick object
                fallingTetrimino.bricks[i].setPosition(new Position(x, new_y));
            }
            return true;
        } else {   // change game field states
            for (var brick : fallingTetrimino.getBricks()) {
                var position = brick.getPosition();
                gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_STATIC;
            }
            return false;
        }
    }

    /**
     * Checks if {@code fallingTetrimino} can move down
     * @return true if can, false otherwise
     */
    private boolean canMoveDown() {
        for(var brick : fallingTetrimino.getBricks()) {
            var pos = brick.getPosition();
            var new_y = pos.getY() + 1;
            if (new_y >= GAME_FIELD_SIZE_Y || gameFieldArr[new_y][pos.getX()] == GAME_FIELD_BRICK_STATIC) {
                return false;
            }
        }
        return true;
    }

    public void addNewFallingTetriminoToGameField() {
        fallingTetrimino = new Tetrimino();
        for (var brick : fallingTetrimino.getBricks()) {
            var position = brick.getPosition();
            gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_FALLING;
        }
    }

    private void initGameField() {
        for (int i = 0; i < gameFieldArr.length; i++) {
            for (int j = 0; j < gameFieldArr[0].length; j++) {
                gameFieldArr[i][j] = GAME_FIELD_EMPTY;
            }
        }
    }

    public void printGameFieldArr() {
        System.out.println("\nPrinting game field array:");
        for (var row : gameFieldArr) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }
}
