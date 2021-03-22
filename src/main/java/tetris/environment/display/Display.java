package tetris.environment.display;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tetris.environment.Constants;
import tetris.environment.engine.Brick;
import tetris.environment.engine.Position;
import javafx.animation.AnimationTimer;
import tetris.environment.engine.Tetrimino;

import java.time.LocalTime;

import static tetris.environment.Constants.DisplayConst.*;

public class Display extends Application {
    private Stage primaryStage;
    private Scene scene;
    private Group root;
    private Label scoreLabel;
    private Rectangle brickRect = null;
    int brickY = 0;
    AnimationTimer gameLoop;
    Long lastUpdate = null;


    public static void main(String[] args) {
        System.out.println("Launching main.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Display.start");
        System.out.println(Constants.DisplayConst.toStringStatic());
        this.primaryStage = primaryStage;
        // set main window parameters
        setWindowParameters();
        // setup scene
        setupScene();
        // run
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    void startGame() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == null) {
                    lastUpdate = now;
                }
                if (now - lastUpdate >= DELAY_SECONDS * 1_000_000_000.0) {
                    if (brickRect != null) {
                        brickRect.setTranslateY(brickY += BRICK_DISPLAY_SIZE_ACTUAL);
                    }
                    lastUpdate = now ;
                }
            }
        };
        gameLoop.start();
    }

    public void setupScene() {
        root = new Group();
        scene = new Scene(root, SCENE_SIZE_X, SCENE_SIZE_Y, Color.web(SCENE_BACKGROUND_COLOR));
        var children = root.getChildren();
        // setup game field
        Rectangle gameField = new Rectangle(GAME_FIELD_DISPLAY_MARGIN, GAME_FIELD_DISPLAY_MARGIN,
                GAME_FIELD_DISPLAY_SIZE_X, GAME_FIELD_DISPLAY_SIZE_Y);
        gameField.setFill(Color.web(GAME_FIELD_BACKGROUND_COLOR));
        children.add(gameField);
        // setup right menu
        // Score label
        scoreLabel = new Label("Score: ");
        scoreLabel.setLayoutX(SCENE_SIZE_X - 150);
        scoreLabel.setLayoutY(GAME_FIELD_DISPLAY_MARGIN);
        scoreLabel.setTextFill(Color.WHITESMOKE);
        scoreLabel.setFont(Font.font("Trebuchet MS", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        children.add(scoreLabel);
        // Some labels for evolution (later)

        // buttons
        Button startButton = new Button("Start");
        startButton.setLayoutX(SCENE_SIZE_X - 150);
        startButton.setLayoutY(SCENE_SIZE_Y >> 1);    // what happens here? with the >> operator
        startButton.setOnAction(this::startButtonClicked);
        children.add(startButton);
    }

    public void startButtonClicked(ActionEvent e) {
        Brick brick = new Brick(new Position(0,0), tetris.environment.engine.Color.red);
        brickRect = BrickDisplay.getBrickDisplay(brick);
        root.getChildren().add(brickRect);
        startGame();
    }

    public void setWindowParameters() {
        primaryStage.setMaxHeight(SCENE_SIZE_Y + GAME_FIELD_DISPLAY_MARGIN * 1.5);
        primaryStage.setMaxWidth(SCENE_SIZE_X + GAME_FIELD_DISPLAY_MARGIN);
        primaryStage.setMinHeight(SCENE_SIZE_Y + GAME_FIELD_DISPLAY_MARGIN * 1.5);
        primaryStage.setMinWidth(SCENE_SIZE_X + GAME_FIELD_DISPLAY_MARGIN);
        primaryStage.setTitle("TetrisAI");
    }

}
