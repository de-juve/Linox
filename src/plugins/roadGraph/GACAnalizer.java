package plugins.roadGraph;

import ij.ImagePlus;
import plugins.DataCollection;
import plugins.LuminanceCalculator;
import plugins.MyAPlugin;
import workers.NodeWorker;

import java.util.TreeMap;

public class GACAnalizer extends MyAPlugin {

    public GACAnalizer() {
        title = "GAC analyzer";
    }

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if (result == null) {
            result = new ImagePlus("gac analizer result " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);

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

        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

        destroySingleNodes();
        destroyOneElementNodes();

        destroySinuousNodes();

        destroyDarkNodes();

        paintNodes();
    }

    private void destroySingleNodes() {
        TreeMap<Integer, NodeWorker.Node> nodes  = (TreeMap<Integer, NodeWorker.Node>) NodeWorker.getInstance().getNodes().clone();
        System.out.println("before destroy single nodes " + NodeWorker.getInstance().getNodes().size());

        for(NodeWorker.Node node : nodes.values()) {
            if(node.isEmptyConnectedNodes()) {
                for(Integer el : node.getElements()) {
                    DataCollection.INSTANCE.removeWatershedPoint(el);
                }
                NodeWorker.getInstance().removeNode(node.getLabel());
            }
        }

        System.out.println("after destroy single nodes " + NodeWorker.getInstance().getNodes().size());
    }

    private void destroyOneElementNodes() {
        TreeMap<Integer, NodeWorker.Node> nodes  = (TreeMap<Integer, NodeWorker.Node>) NodeWorker.getInstance().getNodes().clone();
        System.out.println("before destroy one element nodes " + NodeWorker.getInstance().getNodes().size());
        for(NodeWorker.Node node : nodes.values()) {
            if(node.getSizeElements() == 1) {
                for(Integer el : node.getElements()) {
                    DataCollection.INSTANCE.removeWatershedPoint(el);
                }
                NodeWorker.getInstance().removeNode(node.getLabel());
            }
        }
        System.out.println("after destroy one element nodes " + NodeWorker.getInstance().getNodes().size());
    }

    private void destroySinuousNodes() {
        for (NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
            node.countElementsCurvature(width);
        }

        TreeMap<Integer, NodeWorker.Node> nodes  = (TreeMap<Integer, NodeWorker.Node>) NodeWorker.getInstance().getNodes().clone();
        System.out.println("before destroy sinuous nodes " + NodeWorker.getInstance().getNodes().size());
        for(NodeWorker.Node node : nodes.values()) {
            if(node.getMeanCurvature() >= 1.0) {
                for(Integer el : node.getElements()) {
                    DataCollection.INSTANCE.removeWatershedPoint(el);
                }
                NodeWorker.getInstance().removeNode(node.getLabel());
            }
        }
        System.out.println("after destroy sinuous nodes " + NodeWorker.getInstance().getNodes().size());
    }

    private void destroyDarkNodes() {
        for (NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
            node.countMeanAndMeanVariationPotential();
        }

        TreeMap<Integer, NodeWorker.Node> nodes  = (TreeMap<Integer, NodeWorker.Node>) NodeWorker.getInstance().getNodes().clone();
        System.out.println("before destroy dark nodes " + NodeWorker.getInstance().getNodes().size());
        for(NodeWorker.Node node : nodes.values()) {
            if(node.getMeanPotential() < 100) {
                for(Integer el : node.getElements()) {
                    DataCollection.INSTANCE.removeWatershedPoint(el);
                }
                NodeWorker.getInstance().removeNode(node.getLabel());
            }
        }
        System.out.println("after destroy dark nodes " + NodeWorker.getInstance().getNodes().size());
    }

    private void paintNodes() {
        for (NodeWorker.Node n : NodeWorker.getInstance().getNodes().values()) {
            for (Integer element : n.getElements()) {
                imageProcessor.set(element, n.getColor().getRGB());
            }
        }
    }
}
