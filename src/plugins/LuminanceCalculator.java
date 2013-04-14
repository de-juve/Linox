package plugins;

import gui.Linox;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import workers.MassiveWorker;


public class LuminanceCalculator extends MyAPlugin {

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            create(imageProcessor, DataCollection.INSTANCE.getLuminances());
            result = new ImagePlus("luminance " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        Linox.getInstance().getStatusBar().setProgress("luminance", 0, 100);

        DataCollection.INSTANCE.newLuminances(width*height);

        calculateLuminance();
    }

    private void calculateLuminance() {
        for(int i = 0; i < DataCollection.INSTANCE.getLuminances().length; i++) {
            int pixel = imageProcessor.get(i);
            int r = (pixel & 0xff0000)>>16;
            int g = (pixel & 0x00ff00)>>8;
            int b = (pixel & 0x0000ff);
            int lum = (int)(0.299*r + 0.587*g + 0.114*b);
            DataCollection.INSTANCE.setLuminance(i, lum);

            Linox.getInstance().getStatusBar().setProgress("luminance", i*100/DataCollection.INSTANCE.getLuminances().length, 100);
        }
    }
}
