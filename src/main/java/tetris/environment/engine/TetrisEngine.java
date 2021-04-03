package tetris.environment.engine;

import tetris.ai.Agent;
import tetris.environment.Environment;
import tetris.environment.engine.results.SimulationResult;
import tetris.environment.engine.results.StepResult;
import tetris.environment.engine.tetrimino.Brick;
import tetris.environment.engine.tetrimino.Tetrimino;
import tetris.environment.engine.tetrimino.TetriminoBuilder;
import tetris.environment.engine.tetrimino.features.Position;
import tetris.environment.engine.tetrimino.features.Shape;

import java.util.*;

import static tetris.environment.engine.Constants.*;

/**
 * Responsible for game logic - maintaining game field,
 * moving Tetrimino etc.
 */
public class TetrisEngine implements Environment {
    private final int[][] gameFieldArr = new int[GAME_FIELD_SIZE_Y][GAME_FIELD_SIZE_X];
    private final TetriminoBuilder tetriminoBuilder = new TetriminoBuilder();
    private final Map<Position, Brick> stableBricksMap = new HashMap<>();

    private Tetrimino fallingTetrimino;
    private int gameScore = 0;

    public TetrisEngine() {
        System.out.println("\nInitializing game engine.");
        fillGameFieldWithValuesEmpty();
    }


    /**
     * Executes action from user/operator.
     * Takes next step in environment.
     * Allowed indefinitely during turn, user decides when to end turn by choosing action NEXT_TURN
     * @param action action to execute
     * @return StepResult
     */
    public StepResult step(Action action) {
        boolean tetriminoDropped = false;
        if (action == Action.ROTATE) {
            rotateFallingTetrimino();
        } else if (canMove(action)) {
            move(action);
        } else if (action == Action.END_TURN) {
            if (canMoveDown()) {
                move(Action.MOVE_DOWN);
            } else {
                tetriminoDropped = true;
                putDownTetrimino();
                int completedLines = RemoveCompletedLines();
                gameScore += completedLines;
                addNewFallingTetriminoToGameField();
            }
            if (isGameOver()) {
                handleGameOver();
                return new StepResult(true, tetriminoDropped, gameScore, fallingTetrimino, new ArrayList<>(stableBricksMap.values()));
            }
        }
        return new StepResult(false, tetriminoDropped, gameScore, fallingTetrimino, new ArrayList<>(stableBricksMap.values()));
    }

    /**
     * Set environment to initial condition and return initial observation
     */
    public StepResult reset() {
        gameScore = 0;
        fillGameFieldWithValuesEmpty();
        addNewFallingTetriminoToGameField();
        stableBricksMap.clear();
        return new StepResult(false, 0, fallingTetrimino, new ArrayList<>());
    }

