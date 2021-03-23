package tetris.environment;


public class Constants {
    /**
     * {@code Engine} constants
     */
    public static class EngineConst {
        public final static int GAME_FIELD_SIZE_X = 10;
        public final static int GAME_FIELD_SIZE_Y = 20;
        public final static int GAME_FIELD_EMPTY = 0;
        public final static int GAME_FIELD_BRICK_STABLE = 1;
        public final static int GAME_FIELD_BRICK_FALLING = 2;
    }

    /**
     * {@code Display} constants
     */
    public static class DisplayConst {
        public static final String SCENE_BACKGROUND_COLOR = "#222222";
        public static final String GAME_FIELD_BACKGROUND_COLOR = "#4e4e50";
        public static final String BRICK_STROKE_COLOR = "#000000";

        public static final double SCENE_SIZE_Y = 650;
        public static final double SCENE_SIZE_X = 550;
        public static final double GAME_FIELD_MARGIN_PERCENT = 0.09;  // %
        public static final double GAME_FIELD_DISPLAY_SIZE_Y = SCENE_SIZE_Y * (1 - GAME_FIELD_MARGIN_PERCENT);
        public static final double GAME_FIELD_DISPLAY_SIZE_X = GAME_FIELD_DISPLAY_SIZE_Y / 2;
        public static final double GAME_FIELD_DISPLAY_MARGIN = (SCENE_SIZE_Y - GAME_FIELD_DISPLAY_SIZE_Y) / 2;
        public static final double BRICK_STROKE_WIDTH = 0.5;
        public static final double BRICK_DISPLAY_SIZE_ACTUAL = GAME_FIELD_DISPLAY_SIZE_X / EngineConst.GAME_FIELD_SIZE_X;
        public static final double BRICK_DISPLAY_SIZE_ADJUSTED = BRICK_DISPLAY_SIZE_ACTUAL - BRICK_STROKE_WIDTH;
        public static final double DELAY_SECONDS = 0.3;

        public static String toStringStatic() {
            return "DisplayConst{" +
                    "SCENE_BACKGROUND_COLOR='" + SCENE_BACKGROUND_COLOR + '\'' +
                    ",\n GAME_FIELD_BACKGROUND_COLOR='" + GAME_FIELD_BACKGROUND_COLOR + '\'' +
                    ",\n BRICK_STROKE_COLOR='" + BRICK_STROKE_COLOR + '\'' +
                    ",\n SCENE_SIZE_Y=" + SCENE_SIZE_Y +
                    ",\n SCENE_SIZE_X=" + SCENE_SIZE_X +
                    ",\n GAME_FIELD_DISPLAY_SIZE_Y=" + GAME_FIELD_DISPLAY_SIZE_Y +
                    ",\n GAME_FIELD_DISPLAY_SIZE_X=" + GAME_FIELD_DISPLAY_SIZE_X +
                    ",\n GAME_FIELD_DISPLAY_MARGIN=" + GAME_FIELD_DISPLAY_MARGIN +
                    ",\n BRICK_STROKE_WIDTH=" + BRICK_STROKE_WIDTH +
                    ",\n BRICK_DISPLAY_SIZE_ACTUAL=" + BRICK_DISPLAY_SIZE_ACTUAL +
                    ",\n BRICK_DISPLAY_SIZE_ADJUSTED=" + BRICK_DISPLAY_SIZE_ADJUSTED +
                    ",\n DELAY_SECONDS=" + DELAY_SECONDS +
                    '}';
        }
    }

}
