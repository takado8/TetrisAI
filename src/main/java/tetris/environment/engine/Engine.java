package tetris.environment.engine;

import java.util.*;

import static tetris.environment.Constants.EngineConst.*;

/**
 * Responsible for game logic - maintaining game field,
 * moving Tetrimino etc.
 */
public class Engine {
    private final int[][] gameFieldArr = new int[GAME_FIELD_SIZE_Y][GAME_FIELD_SIZE_X];
    private Tetrimino fallingTetrimino;
    private int gameScore = 0;
    private int totalGameScore = 0;
    private final Map<Position, Brick> stableBricksDict = new HashMap<>();

    public Engine() {
        System.out.println("\nInitializing game engine.");
    }

    /**
     * Take next step in environment
     */
    public StepResult step(Action action) {
        printGameFieldArr();
        // check if the game is over
        if (checkGameOver()) {
            // game is over
            totalGameScore += gameScore;
            gameScore = 0;
            System.out.println("> GAME OVER <");
            return new StepResult(true, fallingTetrimino, new ArrayList<>(stableBricksDict.values()));
        }
        // game is not over
        // execute action from user/operator
        // move tetrimino left/right
        if (action != null && canMove(action)) {
            move(action);
        }
        // move tetrimino down (every turn)
        if (canMoveDown()) {
            move(Action.moveDown);
        } else {
            putDownTetrimino();
            // check if any lines are completed, remove them, return number of removed.
            int stepScore = checkAndRemoveLinesCompleted();
            gameScore += stepScore;
        }
        return new StepResult(false, fallingTetrimino, new ArrayList<>(stableBricksDict.values()));
    }

    /**
     * Set environment to initial condition and return initial observation
     */
    public StepResult reset() {
        initGameField();
        addNewFallingTetriminoToGameField();
        stableBricksDict.clear();
        return new StepResult(false, fallingTetrimino, new ArrayList<>());
    }

    /**
     * Checks if any lines are completed. Clears completed lines, and moves all above pieces one line down
     *
     * @return number of cleared lines
     */
    private int checkAndRemoveLinesCompleted() {
        // if sum of values in line equals full possible line value, than line is complete
        int clearedLines = 0;
        // loop through the game field array bottom up
        for (int y = gameFieldArr.length - 1; y >= 0; y--) {
            // sum stableBricks in line
            int lineStableBricks = 0;
            for (int value : gameFieldArr[y]) {
                if (value == GAME_FIELD_BRICK_STABLE) {
                    lineStableBricks++;
                }
            }
            // check if line is complete
            if (lineStableBricks == GAME_FIELD_SIZE_X) {
                // line is complete
                clearedLines++;
                // clear bricks from stableBrickDict
                for (int x = 0; x < gameFieldArr[y].length; x++) {
                    Position pos = new Position(x, y);
                    stableBricksDict.remove(pos);
                }
                // move all the above lines down, by one row, starting from cleared index
                // remap positions in all above bricks
                for (int k = y; k > 0; k--) {
                    gameFieldArr[k] = gameFieldArr[k - 1];
                    for (int i = 0; i < gameFieldArr[k].length; i++) {
                        Position oldPos = new Position(i, k - 1);
                        Brick oldBrick = stableBricksDict.get(oldPos);
                        if (oldBrick != null) {
                            stableBricksDict.remove(oldPos);
                            Position newPos = new Position(i, k);
                            oldBrick.setPosition(newPos);
                            stableBricksDict.put(newPos, oldBrick);
                        }
                    }
                }
                // top line should be empty
                Arrays.fill(gameFieldArr[0], GAME_FIELD_EMPTY);
                // everything moved down, so we need to check same line again, increment i
                y++;
            }
        }
        return clearedLines;
    }

    /**
     * Checks if any stable brick touches top of the map
     *
     * @return true if it does, false otherwise
     */
    private boolean checkGameOver() {
        for (var position : gameFieldArr[0]) {
            if (position == GAME_FIELD_BRICK_STABLE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Changes {@code fallingTetrimino} {@code gameFieldArr} states from falling to stable, moves its Bricks to the
     * {@code stableBricksDict} and adds new {@code fallingTetrimino} to the game field
     */
    private void putDownTetrimino() {
        for (var brick : fallingTetrimino.getBricks()) {
            Position position = brick.getPosition();
            stableBricksDict.put(position, brick);
            gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_STABLE;
        }

        addNewFallingTetriminoToGameField();
    }

    private void addNewFallingTetriminoToGameField() {
        fallingTetrimino = new Tetrimino();
        // add tetrimino bricks to game field array
        for (var brick : fallingTetrimino.getBricks()) {
            var position = brick.getPosition();
            // dont overwrite not empty values, changes result of checkGameOver()
            if (gameFieldArr[position.getY()][position.getX()] == GAME_FIELD_EMPTY) {
                gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_FALLING;
            }
        }
    }

    /**
     * Moves down {@code fallingTetrimino}
     */
    private void move(Action action) {
        int addX;
        int addY;
        switch (action) {
            case moveDown:
                addX = 0;
                addY = 1;
                break;
            case moveLeft:
                addX = -1;
                addY = 0;
                break;
            case moveRight:
                addX = 1;
                addY = 0;
                break;
            default:
                return;
        }
        Brick[] bricks = fallingTetrimino.getBricks();
        Position[] newPositions = new Position[4];
        // get new positions and erase old bricks
        for (int i = 0; i < bricks.length; i++) {
            Brick brick = bricks[i];
            var position = brick.getPosition();
            var x = position.getX();
            var y = position.getY();
            // draw brick in a new position
            var new_y = y + addY;
            var new_x = x + addX;
            var newPosition = new Position(new_x, new_y);
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
     * Checks if {@code fallingTetrimino} can move in {@code Direction}
     *
     * @return true if can, false otherwise
     */
    private boolean canMove(Action action) {
        int addX;
        switch (action) {
            case moveLeft:
                addX = -1;
                break;
            case moveRight:
                addX = 1;
                break;
            default:
                return false;
        }
        for (var brick : fallingTetrimino.getBricks()) {
            var pos = brick.getPosition();
            var newX = pos.getX() + addX;
            if (newX >= GAME_FIELD_SIZE_X || newX < 0 ||
                    gameFieldArr[pos.getY()][newX] == GAME_FIELD_BRICK_STABLE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if {@code fallingTetrimino} can move down
     *
     * @return true if can,false otherwise and false if fallingTetrimino is null
     */
    private boolean canMoveDown() {
        for (var brick : fallingTetrimino.getBricks()) {
            var pos = brick.getPosition();
            var new_y = pos.getY() + 1;
            if (new_y >= GAME_FIELD_SIZE_Y || gameFieldArr[new_y][pos.getX()] == GAME_FIELD_BRICK_STABLE) {
                return false;
            }
        }
        return true;
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

    public int[][] getGameFieldArr() {
        return Arrays.stream(gameFieldArr).map(int[]::clone).toArray(int[][]::new);
    }
}
