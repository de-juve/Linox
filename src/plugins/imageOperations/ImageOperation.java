package plugins.imageOperations;

import workers.ShedWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

public abstract class ImageOperation {
    protected ArrayList<Integer> values;
    protected int width, height;

    public void createImage(Integer[] closing, Integer[] opening, TreeMap<Integer, ShedWorker.Shed> closingSheds, TreeMap<Integer, ShedWorker.Shed> openingSheds, Integer[] result) {
        values = new ArrayList<>();

        if(closingSheds == null) {
            defineValues(closing, opening);
        }   else {
            defineValues(closing, opening, closingSheds, openingSheds);
        }
        int max = Collections.max(values);
        int min = Collections.min(values);
        for (int i = 0; i < closing.length; i++) {
            result[i] = ((values.get(i) - min) * 255 / (max - min));
        }
    }

    public void createImage(Integer[] closing, Integer[] opening, Integer[] result) {
        createImage(closing, opening, null, null, result);
    }

    protected Integer getValue(int id) {
        return values.get(id);
    }

    protected abstract void defineValues(Integer[] closing, Integer[] opening, TreeMap<Integer, ShedWorker.Shed> closingSheds, TreeMap<Integer, ShedWorker.Shed> openingSheds);
    protected abstract void defineValues(Integer[] closing, Integer[] opening);

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
