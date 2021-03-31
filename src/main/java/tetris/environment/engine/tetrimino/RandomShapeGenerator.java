package tetris.environment.engine.tetrimino;

import tetris.environment.engine.tetrimino.features.Shape;
import java.util.Random;

import static tetris.environment.engine.tetrimino.features.Shape.*;

class RandomShapeGenerator {
    private static final Random random = new Random();
    private static final Shape[] shapesArray = {SHAPE_I, SHAPE_O, SHAPE_T, SHAPE_L, SHAPE_J, SHAPE_S, SHAPE_Z};
    private static int shape_index = shapesArray.length;


    protected Shape getShape() {
        if (shape_index == shapesArray.length) {
            shuffleShapesArray();
            shape_index = 0;
        }
        return shapesArray[shape_index++];
    }

    protected Shape getNextShape() {
        if (shape_index == shapesArray.length) {
            shuffleShapesArray();
            shape_index = 0;
        }
        return shapesArray[shape_index];
    }

    private void shuffleShapesArray() {
        int n = shapesArray.length;
        while (n > 1) {
            n--;
            int k = random.nextInt(n + 1);
            var value = shapesArray[k];
            shapesArray[k] = shapesArray[n];
            shapesArray[n] = value;
        }
    }
}
