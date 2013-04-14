package image;

public class ImageOperationMinus extends ImageOperation{
    @Override
    protected void defineValues(Integer[] closing, Integer[] opening) {
        for(int i = 0; i < closing.length; i++) {
            values.add(i, Math.abs(closing[i] - opening[i]));
        }
    }
}
