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
import tetris.environment.engine.*;
import javafx.animation.AnimationTimer;
import java.util.ArrayList;
import java.util.List;

import static tetris.environment.Constants.DisplayConst.*;

public class Display extends Application {
    private Group root;
    private Label scoreLabel;
    private final List<BrickDisplay> brickDisplayList = new ArrayList<>();

    private AnimationTimer gameLoop;
    private Long lastUpdate = 0L;
    private double timeDelay = DELAY_SECONDS_NORMAL;

    private Engine engine;
    private Direction directionSelected = Direction.NONE;

    public static void main(String[] args) {
        System.out.println("Launching main.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // init game engine
        engine = new Engine();
        // init Display
        System.out.println("Display.start()");
//        System.out.println(Constants.DisplayConst.toStringStatic());
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
                if (lastUpdate == 0L) {
                    lastUpdate = now;
                }
                if (now - lastUpdate >= timeDelay * 1_000_000_000.0) {
                    // make step with selected action and get result
                    StepResult stepResult = engine.step(directionSelected);
                    if (stepResult.isFinalStep) {
                        // reset environment
                        gameLoop.stop();
                        scoreLabel.setText("GAME OVER");
                        return;
                    }
                    directionSelected = Direction.NONE;
                    // update position in all elements
                    // for now just erase old and replace with new ones. ughh.
                    for (BrickDisplay brick : brickDisplayList) {
                        root.getChildren().remove(brick);
                    }
                    for (Brick brick : stepResult.bricks){
                        BrickDisplay brickDisplay = new BrickDisplay(brick);
                        brickDisplayList.add(brickDisplay);
                        root.getChildren().add(brickDisplay);
                    }
                    lastUpdate = now;
                }
            }
        };
        gameLoop.start();
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
        scoreLabel.setLayoutX(SCENE_SIZE_X - 150);
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

    private void keyboardListenerRelease(Event e){
        KeyCode keyCode = ((KeyEvent) e).getCode();
        if (keyCode == KeyCode.DOWN || keyCode == KeyCode.SPACE) {
            timeDelay = DELAY_SECONDS_NORMAL;
        }
    }

    private void keyboardListener(Event e) {
        KeyCode keyCode = ((KeyEvent) e).getCode();
        if (keyCode == KeyCode.LEFT) {
            directionSelected = Direction.LEFT;
        } else if (keyCode == KeyCode.RIGHT) {
            directionSelected = Direction.RIGHT;
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
        // reset timer
        lastUpdate = 0L;
        // clear action
        directionSelected = Direction.NONE;
        // set initial state of game environment and get first observation
        StepResult stepResult = engine.reset();
        // make BrickDisplays and add them to the list and to the root
        for(var brick : stepResult.bricks){
            BrickDisplay brickDisplay = new BrickDisplay(brick);
            brickDisplayList.add(brickDisplay);
            root.getChildren().add(brickDisplay);
        }
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

}
