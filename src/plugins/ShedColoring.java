package plugins;


import ij.ImagePlus;
import plugins.morfology.AreaEqualing;
import workers.Clustering;
import workers.ShedWorker;

import java.awt.*;

public class ShedColoring extends  MyAPlugin {
    public ShedColoring() {
        title = "Sheds coloring";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            Color[] colors = new Color[width*height];
            for(int i = 0; i < DataCollection.INSTANCE.getShedLabels().length; i++) {
                colors[i] = ShedWorker.getInstance().getShedColor(DataCollection.INSTANCE.getShedLabel(i));
            }
            create(imageProcessor, colors);
            result = new ImagePlus("sheds coloring " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);

            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        AreaEqualing areaEqualing = new AreaEqualing();
        areaEqualing.initProcessor(imageProcessor);
        areaEqualing.run();
        Clustering.fillShedsWithDiagonalNeighboureCondition(width, height);

    }
}
