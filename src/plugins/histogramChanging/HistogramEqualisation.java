package plugins.histogramChanging;

public class HistogramEqualisation extends HistogramChanging {
    @Override
    protected void defineValues(Integer[] luminance) {
        HistogramCounter.count(luminance);

        double[] s = new double[HistogramCounter.getHistogram().length];
        for(int i = 0; i < HistogramCounter.getHistogram().length; i++) {
            for(int j = 0; j <= i; j++) {
                s[i] += HistogramCounter.getHistogramValue(luminance[j]);
            }
        }

        for(int i = 0; i < luminance.length; i++) {
            values.add(i, s[luminance[i]]);
        }
    }
}
