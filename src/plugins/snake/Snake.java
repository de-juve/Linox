package plugins.snake;

import java.util.LinkedList;

public class Snake<T extends LinePoint> {
    private LinkedList<T> tail, head, line;
    private int tailSize = 40, headSize = 10;
    private double step = 1;
    private double inc = 0.01;

    public Snake() {
        tail = new LinkedList<>();
        head = new LinkedList<>();
        line = new LinkedList<>();
    }
    public Snake(LinkedList<T> _tail, LinkedList<T> _head) {
        tail = new LinkedList<>(_tail);
        head = new LinkedList<>(_head);
        line = new LinkedList<>();
    }

    public void addElementToHead(T element) {
        if(head.size() == headSize) {
            tail.addFirst(head.removeLast());
        }
        head.addFirst(element);
    }

    public void addElementToTail(T element) {
        if(tail.size() == tailSize) {
            line.addFirst(tail.removeLast());
        }
        tail.addFirst(element);
    }

    public void addElementToLine(T element) {
        line.addFirst(element);
    }

    public void addElement(T element) {
        addElementToHead(element);
        addElementToTail(element);
    }

    public LinkedList<T> getTail() {
        return tail;
    }

    public LinkedList<T> getHead() {
        return head;
    }

    public LinkedList<T> getLine() {
        return line;
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
}
