package plugins;

import ij.ImagePlus;
import image.Mask;
import image.MaskLaplas;
import workers.PixelsMentor;

import java.util.ArrayList;

public class Laplasian extends MyAPlugin {

    public Laplasian() {
        title = "Laplasian";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            /*MassiveWorker worker = new MassiveWorker();
            worker.scale(DataCollection.INSTANCE.getLaplasians());*/
            create(imageProcessor, DataCollection.INSTANCE.getLaplasians());
            result = new ImagePlus("laplasian " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        DataCollection.INSTANCE.newLaplasian(width * height);
        calculateLaplasian();
    }

    private void calculateLaplasian() {
        LuminanceCalculator luminanceCalculatorPlugin = new LuminanceCalculator();
        luminanceCalculatorPlugin.initProcessor(imageProcessor);
        luminanceCalculatorPlugin.run();

        for(int i = 0; i < DataCollection.INSTANCE.getLuminances().length; i++) {
            ArrayList<Integer> neigh = PixelsMentor.defineAllNeighboursIds(i, width, height);
            ArrayList<Integer> luminances = new ArrayList<>(neigh.size());
            for(Integer n : neigh) {
                luminances.add(DataCollection.INSTANCE.getLuminance(n));
            }
            int lapl = countLaplasianPixel(luminances, new MaskLaplas());
            DataCollection.INSTANCE.setLaplasian(i, lapl);
        }
    }

    private int countLaplasianPixel(ArrayList<Integer> luminances, Mask mask)
    {
        return   Math.abs(mask.R(luminances));
    }
}