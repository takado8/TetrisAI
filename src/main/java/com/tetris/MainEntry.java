package com.tetris;

import com.tetris.environment.Brick;
import com.tetris.environment.Engine;

public class MainEntry {
    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.step();
    }
}
