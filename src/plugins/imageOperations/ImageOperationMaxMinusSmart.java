package plugins.imageOperations;


import java.util.ArrayList;

public class ImageOperationMaxMinusSmart extends ImageOperation {

    @Override
    protected void defineValues(Integer[] closing, Integer[] opening) {
        ImageOperation max = new ImageOperationMax();
        max.values = new ArrayList<>();
        max.defineValues(closing, opening);

        ImageOperation smart = new ImageOperationSmart();
        smart.setWidth(width);
        smart.setHeight(height);
        smart.values = new ArrayList<>();
        smart.defineValues(closing, opening);

        for (int i = 0; i < closing.length; i++) {
            values.add(i, Math.abs(max.getValue(i) - smart.getValue(i)));
        }
    }
}
