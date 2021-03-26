package tetris.environment.engine.tetrimino.features;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Rotates position around center by 90 degrees by default for this game
     * @param center center of rotation
     * @return rotated {@code Position}
     */
    public Position rotate(Position center) {
        int rotationDegrees = 90;
        double theta = Math.toRadians(rotationDegrees);
        int x = getX() - center.getX();
        int y = getY() - center.getY();
        int newX = (int) Math.round(x * Math.cos(theta) - y * Math.sin(theta));
        int newY = (int) Math.round(x * Math.sin(theta) + y * Math.cos(theta));
        return new Position(newX + center.getX(), newY + center.getY());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (x != position.x) return false;
        return y == position.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
