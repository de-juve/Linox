package plugins.snake;

public class LinePoint {
    private double x;
    private int y;

    public LinePoint(double x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public double getX() {
        return x;
    }
}
