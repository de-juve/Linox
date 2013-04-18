package plugins.histogramChanging;

public class HistogramEqualisation extends HistogramChanging {
    @Override
    protected void defineValues(Integer[] luminance) {
        HistogramCounter hist = new HistogramCounter();
        hist.count(luminance);

        double[] s = new double[hist.getHistogram().length];
        for(int i = 0; i < hist.getHistogram().length; i++) {
            for(int j = 0; j <= i; j++) {
                s[i] += hist.getHistogramValue(luminance[j]);
            }
        }

        for(int i = 0; i < luminance.length; i++) {
            values.add(i, s[luminance[i]]);
        }
    }
}
