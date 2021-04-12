package tetris.environment.engine.tetrimino.features;

public class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(Position position) {
        this.x = position.getX();
        this.y = position.getY();
    }

    /**
     * Rotates position around center by 90 degrees by default for this game
     *
     * @param center center of rotation
     * @return rotated {@code Position}
     */
    public Position rotate(Position center) {
        int rotationDegrees = 90;
        double theta = Math.toRadians(rotationDegrees);
        int centerX = center.getX();
        int centerY = center.getY();
        int x = this.x - centerX;
        int y = this.y - centerY;
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        int newX = (int) Math.round(x * cosTheta - y * sinTheta);
        int newY = (int) Math.round(x * sinTheta + y * cosTheta);
        return new Position(newX + centerX, newY + centerY);
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
