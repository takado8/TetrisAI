package tetris.environment.display.views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import static tetris.environment.display.Constants.*;

public class StartButtonView extends Button {
    private static final String IDLE_BUTTON_STYLE = "-fx-background-color:\n" +
            "            #090a0c,\n" +
            "            linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),\n" +
            "            linear-gradient(#20262b, #191d22),\n" +
            "            radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));\n" +
            "    -fx-background-radius: 5,4,3,5;\n" +
            "    -fx-background-insets: 0,1,2,0;\n" +
            "    -fx-text-fill: white-smoke;\n" +
            "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );\n" +
            "    -fx-font-family: \"Trebuchet MS\";\n" +
            "    -fx-text-fill: linear-gradient(white, #d0d0d0);\n" +
            "    -fx-font-size: 16px;\n" +
            "    -fx-padding: 10 20 10 20;" +
            "   -fx-cursor: hand;";
    private static final String HOVERED_BUTTON_STYLE = IDLE_BUTTON_STYLE + "-fx-opacity: 0.75";
    private static final String CLICKED_BUTTON_STYLE = IDLE_BUTTON_STYLE + "-fx-opacity: 0.5";

    public StartButtonView(EventHandler<ActionEvent> eventHandler) {
        super(BUTTON_START_TXT);
        setLayoutX(BUTTON_START_LAYOUT_X);
        setLayoutY(BUTTON_START_LAYOUT_Y);
        setDefaultButton(false);

        setMinWidth(100);
        setFocusTraversable(false);
        setOnAction(eventHandler);

        setStyle(IDLE_BUTTON_STYLE);
        setOnMouseEntered(e -> setStyle(HOVERED_BUTTON_STYLE));
        setOnMouseExited(e -> setStyle(IDLE_BUTTON_STYLE));
        setOnMousePressed(e -> setStyle(CLICKED_BUTTON_STYLE));
        setOnMouseReleased(e -> setStyle(IDLE_BUTTON_STYLE));
    }

}
