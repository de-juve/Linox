package plugins.gradationConversions;


public class Logarithm extends GradationConversion {
    @Override
    protected void defineValues(Integer[] luminance) {
        for (int i = 0; i < luminance.length; i++) {
            values.add(i, constC * Math.log(1 + luminance[i]) / Math.log(logBase));
        }
    }
}
