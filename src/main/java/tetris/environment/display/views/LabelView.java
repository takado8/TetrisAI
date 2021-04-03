package tetris.environment.display.views;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;


public class LabelView extends Label {
    public LabelView(String text, double layoutX, double layoutY) {
        super(text);
        setLayoutX(layoutX);
        setLayoutY(layoutY);
        setTextFill(Color.WHITESMOKE);
        setFont(Font.font("Trebuchet MS", FontWeight.NORMAL, FontPosture.REGULAR, 20));
    }
}

