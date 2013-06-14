package plugins.roadGraph;

import ij.ImagePlus;
import plugins.DataCollection;
import plugins.LuminanceCalculator;
import plugins.MyAPlugin;
import road.Road;
import road.RoadPoint;
import workers.EdgeWorker;
import workers.NodeWorker;
import workers.PixelsMentor;

import java.util.ArrayList;

public class RoadBuilder extends MyAPlugin {
    boolean[] analized;
    Integer[] colors;

    public RoadBuilder() {
        title = "Road builder";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if (result == null) {
            result = new ImagePlus("Road builder result " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);

            if (addToStack) {
                DataCollection.INSTANCE.addtoHistory(result);
            }
        }
        return result;
    }

    @Override
    public void run() {
        if(NodeWorker.getInstance().isEmpty()) {
            setErrMessage("Empty GAC");
            exit = true;
            if (exit) {
                return;
            }
        }

        int sourceNodeLabel = defineSourceNode();
        if(sourceNodeLabel < 0) {
            setErrMessage("Not found source node");
            exit = true;
            if (exit) {
                return;
            }
        }

        NodeWorker.Node sourceNode = NodeWorker.getInstance().getNodeByLabel(sourceNodeLabel);

        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

        analized = new boolean[DataCollection.INSTANCE.getLuminances().length];
        colors = new Integer[analized.length];

        for(int i = 0; i < analized.length; i++) {
            analized[i] = false;
            colors[i] = DataCollection.INSTANCE.getLuminance(i);
        }

        for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
            for(Integer el : node.getElements()) {
               // if(!analized[el]) {
                    analized[el] = true;
                    colors[el] = 255;
                    fill(el);
                //}
            }
        }

        create(imageProcessor, colors);
    }

    private void fill(Integer pixel) {
        ArrayList<Integer> neigh = PixelsMentor.defineNeighboursIds(pixel, width, height);
        for(Integer n : neigh) {
            if(!analized[n]) {
                analized[n] = true;
                if(Math.abs(DataCollection.INSTANCE.getLuminance(n) - DataCollection.INSTANCE.getLuminance(pixel)) < 1) {
                    colors[n] = 255;
                    fill(n);
                } else {
                    colors[n] = DataCollection.INSTANCE.getLuminance(n);
                }
            }
        }
    }

    private int defineSourceNode() {
        for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
            if(node.isSourceNode()) {
                return node.getLabel();
            }
        }
        return -1;
    }
}
