package tetris.environment.engine;

import tetris.environment.engine.tetrimino.Tetrimino;
import tetris.environment.engine.tetrimino.TetriminoBuilder;

import java.util.*;

import static tetris.environment.Constants.EngineConst.*;

/**
 * Responsible for game logic - maintaining game field,
 * moving Tetrimino etc.
 */
public class Engine {
    private final int[][] gameFieldArr;
    private final TetriminoBuilder tetriminoBuilder;
    private final Map<Position, Brick> stableBricksMap;

    private Tetrimino fallingTetrimino;
    private int gameScore = 0;
    private int totalGameScore = 0;

    public Engine() {
        System.out.println("\nInitializing game engine.");
        gameFieldArr = new int[GAME_FIELD_SIZE_Y][GAME_FIELD_SIZE_X];
        tetriminoBuilder = new TetriminoBuilder();
        stableBricksMap = new HashMap<>();

        fillGameFieldWithValuesEmpty();
        addNewFallingTetriminoToGameField();
    }

    /**
     * Take next step in environment
     */
    public StepResult step(Direction direction) {
        // check if the game is over
        if (isGameOver()) {
            // game is over
            handleGameOver();
            // return final StepResult
            return new StepResult(true, fallingTetrimino, new ArrayList<>(stableBricksMap.values()));
        }
        // game is not over
        // execute action from user/operator - move tetrimino left/right
        if (canMove(direction)) {
            move(direction);
        }
        // move tetrimino down (every turn)
        if (canMoveDown()) {
            move(Direction.DOWN);
        } else {
            putDownTetrimino();
            // Remove completed lines and return number of them.
            int completedLines = RemoveCompletedLines();
            gameScore += completedLines;
        }
        return new StepResult(false, fallingTetrimino, new ArrayList<>(stableBricksMap.values()));
    }

    /**
     * Set environment to initial condition and return initial observation
     */
    public StepResult reset() {
        fillGameFieldWithValuesEmpty();
        addNewFallingTetriminoToGameField();
        stableBricksMap.clear();
        return new StepResult(false, fallingTetrimino, new ArrayList<>());
    }

    /**
     * Init {@code gameFieldArr} with {@code GAME_FIELD_EMPTY} values
     */
    private void fillGameFieldWithValuesEmpty() {
        for (int i = 0; i < gameFieldArr.length; i++) {
            for (int j = 0; j < gameFieldArr[0].length; j++) {
                gameFieldArr[i][j] = GAME_FIELD_EMPTY;
            }
        }
    }

    /**
     * Checks if any lines are completed. Clears completed lines, and moves all above pieces one line down
     *
     * @return number of cleared lines
     */
    private int RemoveCompletedLines() {
        // if sum of occupied fields in a line equals the line length, than the line is complete
        int clearedLines = 0;
        // loop through the game field array bottom up
        for (int lineNumber = gameFieldArr.length - 1; lineNumber >= 0; lineNumber--) {
            // sum stableBricks in a line
            int lineStableBricks = 0;
            for (int value : gameFieldArr[lineNumber]) {
                if (value == GAME_FIELD_BRICK_STABLE) {
                    lineStableBricks++;
                }
            }
            // check if line is complete
            if (lineStableBricks == GAME_FIELD_SIZE_X) {
                // line is complete
                clearedLines++;
                // clear bricks from stableBrickDict
                for (int x = 0; x < gameFieldArr[lineNumber].length; x++) {
                    Position pos = new Position(x, lineNumber);
                    stableBricksMap.remove(pos);
                }
                // move all the above lines down, by one row, starting from cleared index
                // remap positions in all above bricks
                for (int k = lineNumber; k > 0; k--) {
                    gameFieldArr[k] = gameFieldArr[k - 1];
                    for (int i = 0; i < gameFieldArr[k].length; i++) {
                        Position oldPos = new Position(i, k - 1);
                        Brick oldBrick = stableBricksMap.get(oldPos);
                        if (oldBrick != null) {
                            stableBricksMap.remove(oldPos);
                            Position newPos = new Position(i, k);
                            oldBrick.setPosition(newPos);
                            stableBricksMap.put(newPos, oldBrick);
                        }
                    }
                }
                // top line should be empty
                Arrays.fill(gameFieldArr[0], GAME_FIELD_EMPTY);
                // everything moved down, so we need to check same line again, increment lineNumber
                lineNumber++;
            }
        }
        return clearedLines;
    }

    /**
     * Checks if any stable brick touches top of the map
     *
     * @return true if it does, false otherwise
     */
    private boolean isGameOver() {
        for (var position : gameFieldArr[0]) {
            if (position == GAME_FIELD_BRICK_STABLE) {
                return true;
            }
        }
        return false;
    }

    private void handleGameOver() {
        totalGameScore += gameScore;
        gameScore = 0;
    }

    /**
     * Changes {@code fallingTetrimino} {@code gameFieldArr} states from falling to stable, moves its Bricks to the
     * {@code stableBricksDict} and adds new {@code fallingTetrimino} to the game field
     */
    private void putDownTetrimino() {
        for (var brick : fallingTetrimino.getBricks()) {
            Position position = brick.getPosition();
            stableBricksMap.put(position, brick);
            gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_STABLE;
        }

        addNewFallingTetriminoToGameField();
    }

    private void addNewFallingTetriminoToGameField() {
        fallingTetrimino = tetriminoBuilder.buildNewTetrimino();
        // add tetrimino bricks to game field array
        for (var brick : fallingTetrimino.getBricks()) {
            var position = brick.getPosition();
            // dont overwrite not empty values, changes result of isGameOver()
            if (gameFieldArr[position.getY()][position.getX()] == GAME_FIELD_EMPTY) {
                gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_FALLING;
            }
        }
    }

    /**
     * Moves down {@code fallingTetrimino}
     */
    private void move(Direction direction) {
        int addToX;
        int addToY;
        switch (direction) {
            case DOWN:
                addToX = 0;
                addToY = 1;
                break;
            case LEFT:
                addToX = -1;
                addToY = 0;
                break;
            case RIGHT:
                addToX = 1;
                addToY = 0;
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
            var new_y = y + addToY;
            var new_x = x + addToX;
            var newPosition = new Position(new_x, new_y);
            newPositions[i] = newPosition;
            // erase old brick from array
            gameFieldArr[y][x] = GAME_FIELD_EMPTY;
            // change position in brick object
            fallingTetrimino.getBricks()[i].setPosition(newPosition);
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
    private boolean canMove(Direction direction) {
        int addToX;
        switch (direction) {
            case LEFT:
                addToX = -1;
                break;
            case RIGHT:
                addToX = 1;
                break;
            default:
                return false;
        }
        for (var brick : fallingTetrimino.getBricks()) {
            var pos = brick.getPosition();
            var newX = pos.getX() + addToX;
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
