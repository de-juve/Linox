package plugins.morfology;

import ij.ImagePlus;
import ij.process.ImageProcessor;
import plugins.DataCollection;
import workers.PixelsMentor;
import workers.ShedWorker;

import java.awt.*;
import java.util.ArrayList;


public class AreaEqualing extends MorfologyOperation {

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            create(imageProcessor, DataCollection.INSTANCE.getStatuses());
            result = new ImagePlus("area equaling " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                ImageProcessor ip = imageProcessor.duplicate();
                Color[] colors = new Color[width*height];
                for(int i = 0; i < DataCollection.INSTANCE.getShedLabels().length; i++) {
                    colors[i] = ShedWorker.getInstance().getShedColor(DataCollection.INSTANCE.getShedLabel(i));
                }
                create(ip, colors);
                DataCollection.INSTANCE.addtoHistory(new ImagePlus("area equaling colors " + criteria + " " + DataCollection.INSTANCE.getImageOriginal().getTitle(), ip));
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

	@Override
	public void run() {
		type = "equaling";
        criteria = 1;
        morfRun();
	}
}