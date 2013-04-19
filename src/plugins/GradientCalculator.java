package plugins;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import image.Mask;
import image.MaskDgnl;
import image.MaskHrznt;
import image.MaskVrtcl;
import workers.GetterNeighboures;
import workers.MassiveWorker;
import workers.PixelsMentor;

import java.awt.*;
import java.util.ArrayList;

public class GradientCalculator extends MyAPlugin {
    public GradientCalculator() {
        title = "Gradient calculator";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
          /*  MassiveWorker worker = new MassiveWorker();
            worker.scale(DataCollection.INSTANCE.getGradients());*/

            create(imageProcessor, DataCollection.INSTANCE.getGradients());
            result = new ImagePlus("gradient " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {

        DataCollection.INSTANCE.newGradient(width*height);

        calculateGradient();
    }

    private void calculateGradient() {
        LuminanceCalculator luminanceCalculatorPlugin = new LuminanceCalculator();
        luminanceCalculatorPlugin.initProcessor(imageProcessor);
        luminanceCalculatorPlugin.run();

        for(int i = 0; i < DataCollection.INSTANCE.getLuminances().length; i++) {
            ArrayList<Integer> neigh = PixelsMentor.defineAllNeighboursIds(i, width, height);
            ArrayList<Integer> luminances = new ArrayList<>(neigh.size());
            for(Integer n : neigh) {
                luminances.add(DataCollection.INSTANCE.getLuminance(n));
            }
            int gradH = countGradientPixel(luminances, new MaskHrznt());
            int gradV = countGradientPixel(luminances, new MaskVrtcl());
            int gradD = countGradientPixel(luminances, new MaskDgnl());
            DataCollection.INSTANCE.setGradient(i, (gradH + gradV + gradD)/3);
        }
    }

    private int countGradientPixel(ArrayList<Integer> luminances, Mask mask)
    {
        return   Math.abs(mask.R(luminances));
    }
}
