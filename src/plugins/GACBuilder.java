package plugins;

import gui.Linox;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import plugins.morfology.AreaEqualing;
import workers.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class GACBuilder extends MyAPlugin{

    Queue<Integer> queue;
    ArrayList<Integer> crpoints;
    boolean[] crossPoints;
    boolean[] used;

    @Override
    public ImagePlus getResult(boolean addToStack) {
        if(result == null) {
            for(NodeWorker.Node n : NodeWorker.getInstance().getNodes().values()) {
                if(n.getSizeElements() > 1) {
                    for(Integer element : n.getElements()) {
                        imageProcessor.set(element, n.getColor().getRGB());
                    }
                }
                else {
                    for(Integer element : n.getElements())  {
                        imageProcessor.set(element, Color.BLACK.getRGB());
                    }
                }
            }
            for(Integer cp : crpoints) {
                imageProcessor.set(cp, Color.WHITE.getRGB());
            }

            result = new ImagePlus("gac res " + DataCollection.INSTANCE.getImageOriginal().getTitle(), imageProcessor);

            if(addToStack) {
                ImageProcessor ip = imageProcessor.duplicate();
                Color[] colors =  new Color[DataCollection.INSTANCE.getShedLabels().length];

                for(int i = 0; i < colors.length; i++) {
                    colors[i] = ShedWorker.getInstance().getShedColor(DataCollection.INSTANCE.getShedLabel(i));
                }

                create(ip, colors);
                DataCollection.INSTANCE.addtoHistory(new ImagePlus("sheds area equaling " + DataCollection.INSTANCE.getImageOriginal().getTitle(), ip));

                for(int i = 0 ; i < colors.length; i++) {
                    colors[i] = Color.white;
                }
                for(Integer cp : crpoints) {
                    colors[cp] = Color.red;
                }

                create(ip, colors);
                DataCollection.INSTANCE.addtoHistory(new ImagePlus("gac crpoints colors " + DataCollection.INSTANCE.getImageOriginal().getTitle(), ip));

                DataCollection.INSTANCE.addtoHistory(result);
            }
        }
        return result;
    }


    @Override
    public void run() {

        if(DataCollection.INSTANCE.getWshPoints() == null || DataCollection.INSTANCE.getWshPoints().length != width*height) {
            Watershed2 watershed = new Watershed2();
            watershed.initProcessor(imageProcessor);
            watershed.run();
            exit = watershed.exit;
            if(exit) {
                return;
            }
        }
        int length = DataCollection.INSTANCE.getShedLabels().length;
        DataCollection.INSTANCE.newNodeLabels(length);

        used = new boolean[length];
        crossPoints = new boolean[length];
        crpoints = new ArrayList<>();

        findCrossingPoints();
        createNodes();
        correctNodes();
        createEdges();

        LuminanceCalculator luminanceCalculator = new LuminanceCalculator();
        luminanceCalculator.initProcessor(imageProcessor);
        luminanceCalculator.run();

        LineThickening thickening = new LineThickening();
        thickening.initProcessor(imageProcessor);
        for(NodeWorker.Node node : NodeWorker.getInstance().getNodes().values()) {
            DataCollection.INSTANCE.setLine(node.getLine());
            thickening.run();
        }
        ImagePlus imp = thickening.getResult(true);
        (Linox.getInstance().getImageStore()).addImageTab(imp.getTitle(), imp);

    }

    private void findCrossingPoints() {
        int radiusOfNeighborhood = 2;
        for(Integer i : DataCollection.INSTANCE.getWaterShedPoints()) {
            ArrayList<Integer>  neigh = PixelsMentor.defineNeighboursIds(i, radiusOfNeighborhood, width, height);
            ArrayList<Integer> area = new ArrayList<>();

            for(Integer n : neigh) {
                if(!area.contains(DataCollection.INSTANCE.getShedLabel(n)) && DataCollection.INSTANCE.getWshPoint(n) == 0)
                    area.add(DataCollection.INSTANCE.getShedLabel(n));
            }

            crossPoints[i] = area.size() > 2;
            if(crossPoints[i]) {
                crpoints.add(i);
            }
        }
    }

    private void createNodes() {
        NodeWorker.getInstance().clear();

        boolean[] createNewNode = new boolean[DataCollection.INSTANCE.getShedLabels().length];
        NodeWorker nw = NodeWorker.getInstance();
        queue = new LinkedList<>();
        for(Integer cp : crpoints) {
            used[cp] = false;
        }

        Integer p = getUnUsedWatershedPixel();
        if(p == -1) {
            return;
        }
        createNewNode[p] = true;
        queue.add(p);
        GetterNeighboures getterN = new GetterNeighboures(DataCollection.INSTANCE.getWshPoints());
        while(true) {
            while(queue.size() > 0) {
                p = queue.remove();
                NodeWorker.Node node;

                if(used[p])
                    continue;

                ArrayList<Integer> wneigh =  getterN.getWatershedIds(p, width, height, DataCollection.INSTANCE.getWshPoints(), crossPoints);

                if(createNewNode[p]) {
                    node = nw.newNode();
                    createNewNode[p] = false;
                    node.setStart(p);
                }
                else {
                    int usedNeighboure = getFirstUsedNeighboure(wneigh);
                    node = nw.getNodeByLabel(DataCollection.INSTANCE.getNodeLabel(usedNeighboure));
                }

                DataCollection.INSTANCE.setNodeLabel(p, node.getLabel());
                //nodeLabel[p] = n.getLabel();
                node.addElement(p);
                used[p] = true;

                removeDupesAndSort(wneigh);
                if(wneigh.size() > 0) {
                    if(crossPoints[p]) {
                        //multiple crossing point
                        while(crossPoints[wneigh.get(0)]) {
                            Integer cp = wneigh.get(0);
                            DataCollection.INSTANCE.setNodeLabel(cp, DataCollection.INSTANCE.getNodeLabel(p));
                            // nodeLabel[cp] = nodeLabel[p];
                            node.addElement(cp);
                            used[cp] = true;
                            wneigh.remove(cp);
                            wneigh.addAll(getterN.getWatershedIds(cp, width, height, DataCollection.INSTANCE.getWshPoints(), crossPoints));
                            wneigh.remove(p);
                            removeDupesAndSort(wneigh);
                            if(wneigh.size() == 0)
                                break;
                        }
                    }
                    for(Integer s : wneigh) {
                        if(createNewNode[s])
                            createNewNode[s] = false;
                        if(!used[s])
                            queue.add(s);
                    }
                    if(crossPoints[p]) {
                        node.setEnd(p);
                        for(Integer s : wneigh)
                            createNewNode[s] = true;
                    }
                }
            }
            p = getUnUsedWatershedPixel();
            if( p != -1) {
                createNewNode[p] = true;
                queue.add(p);
            }
            else
                break;
        }
    }

    private Integer getUnUsedWatershedPixel() {
        for(Integer i : DataCollection.INSTANCE.getWaterShedPoints()) {
            if(!used[i]) {
                return i;
            }
        }
        return -1;
    }


    private Integer getFirstUsedNeighboure(ArrayList<Integer> wneigh) {
        for(Integer n : wneigh) {
            if(used[n] && !crossPoints[n])
                return n;
        }
        return -1;
    }

    private void removeDupesAndSort(ArrayList<Integer> wneight) {
        ArrayList<Integer> duplicat = new ArrayList<>();
        for(Integer p : wneight) {
            if(!used[p]) {
                if(crossPoints[p])
                    duplicat.add(0, p);
                else
                    duplicat.add(duplicat.size(), p);
            }
        }
        wneight.clear();
        wneight.addAll(duplicat);
    }



    private void correctNodes() {
        GetterNeighboures getterN = new GetterNeighboures(DataCollection.INSTANCE.getWshPoints());
        for(Integer i : DataCollection.INSTANCE.getWaterShedPoints())  {
            if(!crossPoints[i]) {
                ArrayList<Integer> wn =  getterN.getWatershedIds(i, width, height, DataCollection.INSTANCE.getWshPoints(), crossPoints);
                for(Integer n : wn) {
                    if(!DataCollection.INSTANCE.getNodeLabel(n).equals(DataCollection.INSTANCE.getNodeLabel(i))
                            && !crossPoints[n]) {
                        if(NodeWorker.getInstance().containNodeLabel(DataCollection.INSTANCE.getNodeLabel(n))) {
                            ArrayList<Integer> elms = NodeWorker.getInstance().getNode(DataCollection.INSTANCE.getNodeLabel(n)).getElements();
                            NodeWorker.getInstance().unionNodes(DataCollection.INSTANCE.getNodeLabel(i), DataCollection.INSTANCE.getNodeLabel(n));
                            for(Integer elm : elms) {
                                DataCollection.INSTANCE.setNodeLabel(elm, DataCollection.INSTANCE.getNodeLabel(n));
                            }
                        }
                    }
                }
            }
        }
        NodeWorker.getInstance().sortNodesElements(width, height);
    }


    private void createEdges() {
        EdgeWorker.getInstance().clear();

        for(Integer i : crpoints) {
            GetterNeighboures getterN = new GetterNeighboures(DataCollection.INSTANCE.getWshPoints());
            ArrayList<Integer> wn =  getterN.getWatershedIds(i, width, height, DataCollection.INSTANCE.getWshPoints(), crossPoints);
            for(Integer n : wn) {
                for(Integer m : wn) {
                    if(!DataCollection.INSTANCE.getNodeLabel(n).equals(DataCollection.INSTANCE.getNodeLabel(m))) {
                        EdgeWorker.getInstance().addEdge(DataCollection.INSTANCE.getNodeLabel(n), DataCollection.INSTANCE.getNodeLabel(m));
                    }
                }
            }

        }
    }
}
