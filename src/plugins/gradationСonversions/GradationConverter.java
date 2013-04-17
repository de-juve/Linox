package plugins.gradationСonversions;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import plugins.DataCollection;
import plugins.LuminanceCalculator;
import plugins.MyAPlugin;

import java.awt.*;


public class GradationConverter extends MyAPlugin implements DialogListener {
    GradationConversionsFactory factory;
    String typeConversion;
    int c;
    double y, base;
    Integer[] results;

    public GradationConverter() {
        this.factory = new GradationConversionsFactory();
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            create(imageProcessor, results);
            result = new ImagePlus(typeConversion + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        showDialog("Conversion");
        if(exit)   {
            return;
        }
        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

        results = new Integer[width*height];
        GradationConversion gradationConversion = factory.createImageOperation(typeConversion);
        gradationConversion.setWidth(width);
        gradationConversion.setHeight(height);
        gradationConversion.setBaseLogarithm(base);
        gradationConversion.setConstantC(c);
        gradationConversion.setConstantY(y);
        gradationConversion.createImage(DataCollection.INSTANCE.getLuminances(), results);


    }

    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());
        gd.addNumericField("Base for logarithm", 10, 1);
        gd.addSlider("Const 'c' for logarithm or power func", 1, 20, 1);
        gd.addNumericField("Const 'y' for power func", 1, 1);

        if(name.equalsIgnoreCase("conversion")) {
            gd.addChoice("Type of conversion", factory.getConversionsMap().keySet().toArray(new String[0]), factory.getConversionsMap().keySet().toArray(new String[0])[0]);
            typeConversion = factory.getConversionsMap().keySet().toArray(new String[0])[0];
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
        base = gd.getNextNumber();
        c = (int)gd.getNextNumber();
        y = gd.getNextNumber();
        typeConversion = gd.getNextChoice();
        return true;
    }
}
