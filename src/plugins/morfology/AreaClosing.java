package plugins.morfology;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import plugins.DataCollection;
import workers.ShedWorker;

import java.awt.*;

public class AreaClosing extends MorfologyOperation {
    public AreaClosing() {
        title = "Morphological area closing";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            create(imageProcessor, DataCollection.INSTANCE.getStatuses());
            result = new ImagePlus("area closing " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);

            if(addToStack) {
                ImageProcessor ip = imageProcessor.duplicate();
                Color[] colors = new Color[width*height];
                for(int i = 0; i < DataCollection.INSTANCE.getShedLabels().length; i++) {
                    colors[i] = ShedWorker.getInstance().getShedColor(DataCollection.INSTANCE.getShedLabel(i));
                }
                create(ip, colors);
                DataCollection.INSTANCE.addtoHistory(new ImagePlus("area closing colors " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), ip));
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

	@Override
	public void run() {
		if(criteria <= 0) {
			showDialog("area closing");
		}
		if(exit)   {
			return;
		}
		type = "closing";
		morfRun();

	}
}
