import tetris.environment.engine.Action;
import tetris.environment.engine.Engine;
import tetris.environment.engine.StepResult;

public class HeadlessMain {

    public static void main(String[] args) {
        System.out.println("Headless main()");
        Engine engine = new Engine(true);
        engine.reset();
        boolean final_ste = false;
        while (!final_ste){

           var res = engine.step(Action.END_TURN);
           engine.printGameFieldArr();
           final_ste = res.isFinalStep();
        }
    }
}