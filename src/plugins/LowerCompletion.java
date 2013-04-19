package plugins;

import ij.ImagePlus;
import workers.PixelsMentor;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class LowerCompletion extends MyAPlugin {
    Queue<Integer> queue = new LinkedList<> ();
    int distination;

    public LowerCompletion() {
        title = "Lower completion";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            //MassiveWorker w = new MassiveWorker();
            //w.scale(DataCollection.INSTANCE.getLowerCompletions());

            create(imageProcessor, DataCollection.INSTANCE.getLowerCompletions());
            result = new ImagePlus("lower completion " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);
            if(addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }

        return result;
    }

    @Override
    public void run() {
        DataCollection.INSTANCE.newLowerCompletions(width * height);

        FirstStage();
        //SecondStage();
    }

    private void FirstStage() {
        //ImageStack stack = new ImageStack(width, height);
        //ImageProcessor ip = imageProcessor.duplicate();
        Color[] colors = new Color[width*height];
        for(int i = 0; i < colors.length; i++) {
            colors[i] = Color.BLACK;
        }

        InitQueue();

        distination = 1;
        queue.add(-1);
        while (!queue.isEmpty())
        {
            int id = queue.remove();
            if (id == -1 && queue.size() > 0)
            {
                //create(ip, colors);
                //stack.addSlice(ip.duplicate());

                queue.add(-1);
                distination++;
            }
            else if(id > -1)
            {
                colors[id] = Color.WHITE;
                DataCollection.INSTANCE.setLowerCompletion(id, distination);
                ArrayList<Integer> neighbs = PixelsMentor.defineNeighboursIdsWithSameValueLuminance(id, imageProcessor);
                for(Integer nid : neighbs) {
                    if(DataCollection.INSTANCE.getLowerCompletion(nid) == 0) {
                        queue.add(nid);
                        DataCollection.INSTANCE.setLowerCompletion(nid, -1);
                    }
                }
            }
        }
        //create(ip, colors);
        //stack.addSlice(ip.duplicate());
        //new ImagePlus("Results", stack).show();
    }

    private void SecondStage() {
        //Put the lower complete values in the output image);
        for(int i = 0; i < width*height; i++) {
            int low = 0;
            //int low =  DataCollection.INSTANCE.getLuminance(i);
            if(DataCollection.INSTANCE.getLowerCompletion(i) != 0) {
               // low = distination *  DataCollection.INSTANCE.getLuminance(i) +  DataCollection.INSTANCE.getLowerCompletion(i) -1;
                low = 255*(DataCollection.INSTANCE.getLuminance(i) *  DataCollection.INSTANCE.getLowerCompletion(i))/(255*distination);
                //low = DataCollection.INSTANCE.getLowerCompletion(i);// * DataCollection.INSTANCE.getLuminance(i);
            }
            DataCollection.INSTANCE.setLowerCompletion(i, low);
        }
    }

    private void InitQueue() {
        LuminanceCalculator luminanceCalculatorPlugin = new LuminanceCalculator();
        luminanceCalculatorPlugin.initProcessor(imageProcessor);
        luminanceCalculatorPlugin.run();

        for(int i = 0; i < width*height; i++) {
            DataCollection.INSTANCE.setLowerCompletion(i, 0);

            ArrayList<Integer> neighbs = PixelsMentor.defineNeighboursIds(i, imageProcessor);
            for(Integer n : neighbs) {
                if(DataCollection.INSTANCE.getLuminance(n) < DataCollection.INSTANCE.getLuminance(i)) {
                    DataCollection.INSTANCE.setLowerCompletion(i, -1);
                    queue.add(i);
                    break;
                }
            }
        }
    }
}
