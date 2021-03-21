package tetris.environment.engine;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static tetris.environment.engine.Constants.*;

/**
 * Central class of tetris environment, responsible for game logic - maintaining game field,
 * moving Tetrimino etc.
 */
public class Engine {
    private final int[][] gameFieldArr = new int[GAME_FIELD_SIZE_Y][GAME_FIELD_SIZE_X];
    private Tetrimino fallingTetrimino;
    private int gameScore = 0;
    private int totalGameScore = 0;

    public Engine() {
        System.out.println("\nInitializing game engine...");
        initGameField();
        System.out.println("Finished initializing game engine\n");
        reset();
    }

    /**
     * Take next step in environment
     */
    public boolean step() {
        // render
        printGameFieldArr();
        // some delay
        sleep(1);
        // take action from user/operator
        // (no action for now)
        // execute action
        // (no action for now)
        // move piece down (every turn)
        boolean isFinalStep = false;
        if (canMoveDown()) {
            moveDown();
        } else {
            putDownTetrimino();
            // check if any lines are completed
            int stepScore = checkLinesCompleted();
            gameScore += stepScore;
            // check if the game is over
            if (checkGameOver()) {
                // game is over
                isFinalStep = true;
                totalGameScore += gameScore;
                gameScore = 0;
                System.out.println("> GAME OVER <");
            } else {
                // game is not over, add new tetrimino
                addNewFallingTetriminoToGameField();
            }
        }
        return isFinalStep;
    }

    /**
     * Set environment to initial condition and return initial observation
     */
    public int[][] reset() {
        initGameField();
        addNewFallingTetriminoToGameField();
        return getGameFieldArr();
    }

    /**
     * Checks if any lines are completed. Clears completed lines, and moves all above pieces one line down
     *
     * @return number of cleared lines
     */
    private int checkLinesCompleted() {
        // if sum of values in line equals full possible line value, than line is complete
        int clearedLines = 0;
        int lineMaxValue = GAME_FIELD_SIZE_X * GAME_FIELD_BRICK_STATIC;
        // loop through the game field array bottom up
        for (int i = gameFieldArr.length - 1; i >= 0; i--) {
            // sum line
            int lineValue = 0;
            for (int value : gameFieldArr[i]) {
                lineValue += value;
            }
            // check if line is complete
            if (lineValue == lineMaxValue) {
                // line is complete
                clearedLines++;
                // clear line
                Arrays.fill(gameFieldArr[i], GAME_FIELD_EMPTY);  // faster than loop?
                // move all the above lines down by one row, starting from cleared index
                for (int k = i; k > 0; k--) {
                    gameFieldArr[k] = gameFieldArr[k - 1];
                }
                // top line should be empty
                Arrays.fill(gameFieldArr[0], GAME_FIELD_EMPTY);
                // everything moved down, so we need to check same line again
                i++;
            }
        }
        return clearedLines;
    }

    /**
     * Checks if any brick touches top of the map
     *
     * @return true if it does, false otherwise
     */
    private boolean checkGameOver() {
        for (var position : gameFieldArr[0]) {
            if (position != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Puts down tetrimino, changes field states from falling to static and
     * sets {@code fallingTetrimino} variable to null
     */
    private void putDownTetrimino() {
        for (var brick : fallingTetrimino.getBricks()) {
            var position = brick.getPosition();
            gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_STATIC;
        }
        fallingTetrimino = null;
    }

    /**
     * Moves down {@code fallingTetrimino}
     */
    private void moveDown() {
        Brick[] bricks = fallingTetrimino.getBricks();
        Position[] newPositions = new Position[4];
        // get new positions and erase old bricks
        for (int i = 0; i < bricks.length; i++) {
            Brick brick = bricks[i];
            var position = brick.getPosition();
            var x = position.getX();
            var y = position.getY();
            // draw brick in new position
            var new_y = y + 1;
            var newPosition = new Position(x, new_y);
            newPositions[i] = newPosition;
            // erase old brick from array
            gameFieldArr[y][x] = GAME_FIELD_EMPTY;
            // change position in brick object
            fallingTetrimino.bricks[i].setPosition(newPosition);
        }
        // draw new bricks on array
        for (var newPosition : newPositions) {
            gameFieldArr[newPosition.getY()][newPosition.getX()] = GAME_FIELD_BRICK_FALLING;
        }
    }

    /**
     * Checks if {@code fallingTetrimino} can move down
     *
     * @return true if can, false otherwise
     */
    private boolean canMoveDown() {
        for (var brick : fallingTetrimino.getBricks()) {
            var pos = brick.getPosition();
            var new_y = pos.getY() + 1;
            if (new_y >= GAME_FIELD_SIZE_Y || gameFieldArr[new_y][pos.getX()] == GAME_FIELD_BRICK_STATIC) {
                return false;
            }
        }
        return true;
    }

    private void addNewFallingTetriminoToGameField() {
        fallingTetrimino = new Tetrimino();
        // add tetrimino bricks to game field array
        for (var brick : fallingTetrimino.getBricks()) {
            var position = brick.getPosition();
            gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_FALLING;
        }
    }

    /**
     * Init {@code gameFieldArr} with {@code GAME_FIELD_EMPTY} values
     */
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

    void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            // how to actually raise that exception?
            System.out.println(e.getMessage());
        }
    }

    // only to test new getGameFieldArr with copy
    public int[][] getGameFieldArrNoCopy() {
        return gameFieldArr;
    }

    public int[][] getGameFieldArr() {
        return Arrays.stream(gameFieldArr).map(int[]::clone).toArray(int[][]::new);
    }
}
