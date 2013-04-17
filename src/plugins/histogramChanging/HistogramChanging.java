package plugins.histogramChanging;

import java.util.ArrayList;
import java.util.Collections;

public abstract class HistogramChanging {
    protected ArrayList<Double> values;
    protected int width, height;

    public void createImage(Integer[] luminance, Integer[] result) {
        values = new ArrayList<>();

        defineValues(luminance);
        double max = Collections.max(values);
        double min = Collections.min(values);
        for(int i = 0; i < luminance.length; i++) {
            result[i] = (int)((values.get(i) - min)*255/(max-min));
        }
    }

    protected Double getValue(int id) {
        return values.get(id);
    }

    protected abstract void defineValues(Integer[] luminance);

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
