package tetris.environment.display;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tetris.ai.Agent;
import tetris.environment.Environment;
import tetris.environment.engine.*;
import javafx.animation.AnimationTimer;
import tetris.environment.engine.Action;
import tetris.environment.engine.tetrimino.Brick;

import java.util.ArrayList;
import java.util.List;

import static tetris.environment.display.Constants.*;


public class Display extends Application {
    private Group root;
    private Label scoreLabel;
    private double gameScore = 0.0;
    private final List<BrickDisplay> brickDisplayList = new ArrayList<>();

    private AnimationTimer gameLoop;
    private Long lastTurnUpdate = 0L;
    private Long lastResponseUpdate = 0L;
    private double timeDelay = DELAY_SECONDS_NORMAL;

    private Environment engine;
    private Action actionSelected = Action.NONE;
    private final boolean aiSimulation = true;
    private final double[] agentChromosome =
            {0.2882804024680882, -0.4941227037791766, -0.4138605915149467, -0.0647607979215204, 0.5306732616460637, -0.46437969634792153};
    private final Agent aiAgent = new Agent(agentChromosome);

    public static void main(String[] args) {
        System.out.println("Launching main.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // init game engine
        engine = new Engine(true);
        // init Display
        System.out.println("Display.start()");
        // make root
        root = new Group();
        // setup scene
        Scene scene = setupScene();
        primaryStage.setScene(scene);
        // run
        primaryStage.show();
        // set main window parameters (after show() to access actual window size)
        setWindowParameters(primaryStage);
    }

    void startGame() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                initTimers(now);
                if (isTimeToEndTurn(now)) {
                    // end turn, get result
                    engine.step(actionSelected);
                    StepResult stepResult = engine.step(Action.END_TURN);
                    if (stepResult.isFinalStep()) {
                        // stop the loop, display end label
                        handleFinalStep();
                        return;
                    }
                    if (stepResult.isTetriminoDropped()) {
                        // tetrimino dropped, so we need to simulate newly resp tetrimino
                        // to put it in correct position;
                        // simulate
                        if (aiSimulation) {
                            engine.simulate(aiAgent);
                        }
                        resetGameSpeed();
                        // update score
                        updateScoreLabel(stepResult);
                    }
                    updateDisplay(stepResult);
                    stopRotationAction();
                    lastTurnUpdate = now;
                }
                if (isTimeToRespond(now)) {
                    // make action selected by user
                    StepResult stepResult = engine.step(actionSelected);
                    stopRotationAction();
                    updateDisplay(stepResult);
                    lastResponseUpdate = now;
                }
            }
            private void updateScoreLabel(StepResult stepResult) {
                if (stepResult.getGameScore() != gameScore) {
                    gameScore = stepResult.getGameScore();
                    scoreLabel.setText("Score: " + (int) gameScore);
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

    private void resetGameSpeed() {
        if (timeDelay != DELAY_SECONDS_NORMAL) {
            timeDelay = DELAY_SECONDS_NORMAL;
        }
    }

    private void handleFinalStep() {
        gameLoop.stop();
        scoreLabel.setText(generateGameOverString());
    }

    private String generateGameOverString() {
        return "Score: " + gameScore;
    }

    private boolean isTimeToEndTurn(long now) {
        return now - lastTurnUpdate >= timeDelay * 1_000_000_000.0;
    }

    private boolean isTimeToRespond(long now) {
        return now - lastResponseUpdate >= RESPONSE_DELAY_SECONDS * 1_000_000_000.0;
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

    public Scene setupScene() {
        Scene scene = new Scene(root, SCENE_SIZE_X, SCENE_SIZE_Y, Color.web(SCENE_BACKGROUND_COLOR_HEX));
        var rootChildren = root.getChildren();
        // setup game field
        Rectangle gameField = new Rectangle(GAME_FIELD_DISPLAY_MARGIN, GAME_FIELD_DISPLAY_MARGIN,
                GAME_FIELD_DISPLAY_SIZE_X, GAME_FIELD_DISPLAY_SIZE_Y);
        gameField.setFill(Color.web(GAME_FIELD_BACKGROUND_COLOR_HEX));
        rootChildren.add(gameField);
        // setup right menu
        // Score label
        scoreLabel = new Label("Score: ");
        scoreLabel.setLayoutX(GAME_FIELD_DISPLAY_SIZE_X + 2 * GAME_FIELD_DISPLAY_MARGIN);
        scoreLabel.setLayoutY(GAME_FIELD_DISPLAY_MARGIN);
        scoreLabel.setTextFill(Color.WHITESMOKE);
        scoreLabel.setFont(Font.font("Trebuchet MS", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        rootChildren.add(scoreLabel);
        // Some labels for evolution (later)

        // buttons
        Button startButton = new Button("Start");
        startButton.setLayoutX(SCENE_SIZE_X - 150);
        startButton.setLayoutY(SCENE_SIZE_Y / 2);
        startButton.setDefaultButton(false);
        startButton.setFocusTraversable(false);
        startButton.setOnAction(this::startButtonClicked);
        rootChildren.add(startButton);
        // keyboard listener
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::keyboardListener);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, this::keyboardListenerRelease);
        return scene;
    }

    private void keyboardListenerRelease(Event e) {
        KeyCode keyCode = ((KeyEvent) e).getCode();
        if (keyCode == KeyCode.DOWN) {
            timeDelay = DELAY_SECONDS_NORMAL;
        }
        if (keyCode == KeyCode.LEFT || keyCode == KeyCode.RIGHT) {
            actionSelected = Action.NONE;
        }
    }

    private void keyboardListener(Event e) {
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
        StepResult stepResult = engine.reset();
        // reset game scores
        gameScore = stepResult.getGameScore();
        scoreLabel.setText("Score: " + gameScore);
        // if AI simulation mode is active, run simulation for first tetrimino
        if (aiSimulation) {
            engine.simulate(aiAgent);
        }
        updateDisplay(stepResult);
    }

    /**
     * Starts the game.
     */
    private void startButtonClicked(ActionEvent e) {
        // reset
        reset();
        // start game
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
