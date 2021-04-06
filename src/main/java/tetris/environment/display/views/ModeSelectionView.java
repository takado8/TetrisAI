package tetris.environment.display.views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import static tetris.environment.display.Constants.*;
import static tetris.environment.display.Constants.MODE_HUMAN_RADIO_BUTTON_LAYOUT_Y;

public interface ModeSelectionView {

    void aiModeSelected();
    void humanModeSelected();

    default void setupModeRadioButtons(ObservableList<Node> rootChildren){
        ToggleGroup modeGroup = new ToggleGroup();
        RadioButtonDisplay aiModeButton = new RadioButtonDisplay(modeGroup, MODE_AI_RADIO_BUTTON_TXT,
                MODE_AI_RADIO_BUTTON_LAYOUT_X, MODE_AI_RADIO_BUTTON_LAYOUT_Y, true);
        RadioButtonDisplay humanModeButton = new RadioButtonDisplay(modeGroup, MODE_HUMAN_RADIO_BUTTON_TXT,
                MODE_HUMAN_RADIO_BUTTON_LAYOUT_X, MODE_HUMAN_RADIO_BUTTON_LAYOUT_Y, false);

        rootChildren.add(aiModeButton);
        rootChildren.add(humanModeButton);
        modeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>()
        {
            public void changed(ObservableValue<? extends Toggle> ob, Toggle o, Toggle n)
            {
                RadioButtonDisplay radioButton = (RadioButtonDisplay)modeGroup.getSelectedToggle();
                if (radioButton != null) {
                    if (radioButton == aiModeButton){
                        aiModeSelected();
                    }
                    else if (radioButton == humanModeButton){
                        humanModeSelected();
                    }
                }
            }
        });
    }
}
