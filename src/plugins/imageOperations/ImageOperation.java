package plugins.imageOperations;

import java.util.ArrayList;
import java.util.Collections;

public abstract class ImageOperation {
    protected ArrayList<Integer> values;
    protected int width, height;

    public void createImage(Integer[] closing, Integer[] opening, Integer[] result) {
        values = new ArrayList<>();

        defineValues(closing, opening);
        int max = Collections.max(values);
        int min = Collections.min(values);
        for (int i = 0; i < closing.length; i++) {
            result[i] = ((values.get(i) - min) * 255 / (max - min));
        }
    }

    protected Integer getValue(int id) {
        return values.get(id);
    }

    protected abstract void defineValues(Integer[] closing, Integer[] opening);

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
