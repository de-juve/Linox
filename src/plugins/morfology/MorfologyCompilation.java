package plugins.morfology;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import plugins.imageOperations.ImageOperation;
import plugins.DataCollection;
import plugins.imageOperations.ImageOperationFactory;

import java.awt.*;

public class MorfologyCompilation extends MorfologyOperation {
    ImageOperationFactory factory;
    Integer[] resultOfClosing, resultOfOpening, results;

    public MorfologyCompilation() {
        this.factory = new ImageOperationFactory();
        title = "Morphological compilation";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            create(imageProcessor, results);
            result = new ImagePlus(typeCompilation + " " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        if(criteria <= 0) {
            showDialog("compilation");
        }
        if(exit)   {
            return;
        }
        type = "closing";
        morfRun();
        resultOfClosing = DataCollection.INSTANCE.getStatuses();

        type = "opening";
        morfRun();
        resultOfOpening = DataCollection.INSTANCE.getStatuses();

        results = new Integer[resultOfClosing.length];

        ImageOperation imageOperation = factory.createImageOperation(typeCompilation);
        imageOperation.setWidth(width);
        imageOperation.setHeight(height);
        imageOperation.createImage(resultOfClosing, resultOfOpening, results);

    }



    @Override
    protected void showDialog(String name) {
        GenericDialog gd = new GenericDialog(name, IJ.getInstance());
        gd.addSlider("Criterion", Math.min(1, width), width*height-1, 1);

        if(name.equalsIgnoreCase("compilation")) {
            gd.addChoice("Type of compilation", factory.getOperationMap().keySet().toArray(new String[0]), factory.getOperationMap().keySet().toArray(new String[0])[0]);
            typeCompilation = factory.getOperationMap().keySet().toArray(new String[0])[0];
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
        criteria = (int) gd.getNextNumber();
        typeCompilation = gd.getNextChoice();
        return true;
    }
}
