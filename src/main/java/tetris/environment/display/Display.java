package tetris.environment.display;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Display extends Application {
    public static void main (String[] args) {
        System.out.println("Launching main.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 300, 300, Color.BLACK);

        Rectangle r = new Rectangle(25,25,250,250);
        r.setFill(Color.BLUE);

        root.getChildren().add(r);
        primaryStage.setTitle("BlackJack");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
