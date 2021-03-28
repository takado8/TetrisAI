package tetris.environment.engine;

import tetris.ai.Agent;
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
public class Engine {
    private final int[][] gameFieldArr;
    private final TetriminoBuilder tetriminoBuilder;
    private final Map<Position, Brick> stableBricksMap;

    private Tetrimino fallingTetrimino;
    private int gameScore = 0;
    private int totalGameScore = 0;
    private final boolean debuggingMode;

    public Engine() {
        System.out.println("\nInitializing game engine.");
        gameFieldArr = new int[GAME_FIELD_SIZE_Y][GAME_FIELD_SIZE_X];
        tetriminoBuilder = new TetriminoBuilder();
        stableBricksMap = new HashMap<>();
        this.debuggingMode = false;
        fillGameFieldWithValuesEmpty();
    }

    public Engine(boolean debuggingMode) {
        System.out.println("\nInitializing game engine in debugging mode. (live tests are active");
        this.debuggingMode = debuggingMode;
        gameFieldArr = new int[GAME_FIELD_SIZE_Y][GAME_FIELD_SIZE_X];
        tetriminoBuilder = new TetriminoBuilder();
        stableBricksMap = new HashMap<>();
        fillGameFieldWithValuesEmpty();
    }

    /**
     * Takes next step in environment
     *
     * @param action action to execute
     * @return StepResult
     */
    public StepResult step(Action action) {
        //region TESTING
        if (debuggingMode) {
//            printGameFieldArr();
            int numb_of_falling_bricks = 0;
            for (var row : gameFieldArr) {
                for (var field : row) {
                    if (field == GAME_FIELD_BRICK_FALLING) {
                        numb_of_falling_bricks++;
                    }
                }
            }
            if (numb_of_falling_bricks > 4) {
                throw new Error("Error in Engine.step(); number of falling bricks > 4.");
            }
        }
        //endregion
        // execute action from user/operator - rotate or move tetrimino left/right
        // allowed indefinitely during turn, user decides when to end turn by choosing action NEXT_TURN
        boolean tetriminoDropped = false;
        boolean isFinalStep = false;
        if (action == Action.ROTATE) {
            rotateFallingTetrimino();
        } else if (canMove(action)) {
            move(action);
        } else if (action == Action.END_TURN) {
            // move tetrimino down (at the end of the turn)
            if (canMoveDown()) {
                move(Action.MOVE_DOWN);
            } else {
                tetriminoDropped = true;
                putDownTetrimino();
                // Remove completed lines and return number of them.
                try {
                    int completedLines = RemoveCompletedLines();
                    gameScore += completedLines;
                } catch (Error e) {
                    System.out.println(e.getMessage());
                    System.exit(-1);
                }
                addNewFallingTetriminoToGameField();
            }
            // check if the game is over
            if (isGameOver()) {
                // game is over
                handleGameOver();
                // return final StepResult
                return new StepResult(true,tetriminoDropped,gameScore, fallingTetrimino, new ArrayList<>(stableBricksMap.values()));
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
        //region TESTING
        int nb_of_lines_by_simulation_function = getNumberOfCompleteLines();
        int nb0_of_empty_fields = 0;
        int nb0_of_stable_fields = 0;
        int nb0_of_moving_fields = 0;
        int nb0_of_stable_bricks = 0;
        if (debuggingMode) {
            int total_nb_of_fields = GAME_FIELD_SIZE_X * GAME_FIELD_SIZE_Y;
            nb0_of_stable_bricks = stableBricksMap.size();
            for (int y = 0; y < gameFieldArr.length; y++) {
                for (int x = 0; x < gameFieldArr[y].length; x++) {
                    if (gameFieldArr[y][x] == GAME_FIELD_BRICK_STABLE) {
                        nb0_of_stable_fields++;
                    }
                    if (gameFieldArr[y][x] == GAME_FIELD_BRICK_FALLING) {
                        nb0_of_moving_fields++;
                    }
                    if (gameFieldArr[y][x] == GAME_FIELD_EMPTY) {
                        nb0_of_empty_fields++;
                    }
                }
            }
//            printGameFieldArr();
//            System.out.println("nb0 of stable bricks: " + nb0_of_stable_bricks);
//            System.out.println("nb0 of stable fields: " + nb0_of_stable_fields);
//            assert nb0_of_stable_bricks == nb0_of_stable_fields : "Error 1 in Engine.RemoveCompletedLines; nb0_of_stable_bricks != nb0_of_stable_fields";
            assert nb0_of_moving_fields <= 4 : "Error 2 in Engine.RemoveCompletedLines; nb0_of_moving_fields > 4";
            assert nb0_of_moving_fields <= 0 : "Error 3 in Engine.RemoveCompletedLines; nb0_of_moving_fields > 0";
            assert nb0_of_empty_fields == total_nb_of_fields - nb0_of_stable_fields - nb0_of_moving_fields : "Error 4 in Engine.RemoveCompletedLines; nb0_of_empty_fields != total_nb_of_fields - nb0_of_stable_fields - nb0_of_moving_fields";
        }
        // END OF TESTING REGION
        //endregion
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
        //region TESTING
        if (debuggingMode) {
            int total_nb_of_fields = GAME_FIELD_SIZE_X * GAME_FIELD_SIZE_Y;
            int nb_of_cleared_stable_bricks = clearedLines * GAME_FIELD_SIZE_X;
            int nb1_of_empty_fields = 0;
            int nb1_of_stable_fields = 0;
            int nb1_of_moving_fields = 0;
            int nb1_of_stable_bricks = stableBricksMap.size();
            for (int y = 0; y < gameFieldArr.length; y++) {
                for (int x = 0; x < gameFieldArr[y].length; x++) {
                    if (gameFieldArr[y][x] == GAME_FIELD_BRICK_STABLE) {
                        nb1_of_stable_fields++;
                    }
                    if (gameFieldArr[y][x] == GAME_FIELD_BRICK_FALLING) {
                        nb1_of_moving_fields++;
                    }
                    if (gameFieldArr[y][x] == GAME_FIELD_EMPTY) {
                        nb1_of_empty_fields++;
                    }
                }
            }
//            assert nb1_of_stable_bricks == nb1_of_stable_fields : "Error 5 in Engine.RemoveCompletedLines; nb0_of_stable_bricks != nb0_of_stable_fields";
            assert nb1_of_moving_fields <= 4 : "Error 6 in Engine.RemoveCompletedLines; nb0_of_moving_fields > 4";
            assert nb1_of_empty_fields == total_nb_of_fields - nb1_of_stable_fields - nb1_of_moving_fields : "Error 7 in Engine.RemoveCompletedLines; nb0_of_empty_fields != total_nb_of_fields - nb0_of_stable_fields - nb0_of_moving_fields";
            assert nb0_of_empty_fields + nb_of_cleared_stable_bricks == nb1_of_empty_fields : "Error 8 in Engine.RemoveCompletedLines; nb0_of_empty_fields + nb_of_cleared_stable_bricks != nb1_of_empty_fields";
            assert nb0_of_stable_bricks - nb_of_cleared_stable_bricks == nb1_of_stable_bricks : "Error 9 in Engine.RemoveCompletedLines; nb0_of_stable_bricks - nb_of_cleared_stable_bricks != nb1_of_stable_bricks";
            assert nb0_of_stable_fields - nb_of_cleared_stable_bricks == nb1_of_stable_fields : "Error 10 in Engine.RemoveCompletedLines; nb0_of_stable_fields - nb_of_cleared_stable_bricks != nb1_of_stable_fields";
            assert nb1_of_moving_fields <= 0 : "Error 11 in Engine.RemoveCompletedLines; nb0_of_moving_fields > 0";
            assert clearedLines == nb_of_lines_by_simulation_function: "Error 12 in Engine.RemoveCompletedLines; clearedLines == nb_of_lines_by_simulation_function";
        }
        //endregion
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
//        if (debuggingMode){
//            System.out.println("moving :" + action);
//            printGameFieldArr();
//        }
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
            var x = position.getX();
            var y = position.getY();
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
    public void simulate(Agent aiAgent) {
        // map to store every move evaluations (Double) as keys and SimulationResult as value,
        // that contains moves needed to achieve particular state.
        Map<Double, SimulationResult> evaluationMap = new HashMap<>();

        // tetrimino is in its spawn position
        // move it one down so it could rotate
        int movesDownNeeded;
        switch (fallingTetrimino.getShape()) {
            case SHAPE_I:
                movesDownNeeded = 2;
                break;
            case SHAPE_O:
            case SHAPE_L:
            case SHAPE_J:
                movesDownNeeded = 0;
                break;
            case SHAPE_T:
            case SHAPE_S:
            case SHAPE_Z:
                movesDownNeeded = 1;
                break;
            default:
                movesDownNeeded = 0;
        }
        for (int i = 0; i < movesDownNeeded; i++) {
            if (canMoveDown()) {
                move(Action.MOVE_DOWN);
            }
        }
        // count how many rotations occurred
        int rotations = 0;
        // loop and evaluate every possible state for every orientation
        for (int k = 0; k < fallingTetrimino.getPossibleOrientationsNb(); k++) {
            // tetrimino ends simulation in position x = 0, we need to remember how many moves
            // right leeds to that state. same as with the orientation.
            int movesRight = 0;
            // move it max to left
            while (canMove(Action.MOVE_LEFT)) {
                move(Action.MOVE_LEFT);
            }
            boolean canMoveRight = true;
            while (canMoveRight) {
                // save starting position
                Brick[] bricks = fallingTetrimino.getBricks();
                Position[] bricksStartingPositions = new Position[4];
                for (int i = 0; i < 4; i++) {
                    bricksStartingPositions[i] = new Position(bricks[i].getPosition());
                }
                // move down
                while (canMoveDown()) {
                    move(Action.MOVE_DOWN);
                }
                // evaluate field
                double []fieldFeaturesValues = new double[4];
               fieldFeaturesValues[0] = getNumberOfCompleteLines();
               fieldFeaturesValues[1] = getNumberOfHoles();
               fieldFeaturesValues[2] = getColumnsSummedHeight();
               fieldFeaturesValues[3] = getColumnsSummedHeightDifference();
               double fieldEvaluation = aiAgent.evaluateMove(fieldFeaturesValues);
//               System.out.println("field eval here in assingment: " + fieldEvaluation);
//                for (int i = 0; i < 4; i++) {
////                    System.out.println("field feature " + i + ": " + fieldFeaturesValues[i]);
//                }
               SimulationResult simulationResult = new SimulationResult(movesRight, rotations);
               evaluationMap.put(fieldEvaluation, simulationResult);
                // bring it to starting position
                for (int i = 0; i < bricks.length; i++) {
                    Brick brick = bricks[i];
                    var brickPosition = brick.getPosition();
                    gameFieldArr[brickPosition.getY()][brickPosition.getX()] = GAME_FIELD_EMPTY;
                    gameFieldArr[bricksStartingPositions[i].getY()][bricksStartingPositions[i].getX()]
                            = GAME_FIELD_BRICK_FALLING;
                    brick.setPosition(bricksStartingPositions[i]);
                }
                // move one right
                if (canMove(Action.MOVE_RIGHT)) {
                    move(Action.MOVE_RIGHT);
                    movesRight++;
                } else {
                    canMoveRight = false;
                }
            }
            // next orientation
            int infiniteLoopExit = 0;
            while (!rotateFallingTetrimino() && infiniteLoopExit < 4) {
                infiniteLoopExit++;
                if (canMove(Action.MOVE_LEFT)) {
                    move(Action.MOVE_LEFT);
                }
            }
            rotations++;
        }
        // move it max to the left to bring it to state 0 from which we can get to
        // best state according to evaluationMap key values, by following actions from
        // SimulationResult in map values.
        while (canMove(Action.MOVE_LEFT)) {
            move(Action.MOVE_LEFT);
        }
        // get best result from evaluationMap
        double maxValue = -Double.MAX_VALUE;
        SimulationResult bestResult = null;//new SimulationResult(0,0);  // we want to crash if sth goes wrong and evaluationMap should never be empty
//        System.out.println("len of eval map: " + evaluationMap.size());
        for(var mapEntry : evaluationMap.entrySet()){
            double moveEvaluation = mapEntry.getKey();

//            System.out.println("still good eval: " + moveEvaluation);
            if (moveEvaluation > maxValue){
//                System.out.println("it is");
                maxValue = moveEvaluation;
                bestResult = mapEntry.getValue();
            }
        }
//        System.out.println("and the best result is: " + bestResult);

        // set tetrimino in wanted position
        // not every tetrimino can rotate near wall, so we move it two times right,
        // than rotate it and move it back to the left. This solution could be better, works for now.
        // so two fields to the right
        for (int i = 0; i < 2; i++) {
            if (canMove(Action.MOVE_RIGHT)){
                move(Action.MOVE_RIGHT);
            }
        }
        // rotations
        for (int i = 0; i < bestResult.getRotations(); i++) {
            rotateFallingTetrimino();
        }
        // go back left
        while (canMove(Action.MOVE_LEFT)) {
            move(Action.MOVE_LEFT);
        }
        // now go right.
        for (int i = 0; i < bestResult.getMovesRight(); i++) {
            if (canMove(Action.MOVE_RIGHT)){
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
//        System.out.println("number of completed: " + completedLines);
        return completedLines;
    }

    private int getNumberOfHoles() {
        boolean startCountingHoles = false;
        int holesNb = 0;
        for (int x = 0; x < GAME_FIELD_SIZE_X; x++) {
            for (int y = 0; y < GAME_FIELD_SIZE_Y; y++) {
                if (gameFieldArr[y][x] != GAME_FIELD_EMPTY){
                    startCountingHoles = true;
                }
                if (startCountingHoles && gameFieldArr[y][x] == GAME_FIELD_EMPTY){
                    holesNb++;
                }
            }
            startCountingHoles = false;
        }
        return holesNb;
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

    private int getColumnsSummedHeightDifference() {
        int summedDifference = 0;
        int prevSummedHeight = -1;
        for (int x = 0; x < GAME_FIELD_SIZE_X; x++) {
            for (int y = 0; y < GAME_FIELD_SIZE_Y; y++) {
                if (gameFieldArr[y][x] != GAME_FIELD_EMPTY || y == GAME_FIELD_SIZE_Y - 1) {
                    int summedHeight;
                    if (gameFieldArr[y][x] == 0) {
                        summedHeight = 0;
                    }
                    else {
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
