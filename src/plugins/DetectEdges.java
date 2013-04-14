package plugins;

import ij.ImagePlus;

public class DetectEdges extends MyAPlugin{

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            result = new ImagePlus("findEdges", imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }
        return result;
    }


    public void run() {
        imageProcessor.findEdges();
    }

}