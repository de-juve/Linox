package plugins.snake;

import java.util.Stack;

public class LinePoint {
    private double x;
    private int y;
    private Stack<Integer> neighbours;

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

    public int getStackSize() {
        return neighbours.size();
    }

    public void createStack() {
        neighbours = new Stack<>();
    }

    public void pushNeighbour(int neighbour) {
        neighbours.push(neighbour);
    }

    public int popNeighbour() {
        return neighbours.pop();
    }

    public void removeStack() {
        if(neighbours != null) {
            neighbours.clear();
            neighbours = null;
        }
    }
}
