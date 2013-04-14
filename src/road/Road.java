package road;

import java.util.LinkedList;

public class Road {
    LinkedList<RoadPoint> points;

    public Road() {
        points = new LinkedList<>();
    }

    public void addPoint(RoadPoint point) {
        points.add(point);
    }

    public LinkedList<RoadPoint> getPoints() {
        return points;
    }
}
