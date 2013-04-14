package plugins;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.process.ImageProcessor;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class LuminanceDiscretizator extends MyAPlugin  implements DialogListener {
    private int discretizationLevels;
    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            create(imageProcessor, DataCollection.INSTANCE.getLuminances());
            result = new ImagePlus("discretization " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        showDialog("Discretization");

        if(exit)   {
            return;
        }

        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();
        int discretizationStep = 255 / discretizationLevels;

        Integer[] values = new Integer[width * height];
        for(int i = 0; i < width * height; i++) {
            int pixel = DataCollection.INSTANCE.getLuminance(i);
            values[i] = discretizationStep * (pixel / discretizationStep);
            DataCollection.INSTANCE.setLuminance(i, values[i]);
        }
    }

    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());
        gd.addNumericField("Discretization levels", 1, 0);

        gd.addDialogListener(this);

        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            return;
        }
    }

    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        discretizationLevels =(int) gd.getNextNumber();
        return true;
    }

}
