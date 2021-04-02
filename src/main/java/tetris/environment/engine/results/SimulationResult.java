package tetris.environment.engine.results;

public class SimulationResult {
    private final int movesRight;
    private final int rotations;

    public SimulationResult(int movesRight, int rotations) {
        this.movesRight = movesRight;
        this.rotations = rotations;
    }

    public int getMovesRight() {
        return movesRight;
    }

    public int getRotations() {
        return rotations;
    }

    @Override
    public String toString() {
        return "SimulationResult{" +
                "movesRight=" + movesRight +
                ", rotations=" + rotations +
                '}';
    }
}
