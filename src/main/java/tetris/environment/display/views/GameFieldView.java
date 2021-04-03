package tetris.environment.display.views;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


import static tetris.environment.display.Constants.*;

public class GameFieldView extends Rectangle {
    public GameFieldView() {
        super(GAME_FIELD_DISPLAY_MARGIN, GAME_FIELD_DISPLAY_MARGIN,
                GAME_FIELD_DISPLAY_SIZE_X, GAME_FIELD_DISPLAY_SIZE_Y);

        setFill(Color.web(GAME_FIELD_BACKGROUND_COLOR_HEX));
        setArcWidth(5);
        setArcHeight(5);
        setStrokeWidth(1);
        setStroke(Color.web(GAME_FIELD_STROKE_COLOR_HEX));
    }
}
