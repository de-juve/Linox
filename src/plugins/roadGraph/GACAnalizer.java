package plugins.roadGraph;

import ij.ImagePlus;
import plugins.DataCollection;
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

        destroySingleNodes();
        destroyOneElementNodes();

        paintNodes();
    }

    private void destroySingleNodes() {
        TreeMap<Integer, NodeWorker.Node> nodes  = (TreeMap<Integer, NodeWorker.Node>) NodeWorker.getInstance().getNodes().clone();
        System.out.println(NodeWorker.getInstance().getNodes().size());

        for(NodeWorker.Node node : nodes.values()) {
            if(node.isEmptyConnectedNodes()) {
                NodeWorker.getInstance().removeNode(node.getLabel());
            }
        }

        System.out.println(NodeWorker.getInstance().getNodes().size());
    }

    private void destroyOneElementNodes() {
        TreeMap<Integer, NodeWorker.Node> nodes  = (TreeMap<Integer, NodeWorker.Node>) NodeWorker.getInstance().getNodes().clone();
        System.out.println(NodeWorker.getInstance().getNodes().size());
        for(NodeWorker.Node node : nodes.values()) {
            if(node.getSizeElements() == 1) {
                System.out.println("remove");
                NodeWorker.getInstance().removeNode(node.getLabel());
            }
        }
        System.out.println(NodeWorker.getInstance().getNodes().size());
    }

    private void paintNodes() {
        for (NodeWorker.Node n : NodeWorker.getInstance().getNodes().values()) {
            for (Integer element : n.getElements()) {
                imageProcessor.set(element, n.getColor().getRGB());
            }
        }
    }
}
