package tetris.environment.display;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import tetris.ai.Agent;
import tetris.environment.Environment;
import tetris.environment.display.views.*;
import tetris.environment.engine.Action;
import tetris.environment.engine.TetrisEngine;
import tetris.environment.engine.results.StepResult;
import tetris.environment.engine.tetrimino.Brick;

import java.util.ArrayList;
import java.util.List;

import static tetris.environment.display.Constants.*;


public class Control extends Application implements ModeSelectionView {
    // game engine
    private Environment gameEngine;
    // display objects
    private Group root;
    private Label scoreLabel;
    private double gameScore = 0.0;
    private final List<BrickDisplay> brickDisplayList = new ArrayList<>();
    // asynchronous loop to run the game
    private Action actionSelected = Action.NONE;
    private AnimationTimer gameLoop;
    private Long lastTurnUpdate = 0L;
    private Long lastResponseUpdate = 0L;
    private double timeDelayNormal;
    private double timeDelay;
    // ai
    private boolean aiMode = true;
    private final Agent aiAgent = new Agent(AGENT_CHROMOSOME);

    public Control() {
        if (aiMode) {
            timeDelayNormal = DELAY_SECONDS_AI;
        } else {
            timeDelayNormal = DELAY_SECONDS_NORMAL;
        }
        timeDelay = timeDelayNormal;
    }

