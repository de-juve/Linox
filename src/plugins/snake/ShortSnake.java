package plugins.snake;

import java.util.LinkedList;

public class ShortSnake<T extends LinePoint> {
    private LinkedList<T> head, baseSetPoints;
    private int headSize = 10, baseSetPointsSize = 10;
    private int headId, baseSetPointsId;
    private double step = 1;
    private double inc = 0.01;
    private double minStep = 1E-4;
    private double maxStep = 1E3;

    public ShortSnake() {
        head = new LinkedList<>();
        baseSetPoints = new LinkedList<>();
        headId = 0;
        baseSetPointsId = 0;
    }

    public boolean addFirstElementToHead(T element) {
        if(head.size() + 1 > headSize) {
            head.clear();
        }
        head.addFirst(element);
        headId = (int)element.getX();
        return true;
    }

    public boolean addFirstElementToBaseSetPoints(T element) {
        if(baseSetPoints.size() + 1 > baseSetPointsSize) {
            return false;
        }
        baseSetPoints.addFirst(element);
        baseSetPointsId = (int)element.getX();
        return true;
    }

    public LinkedList<T> getHead() {
        return head;
    }

    public int getBaseSetPointsId() {
        return baseSetPointsId;
    }

    public LinkedList<T> getBaseSetPoints() {
        return baseSetPoints;
    }

    public boolean increaseStep() {
        if(step >= maxStep) {
            return false;
        }
        step += inc;
        return true;
    }

    public boolean reduceStep() {
        if(step <= minStep ) {
            return false;
        }
        while(step-inc <= 0) {
            inc /= 2;
        }
        step -= inc;
        return true;
    }

    public double getStep() {
        return step;
    }

    public double getHeadId() {
        return headId;
    }
}