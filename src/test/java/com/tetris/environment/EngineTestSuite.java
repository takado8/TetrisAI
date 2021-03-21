package com.tetris.environment;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EngineTestSuite {
    @Test
    void testGetGameFieldArr() {
        // Given
        Engine engine = new Engine();
        var expected = engine.getGameFieldArrNoCopy();
        // when
        var actual = engine.getGameFieldArr();
        // than
        assertTrue(Arrays.deepEquals(expected, actual));
    }
}