    public static void main(String[] args) {
        System.out.println("Launching main.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        gameEngine = new TetrisEngine();
        root = new Group();
        Scene scene = setupScene(root);
        setupControls(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        // set main window parameters after show() to access actual window size
        setWindowParameters(primaryStage);
    }

    void startGame() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                initTimers(now);
                if (isTimeToEndTurn(now)) {
                    gameEngine.step(actionSelected);
                    StepResult stepResult = gameEngine.step(Action.END_TURN);
                    if (stepResult.isFinalStep()) {
                        handleFinalStep();
                        return;
                    }
                    if (stepResult.isTetriminoDropped()) {
                        if (aiMode) {
                            gameEngine.runFullSimulation(aiAgent);
                        }
                        resetGameSpeed();
                        updateScore(stepResult);
                        updateScoreLabel();
                    }
                    updateDisplay(stepResult);
                    stopRotationAction();
                    lastTurnUpdate = now;
                }
                if (isTimeToRespond(now)) {
                    StepResult stepResult = gameEngine.step(actionSelected);
                    stopRotationAction();
                    updateDisplay(stepResult);
                    lastResponseUpdate = now;
                }
            }
        };
        gameLoop.start();
    }

    private void stopRotationAction() {
        if (actionSelected == Action.ROTATE) {
            actionSelected = Action.NONE;
        }
    }

    private void updateScoreLabel(){
        scoreLabel.setText(SCORE_LABEL_TXT + (int) gameScore);
    }

    private void updateScore(StepResult stepResult) {
        if (stepResult.getGameScore() != gameScore) {
            gameScore = stepResult.getGameScore();
        }
    }

    private void resetGameSpeed() {
        if (timeDelay != timeDelayNormal) {
            timeDelay = timeDelayNormal;
        }
    }

    private void handleFinalStep() {
        gameLoop.stop();
        updateScoreLabel();
    }

    private boolean isTimeToEndTurn(long now) {
        return now - lastTurnUpdate >= timeDelay * NANOSECONDS_IN_SECOND;
    }

    private boolean isTimeToRespond(long now) {
        return now - lastResponseUpdate >= RESPONSE_DELAY_SECONDS * NANOSECONDS_IN_SECOND;
    }

    private void updateDisplay(StepResult stepResult) {
        // update position in all elements
        // for now just erase old and replace with new ones.
        for (BrickDisplay brick : brickDisplayList) {
            root.getChildren().remove(brick);
        }

        brickDisplayList.clear();
        for (Brick brick : stepResult.getBricks()) {
            BrickDisplay brickDisplay = new BrickDisplay(brick);
            brickDisplayList.add(brickDisplay);
            root.getChildren().add(brickDisplay);
        }
    }

    private SceneDisplay setupScene(Group root){
        SceneDisplay sceneDisplay = new SceneDisplay(root);
        addKeyboardListeners(sceneDisplay);
        return sceneDisplay;
    }

    public void setupControls(Group root) {
        var rootChildren = root.getChildren();
        // setup game field
        rootChildren.add(new GameFieldDisplay());
        // setup right menu
        scoreLabel = new LabelDisplay(SCORE_LABEL_TXT + "0", SCORE_LABEL_LAYOUT_X, SCORE_LABEL_LAYOUT_Y);
        rootChildren.add(scoreLabel);
        rootChildren.add(new LabelDisplay(MODE_LABEL_TXT, MODE_LABEL_LAYOUT_X, MODE_LABEL_LAYOUT_Y));
        setupModeRadioButtons(rootChildren);
        rootChildren.add(new StartButtonDisplay(this::startButtonClicked));
    }

    private void addKeyboardListeners(SceneDisplay scene){
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::keyboardListener);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, this::keyboardListenerRelease);
    }

    public void aiModeSelected () {
        timeDelayNormal = DELAY_SECONDS_AI;
        timeDelay = timeDelayNormal;
        aiMode = true;
    }
    public void humanModeSelected () {
        timeDelayNormal = DELAY_SECONDS_NORMAL;
        timeDelay = timeDelayNormal;
        aiMode = false;
    }

    public void keyboardListenerRelease(Event e) {
        KeyCode keyCode = ((KeyEvent) e).getCode();
        if (keyCode == KeyCode.DOWN) {
            timeDelay = timeDelayNormal;
        }
        if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT) {
            actionSelected = Action.NONE;
        }
    }

    public void keyboardListener(Event e) {
        KeyCode keyCode = ((KeyEvent) e).getCode();
        if (keyCode == KeyCode.LEFT) {
            actionSelected = Action.MOVE_LEFT;
        } else if (keyCode == KeyCode.RIGHT) {
            actionSelected = Action.MOVE_RIGHT;
        } else if (keyCode == KeyCode.UP) {
            actionSelected = Action.ROTATE;
        } else if (keyCode == KeyCode.DOWN) {
            timeDelay = DELAY_SECONDS_SPEEDUP;
        } else if (keyCode == KeyCode.SPACE) {
            timeDelay = DELAY_SECONDS_DROP;
        }
    }

    private void reset() {
        // clear root
        for (BrickDisplay brick : brickDisplayList) {
            root.getChildren().remove(brick);
        }
        // clear brick list
        brickDisplayList.clear();
        // reset timers
        lastTurnUpdate = 0L;
        lastResponseUpdate = 0L;
        // clear action
        actionSelected = Action.NONE;
        // set initial state of game environment and get first observation
        StepResult stepResult = gameEngine.reset();
        // reset game score
        updateScore(stepResult);
        updateScoreLabel();
        // if AI simulation mode is active, run simulation for first tetrimino
        if (aiMode) {
            gameEngine.runFullSimulation(aiAgent);
        }
        updateDisplay(stepResult);
    }

    /**
     * Starts the game.
     */
    private void startButtonClicked(ActionEvent e) {
        reset();
        startGame();
    }

    private void setWindowParameters(Stage primaryStage) {
        var height = primaryStage.getHeight();
        var width = primaryStage.getWidth();
        primaryStage.setMaxHeight(height);
        primaryStage.setMaxWidth(width);
        primaryStage.setMinHeight(height);
        primaryStage.setMinWidth(width);
        primaryStage.setTitle("TetrisAI");
    }

    void initTimers(long now) {
        if (lastTurnUpdate == 0L) {
            lastTurnUpdate = now;
        }
        if (lastResponseUpdate == 0L) {
            lastResponseUpdate = now;
        }
    }
}
