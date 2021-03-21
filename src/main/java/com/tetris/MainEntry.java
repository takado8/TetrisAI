package com.tetris;

import com.tetris.environment.engine.Engine;

public class MainEntry {
    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.step();
    }
}
