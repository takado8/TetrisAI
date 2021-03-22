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

        public static final int SCENE_SIZE_Y = 556;
        public static final int SCENE_SIZE_X = 500;
        public static final int GAME_FIELD_DISPLAY_SIZE_Y = (int)(SCENE_SIZE_Y * 0.90);
        public static final int GAME_FIELD_DISPLAY_SIZE_X = GAME_FIELD_DISPLAY_SIZE_Y / 2;
        public static final int GAME_FIELD_DISPLAY_MARGIN = 20;
        public static final double BRICK_STROKE_WIDTH = 1;
        public static final double BRICK_DISPLAY_SIZE_ACTUAL = (double) GAME_FIELD_DISPLAY_SIZE_X / EngineConst.GAME_FIELD_SIZE_X;
        public static final double BRICK_DISPLAY_SIZE_ADJUSTED = BRICK_DISPLAY_SIZE_ACTUAL - BRICK_STROKE_WIDTH * 2;
        public static final double DELAY_SECONDS = 1;

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
