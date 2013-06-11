package plugins.imageOperations;

public class ImageOperationPlus extends ImageOperation {
    @Override
    protected void defineValues(Integer[] closing, Integer[] opening) {
        for (int i = 0; i < closing.length; i++) {
            values.add(i, closing[i] + opening[i]);
        }
    }
}
