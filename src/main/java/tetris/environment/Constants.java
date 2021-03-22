package tetris.environment;


public class Constants {
    /**
     * {@code Engine} constants
     */
    public static class EngineConst {
        public final static int GAME_FIELD_SIZE_X = 10;
        public final static int GAME_FIELD_SIZE_Y = 10;
        public final static int GAME_FIELD_EMPTY = 0;
        public final static int GAME_FIELD_BRICK_STATIC = 1;
        public final static int GAME_FIELD_BRICK_FALLING = 2;
    }

    /**
     * {@code Display} constants
     */
    public static class DisplayConst {
        public static final String SCENE_BACKGROUND_COLOR = "#222222";
        public static final String GAME_FIELD_BACKGROUND_COLOR = "#4e4e50";
        public static final String BRICK_STROKE_COLOR = "#ffffff";

        public static final int SCENE_SIZE_Y = 550;
        public static final int SCENE_SIZE_X = 500;
        public static final int GAME_FIELD_DISPLAY_SIZE_Y = 500;
        public static final int GAME_FIELD_DISPLAY_SIZE_X = 250;
        public static final int GAME_FIELD_DISPLAY_MARGIN = 20;
        public static final double BRICK_STROKE_WIDTH = 1;
        public static final double BRICK_DISPLAY_SIZE_ACTUAL = GAME_FIELD_DISPLAY_SIZE_X / EngineConst.GAME_FIELD_SIZE_X;
        public static final double BRICK_DISPLAY_SIZE_ADJUSTED = BRICK_DISPLAY_SIZE_ACTUAL - BRICK_STROKE_WIDTH;
        public static final double DELAY_SECONDS = 1;

    }

}
