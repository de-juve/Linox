package plugins.imageOperations;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.DialogListener;
import ij.gui.GenericDialog;
import plugins.DataCollection;
import plugins.MyAPlugin;

import java.awt.*;

public class ImageOperator  extends MyAPlugin implements DialogListener {
    ImageOperationFactory factory;
    String typeOperation;
    Integer[] minuend, subtrahend, results;

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            create(imageProcessor, results);
            result = new ImagePlus(typeOperation + " " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        if(criteria <= 0) {
            showDialog("operation");
        }
        if(exit)   {
            return;
        }

        minuend = DataCollection.INSTANCE.getStatuses();


        subtrahend = DataCollection.INSTANCE.getStatuses();

        results = new Integer[subtrahend.length];

        ImageOperation imageOperation = factory.createImageOperation(typeOperation);
        imageOperation.setWidth(width);
        imageOperation.setHeight(height);
        imageOperation.createImage(minuend, subtrahend, results);

    }


    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());

        if(name.equalsIgnoreCase("operation")) {
            gd.addChoice("Type of operation", factory.getOperationMap().keySet().toArray(new String[0]), factory.getOperationMap().keySet().toArray(new String[0])[0]);
            typeOperation = factory.getOperationMap().keySet().toArray(new String[0])[0];
        }
        gd.addDialogListener(this);

        gd.showDialog();
        if (gd.wasCanceled()) {
            exit = true;
            return;
        }
    }

    @Override
    public boolean dialogItemChanged(GenericDialog gd, AWTEvent e) {
        typeOperation = gd.getNextChoice();
        return true;
    }
}
