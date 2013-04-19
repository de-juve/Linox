package plugins.histogramChanging;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.plugin.Histogram;
import plugins.DataCollection;
import plugins.LuminanceCalculator;
import plugins.MyAPlugin;

import java.awt.*;

public class HistogramChanger extends MyAPlugin implements DialogListener {
    HistogramChangingFactory factory;
    String typeChanging;
    Integer[] results;

    public HistogramChanger() {
        this.factory = new HistogramChangingFactory();
        title = "Histogram changing";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            create(imageProcessor, results);
            result = new ImagePlus(typeChanging + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        showDialog("Histogram Changing");
        if(exit)   {
            return;
        }
        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

        results = new Integer[width*height];
        HistogramChanging histogramChanging = factory.createImageOperation(typeChanging);
        histogramChanging.setWidth(width);
        histogramChanging.setHeight(height);
        histogramChanging.createImage(DataCollection.INSTANCE.getLuminances(), results);
    }

    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());

        if(name.equalsIgnoreCase("histogram changing")) {
            gd.addChoice("Type of changings", factory.getHistogramChangingMap().keySet().toArray(new String[0]), factory.getHistogramChangingMap().keySet().toArray(new String[0])[0]);
            typeChanging = factory.getHistogramChangingMap().keySet().toArray(new String[0])[0];
        }
        gd.addDialogListener(this);

        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            return;
        }
    }

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent awtEvent) {
        typeChanging = gd.getNextChoice();
        return true;
    }

}
