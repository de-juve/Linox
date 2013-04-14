package image;

import workers.PixelsMentor;

import java.util.ArrayList;
import java.util.Collections;

public class ImageOperationSmart extends ImageOperation {

    @Override
    protected void defineValues(Integer[] closing, Integer[] opening) {
        for(int i = 0; i < closing.length; i++) {
            ArrayList<Integer> neighbours = PixelsMentor.defineNeighboursIds(i, width, height);
            Collections.sort(neighbours);
            int areaCls = homogenAreaSize(i, neighbours, closing);
            int areaOpn = homogenAreaSize(i, neighbours, opening);
            if(areaOpn > 0 && areaCls >= areaOpn) {
                values.add(i, closing[i]);
            } else if(areaOpn > 0) {
                values.add(i, opening[i]);
            } else if(areaCls > 0) {
                values.add(i, closing[i]);
            } else {
                values.add(i, Math.min(closing[i], opening[i]));
            }
        }
    }

    private int homogenAreaSize(Integer p, ArrayList<Integer> neighbours, Integer[] values) {
        int count = 0;
        for(Integer n : neighbours) {
            if(values[p].equals(values[n])) {
                count++;
            }
        }
        return count;
    }
}
