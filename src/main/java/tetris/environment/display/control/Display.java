package tetris.environment.display.control;

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
import tetris.environment.display.models.BrickDisplay;
import tetris.environment.engine.*;
import javafx.animation.AnimationTimer;
import tetris.environment.engine.results.StepResult;
import tetris.environment.engine.tetrimino.Brick;

import java.util.ArrayList;
import java.util.List;

import static tetris.environment.display.Constants.*;


public class Display extends Application {
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
    private boolean aiSimulation = true;
    private final Agent aiAgent = new Agent(AGENT_CHROMOSOME);

    public Display() {
        if (aiSimulation) {
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
        // init game engine
        gameEngine = new Engine();
        // init Display
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
                    gameEngine.step(actionSelected);
                    StepResult stepResult = gameEngine.step(Action.END_TURN);
                    if (stepResult.isFinalStep()) {
                        handleFinalStep();
                        return;
                    }
                    if (stepResult.isTetriminoDropped()) {
                        // tetrimino dropped, so we need to simulate newly resp tetrimino
                        if (aiSimulation) {
                            gameEngine.runFullSimulation(aiAgent);
                        }
                        resetGameSpeed();
                        updateScoreLabel(stepResult);
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

    private void updateScoreLabel(StepResult stepResult) {
        if (stepResult.getGameScore() != gameScore) {
            gameScore = stepResult.getGameScore();
            scoreLabel.setText("Score: " + (int) gameScore);
        }
    }

    private void resetGameSpeed() {
        if (timeDelay != timeDelayNormal) {
            timeDelay = timeDelayNormal;
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

    Rectangle makeGameField() {
        Rectangle gameField = new Rectangle(GAME_FIELD_DISPLAY_MARGIN, GAME_FIELD_DISPLAY_MARGIN,
                GAME_FIELD_DISPLAY_SIZE_X, GAME_FIELD_DISPLAY_SIZE_Y);
        gameField.setFill(Color.web(GAME_FIELD_BACKGROUND_COLOR_HEX));
        gameField.setArcWidth(5);
        gameField.setArcHeight(5);
        gameField.setStrokeWidth(1);
        gameField.setStroke(Color.web(GAME_FIELD_STROKE_COLOR_HEX));
        return gameField;
    }

    private Label makeScoreLabel() {
        Label scoreLabel = new Label(SCORE_LABEL_TXT);
        scoreLabel.setLayoutX(GAME_FIELD_DISPLAY_SIZE_X + 2 * GAME_FIELD_DISPLAY_MARGIN);
        scoreLabel.setLayoutY(GAME_FIELD_DISPLAY_MARGIN);
        scoreLabel.setTextFill(Color.WHITESMOKE);
        scoreLabel.setFont(Font.font("Trebuchet MS", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        return scoreLabel;
    }

    public Scene setupScene() {
        Scene scene = new Scene(root, SCENE_SIZE_X, SCENE_SIZE_Y, Color.web(SCENE_BACKGROUND_COLOR_HEX));
        var rootChildren = root.getChildren();
        // setup game field
        rootChildren.add(makeGameField());
        // setup right menu
        scoreLabel = makeScoreLabel();
        rootChildren.add(scoreLabel);
        rootChildren.add(makeStartButton());
        // keyboard listener
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::keyboardListener);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, this::keyboardListenerRelease);
        return scene;
    }

    private void keyboardListenerRelease(Event e) {
        KeyCode keyCode = ((KeyEvent) e).getCode();
        if (keyCode == KeyCode.DOWN) {
            timeDelay = timeDelayNormal;
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
        StepResult stepResult = gameEngine.reset();
        // reset game score
        gameScore = stepResult.getGameScore();
        scoreLabel.setText("Score: " + gameScore);
        // if AI simulation mode is active, run simulation for first tetrimino
        if (aiSimulation) {
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

    private Button makeStartButton() {
        Button startButton = new Button("Start");
        startButton.setLayoutX(SCENE_SIZE_X - 150);
        startButton.setLayoutY(SCENE_SIZE_Y / 2);
        startButton.setDefaultButton(false);
        startButton.setFocusTraversable(false);
        startButton.setOnAction(this::startButtonClicked);
        return startButton;
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
