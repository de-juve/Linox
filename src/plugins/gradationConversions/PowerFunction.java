package plugins.gradationConversions;

public class PowerFunction extends GradationConversion{

    @Override
    protected void defineValues(Integer[] luminance) {

        for(int i = 0; i < luminance.length; i++) {
            values.add(i, constC*Math.pow(luminance[i], constY));
        }
    }
}

