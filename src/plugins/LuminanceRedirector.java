package plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.process.ImageProcessor;
import workers.MassiveWorker;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class LuminanceRedirector extends MyAPlugin  implements DialogListener {
    public LuminanceRedirector() {
        title = "Luminance redirector";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            create(imageProcessor, DataCollection.INSTANCE.getLuminances());
            result = new ImagePlus("luminance redirect " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        if(DataCollection.INSTANCE.getMaxLuminance() == 255) {
            showDialog("new high luminance");
        }
        if(exit)   {
            return;
        }

        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

        int max = DataCollection.INSTANCE.getMaxLuminance();
        Integer[] values = new Integer[width * height];
        for(int i = 0; i < width * height; i++) {
            int pixel = DataCollection.INSTANCE.getLuminance(i);
            int newDouble = new BigDecimal(Math.abs(255f/max * ((float)pixel - max))).setScale(0, RoundingMode.HALF_UP).intValue();
            int value = 255 - newDouble;
            values[i] =value;

        }

        for(int i = 0; i < width * height; i++) {
            DataCollection.INSTANCE.setLuminance(i, values[i]);
        }

    }

    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());
        gd.addNumericField("Luminance of road", DataCollection.INSTANCE.getMaxLuminance(), 0);

        gd.addDialogListener(this);

        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            setErrMessage("canceled");
            return;
        }
    }

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        DataCollection.INSTANCE.setMaxLuminance((int) gd.getNextNumber());
        return true;
    }
}
