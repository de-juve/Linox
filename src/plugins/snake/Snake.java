package plugins.snake;

import java.util.LinkedList;

public class Snake<T extends LinePoint> {
    private LinkedList<T> head, neck, tail, line;
    private int headSize = 10, neckSize = 10, tailSize = 40;
    private int headId;
    private double step = 1;
    private double inc = 0.01;
    private boolean recount;

    public Snake() {
        head = new LinkedList<>();
        neck = new LinkedList<>();
        tail = new LinkedList<>();
        line = new LinkedList<>();
        headId = 0;
        recount = true;
    }

   /* public Snake(LinkedList<T> _neck, LinkedList<T> _tail) {
        head = new LinkedList<>();
        neck = new LinkedList<>(_neck);
        tail = new LinkedList<>(_tail);
        line = new LinkedList<>();
        headId = _tail.size();
    }*/

    public void addElementToHead(T element) {
        if(head.size() == headSize) {
            addElementsToNeck(head);
            head.clear();
        }
        head.addFirst(element);
        headId++;
    }

    public void addElementToNeck(T element) {
        if(neck.size() == neckSize) {
            addElementsToTail(neck);
            neck.clear();
        }
        neck.addFirst(element);
    }

    public void addElementToTail(T element) {
        if(tail.size() == tailSize) {
            addElementsToLine(tail);
            tail.clear();
        }
        tail.addFirst(element);
    }

    private void addElementsToNeck(LinkedList<T> elements) {
        if(neck.size() == neckSize) {
            addElementsToTail(neck);
            neck.clear();
        }
        neck.addAll(elements);
        recount = true;
    }

    private void addElementsToTail(LinkedList<T> elements) {
        if(tail.size() + elements.size() >= tailSize) {
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

    public LinkedList<T> getNeck() {
        return neck;
    }

    public LinkedList<T> getTail() {
        return tail;
    }

    public LinkedList<T> getLine() {
        return line;
    }

    public int getHeadSize() {
        return headSize;
    }

    public int getNeckSize() {
        return neckSize;
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
            addElementsToNeck(head);
            head.clear();
        }
        if(neck.size() > 0) {
            addElementsToTail(neck);
            neck.clear();
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

    public int getHeadId() {
        return headId;
    }

    public LinkedList<Integer> getTailValues() {
        LinkedList<Integer> values = new LinkedList<>();
        for(LinePoint p : tail) {
            values.addFirst(p.getY());
        }
        return values;
    }

    public LinkedList<Integer> getLineValues() {
        LinkedList<Integer> values = new LinkedList<>();
        for(LinePoint p : line) {
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