    public void runFullSimulation(Agent agent) {
        var best = simulate(agent);
        setTetriminoInSimulationResultPosition(best);
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
        for (int y = gameFieldArr.length - 1; y >= 0; y--) {
            // sum stableBricks in a line
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
                    stableBricksMap.remove(pos);
                    gameFieldArr[y][x] = GAME_FIELD_EMPTY;
                }
                // move all the above lines down, by one row, starting from cleared index
                // remap positions in all above bricks
                for (int k = y; k > 0; k--) {
                    for (int x = 0; x < gameFieldArr[k].length; x++) {
                        gameFieldArr[k][x] = gameFieldArr[k - 1][x];
                        Position oldPos = new Position(x, k - 1);
                        Brick oldBrick = stableBricksMap.get(oldPos);
                        if (oldBrick != null) {
                            stableBricksMap.remove(oldPos);
                            Position newPos = new Position(x, k);
                            oldBrick.setPosition(newPos);
                            stableBricksMap.put(newPos, oldBrick);
                        }
                    }
                }
                // top line should be empty
                Arrays.fill(gameFieldArr[0], GAME_FIELD_EMPTY);
                // everything moved down, so we need to check same line again, increment y
                y++;
            }
        }
        return clearedLines;
    }

    /**
     * Checks if any stable brick is located in top line of the game field.
     *
     * @return true if it is, false otherwise
     */
    private boolean isGameOver() {
        for (var position : gameFieldArr[0]) {
            if (position == GAME_FIELD_BRICK_STABLE) {
                return true;
            }
        }
        return false;
    }

    /**
     * Handles game over
     */
    private void handleGameOver() {

    }

    /**
     * Changes {@code fallingTetrimino} {@code gameFieldArr} states from falling to stable, moves its Bricks to the
     * {@code stableBricksDict}
     */
    private void putDownTetrimino() {
        for (var brick : fallingTetrimino.getBricks()) {
            Position position = brick.getPosition();
            stableBricksMap.put(position, brick);
            gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_STABLE;
        }
    }

    private void reversePutDownTetrimino(Tetrimino tetrimino) {
        for (var brick : tetrimino.getBricks()) {
            Position position = brick.getPosition();
            stableBricksMap.remove(position);
            gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_FALLING;
        }
    }

    private void addNewFallingTetriminoToGameField() {
        fallingTetrimino = tetriminoBuilder.buildNewTetrimino();
        // add tetrimino bricks to the game field array
        for (var brick : fallingTetrimino.getBricks()) {
            var position = brick.getPosition();
            // dont overwrite stable values
            if (gameFieldArr[position.getY()][position.getX()] != GAME_FIELD_BRICK_STABLE) {
                gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_FALLING;
            }
        }
    }

    private void addNewFallingTetriminoToGameField(Tetrimino newFallingTetrimino) {
        fallingTetrimino = newFallingTetrimino;
        // add tetrimino bricks to the game field array
        for (var brick : fallingTetrimino.getBricks()) {
            var position = brick.getPosition();
            // dont overwrite stable values
            if (gameFieldArr[position.getY()][position.getX()] != GAME_FIELD_BRICK_STABLE) {
                gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_BRICK_FALLING;
            }
        }
    }

    private void reverseAddNewFallingTetriminoToGameField(Tetrimino currentTetrimino, Tetrimino oldTetrimino) {
        // remove current tetrimino bricks from the game field array
        for (var brick : currentTetrimino.getBricks()) {
            var position = brick.getPosition();
            // dont overwrite stable values
            if (gameFieldArr[position.getY()][position.getX()] != GAME_FIELD_BRICK_STABLE) {
                gameFieldArr[position.getY()][position.getX()] = GAME_FIELD_EMPTY;
            }
        }
        fallingTetrimino = oldTetrimino;
    }

    /**
     * Moves down {@code fallingTetrimino}
     */
    private void move(Action action) {
        int addToX;
        int addToY;
        switch (action) {
            case MOVE_DOWN:
                addToX = 0;
                addToY = 1;
                break;
            case MOVE_LEFT:
                addToX = -1;
                addToY = 0;
                break;
            case MOVE_RIGHT:
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
    private boolean canMove(Action action) {
        int addToX;
        switch (action) {
            case MOVE_LEFT:
                addToX = -1;
                break;
            case MOVE_RIGHT:
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

    /**
     * Rotates fallingTetrimino clockwise
     */
    private boolean rotateFallingTetrimino() {
        if (fallingTetrimino.getShape() == Shape.SHAPE_O) {
            return true;  // O is symmetric in both dimensions, rotation has no effect
        }
        Brick[] bricks = fallingTetrimino.getBricks();
        Position centerOfRotation = bricks[1].getPosition();

        Position[] newPositions = new Position[4];
        for (int i = 0; i < bricks.length; i++) {
            newPositions[i] = bricks[i].getPosition().rotate(centerOfRotation);
        }
        // check if fields are allowed
        if (isFieldAllowedToMove(newPositions)) {
            for (int i = 0; i < bricks.length; i++) {
                Brick brick = bricks[i];
                var oldPosition = brick.getPosition();
                gameFieldArr[oldPosition.getY()][oldPosition.getX()] = GAME_FIELD_EMPTY;
                brick.setPosition(newPositions[i]);
                gameFieldArr[newPositions[i].getY()][newPositions[i].getX()] = GAME_FIELD_BRICK_FALLING;
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if field on {@code gameFieldArr} is unoccupied and within the array boundaries.
     *
     * @param position {@code Position} requested to test.
     * @return true if field is allowed to move and false otherwise.
     */
    private boolean isFieldAllowedToMove(Position position) {
        var x = position.getX();
        var y = position.getY();
        return x < GAME_FIELD_SIZE_X && x >= 0 && y < GAME_FIELD_SIZE_Y && y >= 0
                && gameFieldArr[y][x] != GAME_FIELD_BRICK_STABLE;
    }

    private boolean isFieldAllowedToMove(Position[] positions) {
        for (var position : positions) {
            if (!isFieldAllowedToMove(position)) {
                return false;
            }
        }
        return true;
    }

    /**
     * For AI purpose. simulates and evaluates by the AI every possible move,
     * maps them and chooses best one, than sets falling tetrimino in correct position
     * to end up in wanted state by falling down until end of the turn.
     */
    private SimulationResult simulate(Agent aiAgent) {
        // init map to store every move evaluation (Double) as key and SimulationResult as value,
        // that contains moves needed to achieve particular state.
        double bestEval = -Double.MAX_VALUE;
        int bestMovesRight = 0;
        int bestRotations = 0;
        // tetrimino is in its spawn position
        moveTetriminoDownSoItCouldRotate();
        // count how many rotations occurred
        int rotations = 0;
        // loop and evaluate every possible state for every orientation
        for (int k = 0; k < fallingTetrimino.getPossibleOrientationsNb(); k++) {
            // tetrimino starts and ends simulation in position x = 0, we need to remember how many moves
            // right leeds to which state. same as with the orientation.
            int movesRight = 0;
            moveMaxLeft();
            boolean canMoveRight = true;
            while (canMoveRight) {
                // save bricks and starting positions
                Brick[] bricks = fallingTetrimino.getBricks();
                Position[] bricksStartingPositions = fallingTetrimino.getPositions();
                moveMaxDown();
                // deep eval
                double deepFieldEvaluation = simulateNextStep(aiAgent);
                if (deepFieldEvaluation > bestEval) {
                    bestEval = deepFieldEvaluation;
                    bestMovesRight = movesRight;
                    bestRotations = rotations;
                }

                bringToPositions(bricks, bricksStartingPositions);

                // move one right
                if (canMove(Action.MOVE_RIGHT)) {
                    move(Action.MOVE_RIGHT);
                    movesRight++;
                } else {
                    canMoveRight = false;
                }
            }
            rotateSimulatedTetrimino();
            rotations++;
        }
        // move it max to the left to bring it to state 0 from which we can get to
        // best state according to evaluationMap key values, by following actions from
        // SimulationResult in map values.
        moveMaxLeft();
        return new SimulationResult(bestMovesRight, bestRotations);
    }

    private double simulateNextStep(Agent aiAgent) {
        Tetrimino savedFallingTetrimino = new Tetrimino(fallingTetrimino);
        putDownTetrimino();
        // add new falling tetrimino and save it
        Shape nextShape = tetriminoBuilder.getNextShape();
        Tetrimino newTetrimino = tetriminoBuilder.buildNewTetrimino(nextShape);
        addNewFallingTetriminoToGameField(newTetrimino);
        // simulate
        moveTetriminoDownSoItCouldRotate();
        double bestEval = -Double.MAX_VALUE;
        for (int k = 0; k < fallingTetrimino.getPossibleOrientationsNb(); k++) {
            moveMaxLeft();
            boolean canMoveRight = true;
            while (canMoveRight) {
                // save bricks and starting positions
                Brick[] bricks = fallingTetrimino.getBricks();
                Position[] bricksStartingPositions = fallingTetrimino.getPositions();
                moveMaxDown();

                double fieldEvaluation = aiAgent.evaluateMove(getFieldFeaturesArray());

                if (fieldEvaluation > bestEval) {
                    bestEval = fieldEvaluation;
                }
                bringToPositions(bricks, bricksStartingPositions);

                if (canMove(Action.MOVE_RIGHT)) {
                    move(Action.MOVE_RIGHT);
                } else {
                    canMoveRight = false;
                }
            }
            rotateSimulatedTetrimino();
        }
        reverseAddNewFallingTetriminoToGameField(fallingTetrimino, savedFallingTetrimino);

        reversePutDownTetrimino(savedFallingTetrimino);

        return bestEval;
    }

    private void rotateSimulatedTetrimino() {
        int infiniteLoopExit = 0;
        while (!rotateFallingTetrimino() && infiniteLoopExit < 4) {
            infiniteLoopExit++;
            if (canMove(Action.MOVE_LEFT)) {
                move(Action.MOVE_LEFT);
            }
        }
    }

    private void bringToPositions(Brick[] bricks, Position[] bricksNewPositions) {
        for (int i = 0; i < bricks.length; i++) {
            Brick brick = bricks[i];
            var brickPosition = brick.getPosition();
            gameFieldArr[brickPosition.getY()][brickPosition.getX()] = GAME_FIELD_EMPTY;
            gameFieldArr[bricksNewPositions[i].getY()][bricksNewPositions[i].getX()]
                    = GAME_FIELD_BRICK_FALLING;
            brick.setPosition(bricksNewPositions[i]);
        }
    }

    private void moveTetriminoDownSoItCouldRotate() {
        int movesDownNeeded;
        switch (fallingTetrimino.getShape()) {
            case SHAPE_I:
                movesDownNeeded = 2;
                break;
            case SHAPE_T:
            case SHAPE_S:
            case SHAPE_Z:
                movesDownNeeded = 1;
                break;
            case SHAPE_O:
            case SHAPE_L:
            case SHAPE_J:
            default:
                movesDownNeeded = 0;
        }
        for (int i = 0; i < movesDownNeeded; i++) {
            if (canMoveDown()) {
                move(Action.MOVE_DOWN);
            }
        }
    }

    private void moveMaxDown() {
        while (canMoveDown()) {
            move(Action.MOVE_DOWN);
        }
    }

    private double[] getFieldFeaturesArray() {
        double[] fieldFeaturesValues = new double[NUMBER_OF_GAME_FIELD_EVALUATION_FEATURES];
        fieldFeaturesValues[0] = normalizeNumberOfLines(getNumberOfCompleteLines());
        fieldFeaturesValues[1] = normalizeNumberOfHoles(getNumberOfHoles());
        fieldFeaturesValues[2] = normalizeColumnsSummedHeight(getColumnsSummedHeight());
        fieldFeaturesValues[3] = normalizeColumnsSummedHeightDiff(getColumnsSummedHeightDifference());
        return fieldFeaturesValues;
    }

    private void moveMaxLeft() {
        while (canMove(Action.MOVE_LEFT)) {
            move(Action.MOVE_LEFT);
        }
    }

    public void setTetriminoInSimulationResultPosition(SimulationResult simulationResult) {
        // set tetrimino in wanted position
        // not every tetrimino can rotate near wall, so we move it two times right,
        // than rotate it and move it back to the left. This solution could be better, works for now.
        // so two fields to the right
        for (int i = 0; i < 2; i++) {
            if (canMove(Action.MOVE_RIGHT)) {
                move(Action.MOVE_RIGHT);
            }
        }
        // rotations
        for (int i = 0; i < simulationResult.getRotations(); i++) {
            rotateFallingTetrimino();
        }
        // go back left
        while (canMove(Action.MOVE_LEFT)) {
            move(Action.MOVE_LEFT);
        }
        // now go right.
        for (int i = 0; i < simulationResult.getMovesRight(); i++) {
            if (canMove(Action.MOVE_RIGHT)) {
                move(Action.MOVE_RIGHT);
            }
        }
        // end of simulation. Tetrimino now is in position which AI predicts as best,
        // can now fall down until next tetrimino spawns and simulation runs again.
    }

    private int getNumberOfCompleteLines() {
        int completedLines = 0;
        for (var line : gameFieldArr) {
            int stableStates = 0;
            for (var value : line) {
                if (value == GAME_FIELD_BRICK_STABLE) {
                    stableStates++;
                }
            }
            if (stableStates == GAME_FIELD_SIZE_X) {
                completedLines++;
            }
        }
        return completedLines;
    }

    private double normalizeNumberOfLines(int linesNumber) {
        // max possible number of lines cleared in one move is 4
        // normalize to range <0;1>
        return ((double) linesNumber) / 4;
    }

    private int getNumberOfHoles() {
        boolean startCountingHoles = false;
        int holesNb = 0;
        for (int x = 0; x < GAME_FIELD_SIZE_X; x++) {
            for (int y = 0; y < GAME_FIELD_SIZE_Y; y++) {
                if (gameFieldArr[y][x] != GAME_FIELD_EMPTY) {
                    startCountingHoles = true;
                }
                if (startCountingHoles && gameFieldArr[y][x] == GAME_FIELD_EMPTY) {
                    holesNb++;
                }
            }
            startCountingHoles = false;
        }
        return holesNb;
    }

    private double normalizeNumberOfHoles(int holesNumber) {
        // max possible number of holes almost equals the surface of the game field,
        // but in practice it wont be nearly as big. 20 is guessed average max
        // holes number in game field.
        // normalize to range <0; ~1?>
        return ((double) holesNumber) / 20;
    }

    private int getColumnsSummedHeight() {
        int summedHeight = 0;
        for (int x = 0; x < GAME_FIELD_SIZE_X; x++) {
            for (int y = 0; y < GAME_FIELD_SIZE_Y; y++) {
                if (gameFieldArr[y][x] != GAME_FIELD_EMPTY) {
                    summedHeight += GAME_FIELD_SIZE_Y - y;
                    break;
                }
            }
        }
        return summedHeight;
    }

    private double normalizeColumnsSummedHeight(int columnsHeight) {
        // max possible number of summed columns is equal to game field plane.
        // normalize to range <0;1>
        return ((double) columnsHeight) / (GAME_FIELD_SIZE_Y * GAME_FIELD_SIZE_X);
    }

    private int getColumnsSummedHeightDifference() {
        int summedDifference = 0;
        int prevSummedHeight = -1;
        for (int x = 0; x < GAME_FIELD_SIZE_X; x++) {
            for (int y = 0; y < GAME_FIELD_SIZE_Y; y++) {
                if (gameFieldArr[y][x] != GAME_FIELD_EMPTY || y == GAME_FIELD_SIZE_Y - 1) {
                    int summedHeight;
                    if (gameFieldArr[y][x] == 0) {
                        summedHeight = 0;
                    } else {
                        summedHeight = GAME_FIELD_SIZE_Y - y;
                    }
                    if (prevSummedHeight >= 0) {
                        summedDifference += Math.abs(summedHeight - prevSummedHeight);
                    }
                    prevSummedHeight = summedHeight;
                    break;
                }
            }
        }
        return summedDifference;
    }

    private double normalizeColumnsSummedHeightDiff(int columnsHeightDiff) {
        // max possible number of summed columns difference is almost equal to game field plane,
        // but should appear rarely, so we take half of that.
        // normalize to range <0;~1>
        return ((double) columnsHeightDiff) / (((double) GAME_FIELD_SIZE_Y * (double) GAME_FIELD_SIZE_X) / 2);
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
