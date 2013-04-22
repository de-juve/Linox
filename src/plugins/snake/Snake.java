package plugins.snake;

import java.util.LinkedList;

public class Snake<T extends LinePoint> {
    LinkedList<T> tail, head;
    double step = 1;

    public Snake() {
        tail = new LinkedList<>();
        head = new LinkedList<>();
    }
    public Snake(LinkedList<T> _tail, LinkedList<T> _head) {
        tail = new LinkedList<>(_tail);
        head = new LinkedList<>(_head);
    }

    public void addElementToHead(T element) {
        head.addFirst(element);
    }

    public void removeElementFromHead() {
        head.removeLast();
    }

    public void addElementToTail(T element) {
        tail.addFirst(element);
    }

    public void removeElementFromTail() {
        tail.removeLast();
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

    public void setHead(LinkedList<T> head) {
        this.head = head;
    }

    public void increaseStep() {
        step += 0.01;
    }

    public void reduceStep() {
        step -= 0.01;
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

    public LinkedList<Integer> getHeadValues() {
        LinkedList<Integer> values = new LinkedList<>();
        for(LinePoint p : head) {
            values.addFirst(p.getY());
        }
        return values;
    }
}
