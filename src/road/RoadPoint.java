package road;

public class RoadPoint {
    private int position;
    private Direction direction;
    private RoadPoint nextPoint, previousPoint;
    private int roadWidth;

    public RoadPoint(Integer pixelId, Direction d, int _roadWidth) {
       position = pixelId;
       direction = d;
       roadWidth = _roadWidth;
    }

    public Direction getDirection() {
        return direction;
    }
}
