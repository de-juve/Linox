package road;

import java.util.ArrayList;

public class RoadPoint {
    ArrayList<Integer> points;
    Direction direction;
  //  RoadPoint nextPoint, previousPoint;

    public RoadPoint(Integer point, Direction d) {
        points = new ArrayList<>();
        points.add(point);
        direction = d;
    }

    public void addPoint(Integer point) {
        points.add(point);
    }

    public Direction getDirection() {
        return direction;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }
}
