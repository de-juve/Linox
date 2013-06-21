package plugins.imageOperations;

import plugins.DataCollection;
import workers.ShedWorker;

import java.util.*;

public class ImageOperationSmart extends ImageOperation {

    @Override
    protected void defineValues(Integer[] closing, Integer[] opening) {
    }

    @Override
    protected void defineValues(Integer[] closing, Integer[] opening,  TreeMap<Integer, ShedWorker.Shed> closingSheds, TreeMap<Integer, ShedWorker.Shed> openingSheds) {

        for (int i = 0; i < closing.length; i++) {
            int areaCls, areaOpn;

            areaCls = countHomogenAreaSize(i, closingSheds, true);
            areaOpn = countHomogenAreaSize(i, openingSheds, false);
            if (areaOpn > 0 && areaCls >= areaOpn) {
                values.add(i, closing[i]);
            } else if (areaOpn > 0) {
                values.add(i, opening[i]);
            } else if (areaCls > 0) {
                values.add(i, closing[i]);
            } else {
                values.add(i, Math.min(closing[i], opening[i]));
            }
        }
    }

    private int countHomogenAreaSize(Integer p, TreeMap<Integer, ShedWorker.Shed> sheds, boolean prev) {
        int count;
        Integer label;
        if(prev) {
            label = DataCollection.INSTANCE.getPrevShedLabel(p);
        } else {
            label = DataCollection.INSTANCE.getShedLabel(p);
        }

        count =  sheds.get(label).size();
        return count;
    }


}
