package plugins.snake;

import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeMap;

public class Penalty {
    private int threshold;
    private LinkedList<Double> penalties;
    private int penaltiesSize = 10;
    private double[] weights;
    private TreeMap<Double, LinkedList<LinePoint>> recoveryPointMap;

    public Penalty(int threshold) {
        penalties = new LinkedList<>();
        weights = new double[] {2, 1.5, 1, 0.5, 0.3, 0.2, 0.15, 0.1, 0.05, 0.01};
        this.threshold = threshold;
        recoveryPointMap = new TreeMap<>();
    }

    public void addPenalty(double value, LinkedList<LinePoint> line) {
        if(penalties.size() >= penaltiesSize) {
            double last = penalties.removeLast();
            if(recoveryPointMap.containsKey(last)) {
                recoveryPointMap.remove(last);
            }
        }
        penalties.addFirst(value);
        recoveryPointMap.put(value, line);
    }

    public void addPenalty(double value) {
        if(penalties.size() >= penaltiesSize) {
            penalties.removeLast();
        }
        penalties.addFirst(value);
    }

    public double countPenalty() {
        double result = 0;
        for(int i = 0; i < penalties.size(); i++) {
            result += weights[i] * penalties.get(i);
        }
        return result;
    }

    public LinkedList<LinePoint> recover() throws NegativeArraySizeException {
        if(recoveryPointMap.size() > 0) {
            double maxKey = Collections.max(recoveryPointMap.keySet());
            return recoveryPointMap.remove(maxKey);
        } else {
            throw new NegativeArraySizeException("Empty recoveryPointMap");
        }
    }


    public LinkedList<LinePoint> recoverFirst() throws NegativeArraySizeException {
        if(recoveryPointMap.size() > 0) {
            return recoveryPointMap.remove(recoveryPointMap.firstKey());
        } else {
            throw new NegativeArraySizeException("Empty recoveryPointMap");
        }
    }



    public int getThreshold() {
        return threshold;
    }


}
