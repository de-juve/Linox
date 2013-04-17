package plugins.histogramChanging;

public class HistogramCounter {
    private static double[] histogram = new double[256];
    private static double mean, stdDev;

    public static void count(Integer[] luminances) {
        for(int i = 0; i < luminances.length; i++) {
            histogram[luminances[i]]++;
        }
        for(int i = 0; i < histogram.length; i++) {
            histogram[i] /= luminances.length;
        }
    }

    public static double[] getHistogram() {
        return histogram;
    }

    public static double getHistogramValue(int i) {
        if(i >= 0 && i < 256) {
            return histogram[i];
        }
        throw new ArrayIndexOutOfBoundsException(i);
    }
}
