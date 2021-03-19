package com.tetris.environment;

public class Engine {
    public final static int GAME_FIELD_SIZE_X = 20;
    public final static int GAME_FIELD_SIZE_Y = 20;
    int[][] simpleGameFieldArr = new int[GAME_FIELD_SIZE_Y][GAME_FIELD_SIZE_X];
    Tetrimino falling_tetrimino;
    
    public Engine() {

    }
}
