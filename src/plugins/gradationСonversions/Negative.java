package plugins.gradationСonversions;

public class Negative extends GradationConversion{

    @Override
    protected void defineValues(Integer[] luminance) {

        for(int i = 0; i < luminance.length; i++) {
            values.add(i, 255d - luminance[i]);
        }
    }
}
