package tetris.environment.display;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static tetris.environment.display.Constants.*;

public class Display extends Application {
    public static void main(String[] args) {
        System.out.println("Launching main.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setMaxHeight(SCENE_SIZE_Y + GAME_FIELD_DISPLAY_MARGIN * 1.5);
        primaryStage.setMaxWidth(SCENE_SIZE_X + GAME_FIELD_DISPLAY_MARGIN);
        primaryStage.setMinHeight(SCENE_SIZE_Y + GAME_FIELD_DISPLAY_MARGIN * 1.5);
        primaryStage.setMinWidth(SCENE_SIZE_X + GAME_FIELD_DISPLAY_MARGIN);

        primaryStage.setTitle("TetrisAI");
        Group root = new Group();

        Scene scene = new Scene(root, SCENE_SIZE_X, SCENE_SIZE_Y, Color.web(SCENE_BACKGROUND_COLOR));

        Rectangle gameField = new Rectangle(GAME_FIELD_DISPLAY_MARGIN, GAME_FIELD_DISPLAY_MARGIN,
                GAME_FIELD_DISPLAY_SIZE_X, GAME_FIELD_DISPLAY_SIZE_Y);
        gameField.setFill(Color.web(GAME_FIELD_BACKGROUND_COLOR));

        root.getChildren().add(gameField);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
