package tetris.environment.engine;

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
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public String toString() {
        return "SimulationResult{" +
                "movesRight=" + movesRight +
                ", rotations=" + rotations +
                '}';
    }
}
