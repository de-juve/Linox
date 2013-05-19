package plugins.snake;

import java.util.LinkedList;

public class Snake<T extends LinePoint> {
    private LinkedList<T> head, tail, baseSetPoints, line;
    private int headSize = 10, baseSetPointsSize = 10, tailSize = 40;
    private int headId, baseSetPointsId;
    private double step = 1;
    private double inc = 0.01;
    private boolean recount;

    public Snake() {
        head = new LinkedList<>();
        baseSetPoints = new LinkedList<>();
        tail = new LinkedList<>();
        line = new LinkedList<>();
        headId = 0;
        recount = true;
    }

    public boolean addFirstElementToHead(T element) {
        if(head.size() + 1 > headSize) {
            addElementsToTail(head);
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

    public void removeElementsFromBaseSetPoints(int count) {
        for(int i = 0; i < Math.min(Math.abs(count), baseSetPointsSize-1); i++) {
            baseSetPoints.removeLast();
        }
    }

    public int getBaseSetPointsId() {
        return baseSetPointsId;
    }

    private void addElementsToTail(LinkedList<T> elements) {
        if(tail.size() + elements.size() > tailSize) {
            addElementsToLine(tail);
            tail.clear();
        }
        tail.addAll(elements);
    }

    private void addElementsToLine(LinkedList<T> elements) {
        line.addAll(elements);
    }

    public LinkedList<T> getHead() {
        return head;
    }

    public LinkedList<T> getBaseSetPoints() {
        return baseSetPoints;
    }

    public LinkedList<T> getLine() {
        return line;
    }

    public int getHeadSize() {
        return headSize;
    }

    public int getBaseSetPointsSize() {
        return baseSetPointsSize;
    }

    public int getTailSize() {
        return tailSize;
    }

    public boolean isRecount() {
        if(recount) {
            recount = !recount;
            return !recount;
        }
        return recount;
    }

    public void merge() {
        if(head.size() > 0) {
            addElementsToTail(head);
            head.clear();
        }
        if(tail.size() > 0) {
            addElementsToLine(tail);
            tail.clear();
        }
    }

    public void setHead(LinkedList<T> head) {
        this.head = head;
    }

    public void increaseStep() {
        step += inc;
    }

    public void reduceStep() {
        while(step-inc <= 0) {
            inc /= 2;
        }
        step -= inc;
    }

    public double getStep() {
        return step;
    }

    public double getHeadId() {
        return headId;
    }

    public LinkedList<Integer> getTailValues() {
        LinkedList<Integer> values = new LinkedList<>();
        for(LinePoint p : tail) {
            values.addFirst(p.getY());
        }
        return values;
    }

    public boolean headContains(int value) {
        for(LinePoint p : head) {
            if(p.getY() == value) {
                return true;
            }
        }
        return false;
    }

    public boolean tailContains(int value) {
        for(LinePoint p : tail) {
            if(p.getY() == value) {
                return true;
            }
        }
        return false;
    }
}
