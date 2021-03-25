package tetris.environment.display;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tetris.environment.engine.Brick;

import static tetris.environment.Constants.DisplayConst.*;


/**
 * Makes displayable Rectangle from {@code Brick}
 */
public class BrickDisplay extends Rectangle {
    public final int id;

    public BrickDisplay(Brick brick) {
        super(translatePositionX(brick), translatePositionY(brick),
                BRICK_DISPLAY_SIZE_ADJUSTED, BRICK_DISPLAY_SIZE_ADJUSTED);
        setFill(Color.web(brick.color.getHexString()));
        setArcWidth(5);
        setArcHeight(5);
        setStrokeWidth(BRICK_STROKE_WIDTH);
        setStroke(Color.web(BRICK_STROKE_COLOR_HEX));
        this.id = brick.id;
    }

    /**
     * Translates game x position to actual graphic rendering position on game window.
     */
    private static double translatePositionX(Brick brick) {
        return brick.getPosition().getX() * BRICK_DISPLAY_SIZE_ACTUAL + GAME_FIELD_DISPLAY_MARGIN + BRICK_STROKE_WIDTH;
    }

    /**
     * Translates game y position to actual graphic rendering position on game window.
     */
    private static double translatePositionY(Brick brick) {
        return brick.getPosition().getY() * BRICK_DISPLAY_SIZE_ACTUAL + GAME_FIELD_DISPLAY_MARGIN;
    }

}
