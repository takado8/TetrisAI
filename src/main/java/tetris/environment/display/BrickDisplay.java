package tetris.environment.display;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tetris.environment.engine.Brick;

import static tetris.environment.Constants.DisplayConst.*;


/**
 * Makes displayable Rectangle from {@code Brick}
 */
public class BrickDisplay {
    static Rectangle getBrickDisplay(Brick brick) {
        System.out.println(brick.getPosition());
        double x = brick.getPosition().getX() * BRICK_DISPLAY_SIZE_ADJUSTED + GAME_FIELD_DISPLAY_MARGIN + BRICK_STROKE_WIDTH/2;
        double y = brick.getPosition().getY() * BRICK_DISPLAY_SIZE_ADJUSTED + GAME_FIELD_DISPLAY_MARGIN + BRICK_STROKE_WIDTH/2;
        Rectangle brickDisplay = new Rectangle(x, y, BRICK_DISPLAY_SIZE_ADJUSTED, BRICK_DISPLAY_SIZE_ADJUSTED);
        brickDisplay.setFill(Color.INDIANRED);
        brickDisplay.setArcWidth(5);
        brickDisplay.setArcHeight(5);
        brickDisplay.setStrokeWidth(BRICK_STROKE_WIDTH);
        brickDisplay.setStroke(Color.web(BRICK_STROKE_COLOR));

        return brickDisplay;
    }
}
