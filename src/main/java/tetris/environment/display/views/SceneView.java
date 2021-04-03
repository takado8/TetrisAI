package tetris.environment.display.views;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import static tetris.environment.display.Constants.*;

public class SceneView extends Scene {
    public SceneView(Group root) {
        super(root, SCENE_SIZE_X, SCENE_SIZE_Y,
                Color.web(SCENE_BACKGROUND_COLOR_HEX));
    }
}
