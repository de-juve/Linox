package plugins.morfology;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import plugins.DataCollection;

public class AreaOpening extends MorphologyOperation {
    public AreaOpening() {
        title = "Morphological area opening";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if (result == null) {
            create(imageProcessor, DataCollection.INSTANCE.getStatuses());
            result = new ImagePlus("area opening " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);

            if (addToStack) {
                ImageProcessor ip = imageProcessor.duplicate();
                create(ip, DataCollection.INSTANCE.getShedLabels());
                DataCollection.INSTANCE.addtoHistory(new ImagePlus("area opening colors " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), ip));
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        if (criteria <= 0) {
            showDialog("area opening");
        }
        if (exit) {
            return;
        }
        type = "opening";
        morfRun();

    }
}
