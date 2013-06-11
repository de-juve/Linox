package plugins.imageOperations;

public class ImageOperationMax extends ImageOperation {
    @Override
    protected void defineValues(Integer[] closing, Integer[] opening) {
        for (int i = 0; i < closing.length; i++) {
            values.add(i, Math.max(closing[i], opening[i]));
        }
    }
}