package tetris.environment.display;


import static tetris.environment.engine.Constants.GAME_FIELD_SIZE_X;

/**
 * {@code Display} constants
 */
public class Constants {
    public static final String SCENE_BACKGROUND_COLOR_HEX = "#222222";
    public static final String GAME_FIELD_BACKGROUND_COLOR_HEX = "#4e4e50";
    public static final String BRICK_STROKE_COLOR_HEX = "#000000";
    public static final String GAME_FIELD_STROKE_COLOR_HEX = "#666666";

    public static final double SCENE_SIZE_Y = 650;
    public static final double SCENE_SIZE_X = 485;
    public static final double GAME_FIELD_MARGIN_PERCENT = 0.09;
    public static final double GAME_FIELD_DISPLAY_SIZE_Y = SCENE_SIZE_Y * (1 - GAME_FIELD_MARGIN_PERCENT);
    public static final double GAME_FIELD_DISPLAY_SIZE_X = GAME_FIELD_DISPLAY_SIZE_Y / 2;
    public static final double GAME_FIELD_DISPLAY_MARGIN = (SCENE_SIZE_Y - GAME_FIELD_DISPLAY_SIZE_Y) / 2;
    public static final double BRICK_STROKE_WIDTH = 0.5;
    public static final double BRICK_DISPLAY_SIZE_ACTUAL = GAME_FIELD_DISPLAY_SIZE_X / GAME_FIELD_SIZE_X;
    public static final double BRICK_DISPLAY_SIZE_ADJUSTED = BRICK_DISPLAY_SIZE_ACTUAL - BRICK_STROKE_WIDTH;
    public static final double DELAY_SECONDS_NORMAL = 0.5;
    public static final double DELAY_SECONDS_AI = 0.001;
    public static final double DELAY_SECONDS_SPEEDUP = 0.04;
    public static final double DELAY_SECONDS_DROP = 0.009;
    public static final double RESPONSE_DELAY_SECONDS = 0.15;
    public static final double NANOSECONDS_IN_SECOND = 1_000_000_000.0;
    public static final double ARC_HEIGHT = 5;
    public static final double ARC_WIDTH = 5;

    public static final double[] AGENT_CHROMOSOME = {-0.45950405956858037, 0.5960425668385464, 0.27972246164649833, 0.1535224793617473, 0.031120704816792168, 0.2200616699134029, 0.15717120353367994, 0.03955697222290288, -0.49022188611061596, -0.12567734406275677};

    public static final String SCORE_LABEL_TXT = "Score: ";
    public static final double SCORE_LABEL_LAYOUT_X = GAME_FIELD_DISPLAY_SIZE_X + 2 * GAME_FIELD_DISPLAY_MARGIN;
    public static final double SCORE_LABEL_LAYOUT_Y = GAME_FIELD_DISPLAY_MARGIN;

    public static final String MODE_LABEL_TXT = "Mode:";
    public static final double MODE_LABEL_LAYOUT_X = SCORE_LABEL_LAYOUT_X;
    public static final double MODE_LABEL_LAYOUT_Y = SCORE_LABEL_LAYOUT_Y + GAME_FIELD_DISPLAY_MARGIN * 6;

    public static final String MODE_AI_RADIO_BUTTON_TXT = "AI";
    public static final double MODE_AI_RADIO_BUTTON_LAYOUT_X = SCORE_LABEL_LAYOUT_X + GAME_FIELD_DISPLAY_MARGIN * 0.66;
    public static final double MODE_AI_RADIO_BUTTON_LAYOUT_Y = MODE_LABEL_LAYOUT_Y + GAME_FIELD_DISPLAY_MARGIN * 1.25;

    public static final String MODE_HUMAN_RADIO_BUTTON_TXT = "Human";
    public static final double MODE_HUMAN_RADIO_BUTTON_LAYOUT_X = MODE_AI_RADIO_BUTTON_LAYOUT_X;
    public static final double MODE_HUMAN_RADIO_BUTTON_LAYOUT_Y = MODE_AI_RADIO_BUTTON_LAYOUT_Y + GAME_FIELD_DISPLAY_MARGIN;

    public static final String BUTTON_START_TXT = "Start";
    public static final double BUTTON_START_LAYOUT_X = MODE_LABEL_LAYOUT_X;
    public static final double BUTTON_START_LAYOUT_Y = MODE_HUMAN_RADIO_BUTTON_LAYOUT_Y + GAME_FIELD_DISPLAY_MARGIN * 6;


}


