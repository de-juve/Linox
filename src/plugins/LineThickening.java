package plugins;


import ij.ImagePlus;
import road.Direction;
import road.Road;
import road.RoadPoint;
import workers.PixelsMentor;

import java.awt.*;
import java.util.LinkedList;
import java.util.ListIterator;

public class LineThickening extends MyAPlugin {
    public LineThickening() {
        title = "Line thickening";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        result = new ImagePlus("line thickening " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
        if(addToStack) {
            DataCollection.INSTANCE.addtoHistory(result);
        }

        return result;
    }

    @Override
    public void run() {
        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

        LinkedList<Integer> line = DataCollection.INSTANCE.getLine();
        ShowStaticstics.showHistogram(line, false);
        ListIterator<Integer> iterator = line.listIterator();
        if(!iterator.hasNext()) {
            return;
        }
        Road road = new Road();
        int current = iterator.next();
        while (iterator.hasNext()) {
            int next = iterator.next();
            RoadPoint roadPoint = new RoadPoint(current, Direction.defineDirection(current, next, width));
            thickening(current, roadPoint, 1);
            thickening(current, roadPoint, 2);
            road.addPoint(roadPoint);
            current = next;
        }
        for(RoadPoint roadPoint : road.getPoints()) {
            for(Integer point : roadPoint.getPoints()) {
                imageProcessor.set(point, Color.RED.getRGB());
            }
        }
    }

    private void thickening(int current, RoadPoint roadPoint, int type) {
        Integer neighboure = -1;
        if(type == 1) {
            neighboure = PixelsMentor.defineNeighbourId(current, roadPoint.getDirection().opposite1(), width, height);
        } else {
            neighboure = PixelsMentor.defineNeighbourId(current, roadPoint.getDirection().opposite2(), width, height);
        }
        dilating(roadPoint, neighboure, type);
    }

    private void dilating(RoadPoint roadPoint, Integer neighboure, int type) {
        if(neighboure > 0 && Math.abs(DataCollection.INSTANCE.getLuminance(neighboure) - (int)ShowStaticstics.mean) <= 5/*ShowStaticstics.stdDev*/) {
            roadPoint.addPoint(neighboure);
            thickening(neighboure, roadPoint, type);
        }
    }
}
