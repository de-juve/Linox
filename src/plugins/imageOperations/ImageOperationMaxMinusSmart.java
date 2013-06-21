package plugins.imageOperations;


import workers.ShedWorker;

import java.util.ArrayList;
import java.util.TreeMap;

public class ImageOperationMaxMinusSmart extends ImageOperation {

    @Override
    protected void defineValues(Integer[] closing, Integer[] opening) {
    }

    @Override
    protected void defineValues(Integer[] closing, Integer[] opening, TreeMap<Integer, ShedWorker.Shed> closingSheds, TreeMap<Integer, ShedWorker.Shed> openingSheds) {
        ImageOperation max = new ImageOperationMax();
        max.values = new ArrayList<>();
        max.defineValues(closing, opening);

        ImageOperation smart = new ImageOperationSmart();
        smart.setWidth(width);
        smart.setHeight(height);
        smart.values = new ArrayList<>();
        smart.defineValues(closing, opening, closingSheds, openingSheds);

        for (int i = 0; i < closing.length; i++) {
            values.add(i, Math.abs(max.getValue(i) - smart.getValue(i)));
        }
    }
}
