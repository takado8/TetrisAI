package tetris.environment.display.views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class RadioButtonView extends RadioButton {
    public RadioButtonView(ToggleGroup group, String text, double layoutX, double layoutY,
                           boolean isSelected) {
        super(text);
        setToggleGroup(group);
        setLayoutX(layoutX);
        setLayoutY(layoutY);
        setFont(Font.font("Trebuchet MS", FontWeight.NORMAL, FontPosture.REGULAR, 12));
        setTextFill(Color.WHITESMOKE);
        setSelected(isSelected);
    }
}
