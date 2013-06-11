package plugins.imageOperations;

import gui.Linox;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import ij.process.ImageProcessor;
import plugins.DataCollection;
import plugins.LuminanceCalculator;
import plugins.MyAPlugin;
import sun.awt.image.ImageAccessException;

import java.awt.*;

public class ImageOperator extends MyAPlugin implements DialogListener {
    ImageOperationFactory factory;
    String typeOperation;

    Integer[] minuend, subtrahend, results;
    ImageProcessor ip;

    public ImageOperator() {
        this.factory = new ImageOperationFactory();
        title = "Image operator";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if (result == null) {
            create(imageProcessor, results);
            result = new ImagePlus(typeOperation + " " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if (addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        if (Linox.getInstance().getImageStore().getTitles().size() < 2) {
            exit = true;
            setErrMessage("not enough images");
        } else {
            showDialog("operation");
        }

        if (exit) {
            return;
        }

        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();
        minuend = DataCollection.INSTANCE.getLuminances();

        luminanceCalculator.initProcessor(ip);
        luminanceCalculator.run();
        subtrahend = DataCollection.INSTANCE.getLuminances();

        results = new Integer[subtrahend.length];

        ImageOperation imageOperation = factory.createImageOperation(typeOperation);
        imageOperation.setWidth(width);
        imageOperation.setHeight(height);
        imageOperation.createImage(minuend, subtrahend, results);

    }


    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());

        gd.addChoice("Select minuend", Linox.getInstance().getImageStore().getTitles().toArray(new String[0]), Linox.getInstance().getImageStore().getTitles().toArray(new String[0])[0]);
        gd.addChoice("Select subtrahend", Linox.getInstance().getImageStore().getTitles().toArray(new String[0]), Linox.getInstance().getImageStore().getTitles().toArray(new String[0])[1]);


        if (name.equalsIgnoreCase("operation")) {
            gd.addChoice("Type of operation", factory.getOperationMap().keySet().toArray(new String[0]), factory.getOperationMap().keySet().toArray(new String[0])[0]);
            typeOperation = factory.getOperationMap().keySet().toArray(new String[0])[0];
        }
        gd.addDialogListener(this);

        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            setErrMessage("canceled");
            return;
        }
    }

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        try {
            imageProcessor = Linox.getInstance().getImageStore().getImage(gd.getNextChoice()).getProcessor().duplicate();
            ip = Linox.getInstance().getImageStore().getImage(gd.getNextChoice()).getProcessor().duplicate();
        } catch (ImageAccessException e1) {
            exit = true;
            setErrMessage(e1.getMessage());
        }
        if (ip.getWidth() != imageProcessor.getWidth() || ip.getHeight() != imageProcessor.getHeight()) {
            exit = true;
            setErrMessage("Images have different size");
        }
        typeOperation = gd.getNextChoice();
        return true;
    }
}
