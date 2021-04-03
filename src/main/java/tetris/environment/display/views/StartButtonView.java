package tetris.environment.display.views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import static tetris.environment.display.Constants.SCENE_SIZE_X;
import static tetris.environment.display.Constants.SCENE_SIZE_Y;

public class StartButtonView extends Button {
    public StartButtonView(EventHandler<ActionEvent> eventHandler){
        super("Start");
        setLayoutX(SCENE_SIZE_X - 150);
        setLayoutY(SCENE_SIZE_Y / 2);
        setDefaultButton(false);
        setFocusTraversable(false);
        setOnAction(eventHandler);
    }
}
