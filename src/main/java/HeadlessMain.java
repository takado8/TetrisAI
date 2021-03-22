import tetris.environment.engine.Engine;

public class HeadlessMain {

    public static void main(String[] args) {
        Engine engine = new Engine();
        boolean result = false;
        while (!result) {
            result = engine.step();
        }
    }
}