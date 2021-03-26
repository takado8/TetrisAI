package tetris.environment.display;


import static tetris.environment.engine.Constants.GAME_FIELD_SIZE_X;

/**
 * {@code Display} constants
 */
public class Constants {
    public static final String SCENE_BACKGROUND_COLOR_HEX = "#222222";
    public static final String GAME_FIELD_BACKGROUND_COLOR_HEX = "#4e4e50";
    public static final String BRICK_STROKE_COLOR_HEX = "#000000";

    public static final double SCENE_SIZE_Y = 650;
    public static final double SCENE_SIZE_X = 550;
    public static final double GAME_FIELD_MARGIN_PERCENT = 0.09;  // %
    public static final double GAME_FIELD_DISPLAY_SIZE_Y = SCENE_SIZE_Y * (1 - GAME_FIELD_MARGIN_PERCENT);
    public static final double GAME_FIELD_DISPLAY_SIZE_X = GAME_FIELD_DISPLAY_SIZE_Y / 2;
    public static final double GAME_FIELD_DISPLAY_MARGIN = (SCENE_SIZE_Y - GAME_FIELD_DISPLAY_SIZE_Y) / 2;
    public static final double BRICK_STROKE_WIDTH = 0.5;
    public static final double BRICK_DISPLAY_SIZE_ACTUAL = GAME_FIELD_DISPLAY_SIZE_X / GAME_FIELD_SIZE_X;
    public static final double BRICK_DISPLAY_SIZE_ADJUSTED = BRICK_DISPLAY_SIZE_ACTUAL - BRICK_STROKE_WIDTH;
    public static final double DELAY_SECONDS_SLOW = 0.75;
    public static final double DELAY_SECONDS_NORMAL = 0.5;
    public static final double DELAY_SECONDS_FAST = 0.3;
    public static final double DELAY_SECONDS_SPEEDUP = 0.04;
    public static final double DELAY_SECONDS_DROP = 0.009;
    public static final double RESPONSE_DELAY_SECONDS = 0.1;


    public static String toStringStatic() {
        return "DisplayConst{" +
                "SCENE_BACKGROUND_COLOR='" + SCENE_BACKGROUND_COLOR_HEX + '\'' +
                ",\n GAME_FIELD_BACKGROUND_COLOR='" + GAME_FIELD_BACKGROUND_COLOR_HEX + '\'' +
                ",\n BRICK_STROKE_COLOR='" + BRICK_STROKE_COLOR_HEX + '\'' +
                ",\n SCENE_SIZE_Y=" + SCENE_SIZE_Y +
                ",\n SCENE_SIZE_X=" + SCENE_SIZE_X +
                ",\n GAME_FIELD_DISPLAY_SIZE_Y=" + GAME_FIELD_DISPLAY_SIZE_Y +
                ",\n GAME_FIELD_DISPLAY_SIZE_X=" + GAME_FIELD_DISPLAY_SIZE_X +
                ",\n GAME_FIELD_DISPLAY_MARGIN=" + GAME_FIELD_DISPLAY_MARGIN +
                ",\n BRICK_STROKE_WIDTH=" + BRICK_STROKE_WIDTH +
                ",\n BRICK_DISPLAY_SIZE_ACTUAL=" + BRICK_DISPLAY_SIZE_ACTUAL +
                ",\n BRICK_DISPLAY_SIZE_ADJUSTED=" + BRICK_DISPLAY_SIZE_ADJUSTED +
                ",\n DELAY_SECONDS=" + DELAY_SECONDS_NORMAL +
                '}';
    }
}


