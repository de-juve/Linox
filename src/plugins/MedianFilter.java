package plugins;

import ij.ImagePlus;
import plugins.histogramChanging.HistogramCounter;
import workers.PixelsMentor;

import java.util.ArrayList;

public class MedianFilter extends MyAPlugin {
    @Override
    public ImagePlus getResult(boolean addToStack) {
        result = new ImagePlus("median filter " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
        if(addToStack) {
            DataCollection.INSTANCE.addtoHistory(result);
        }

        return result;
    }

    @Override
    public void run() {
        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

        HistogramCounter hist = new HistogramCounter();

        for(int i = 0; i < width*height; i++) {
            ArrayList<Integer> neigh = PixelsMentor.defineAllNeighboursIds(i, 3, width, height);
            Integer[] arr = new Integer[neigh.size()];
            int j = 0;
            for(Integer n : neigh) {
                arr[j] = DataCollection.INSTANCE.getLuminance(n);
                j++;
            }
            hist.count(arr);
            int value = ((((int)hist.getMedian() & 0xff) << 16) +
                    (((int)hist.getMedian() & 0xff) << 8) +
                    ((int)hist.getMedian() & 0xff));
            imageProcessor.set(i, value);
        }
    }
}
