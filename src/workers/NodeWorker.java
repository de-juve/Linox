package workers;

import plugins.DataCollection;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeMap;

public class NodeWorker {
    private volatile static NodeWorker worker;
    private TreeMap<Integer, Node> nodes;
    private int label;

    private NodeWorker() {
        nodes = new TreeMap<>();
        label = 0;
    }

    public static NodeWorker getInstance() {
        if (worker == null) {
            synchronized (NodeWorker.class) {
                if (worker == null) {
                    worker = new NodeWorker();
                }
            }
        }
        return worker;
    }

    public void unionNodes(Integer lp, Integer ln) {
        if (!nodes.containsKey(lp) || !nodes.containsKey(ln)) {
            return;
        }

        Node n1 = nodes.get(lp);
        Node n2 = nodes.get(ln);

        for (Integer elm : n2.getElements()) {
            n1.addElement(elm);
        }
        n2.clear();
        nodes.remove(ln);
    }

    public Node newNode(Random random) {
        nodes.put(label, new Node(label, random));
        label++;
        return nodes.get(label - 1);
    }

    public boolean containNodeLabel(int label) {
        return nodes.containsKey(label);
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public Node getNodeByLabel(int label) {
        return nodes.get(label);
    }

    public TreeMap<Integer, Node> getNodes() {
        return nodes;
    }

    public void clear() {
        nodes = new TreeMap<>();
        label = 0;
    }

    public void removeNode(int label) {
        nodes.get(label).clear();
        nodes.remove(label);
    }

    public void sortNodesElements(int width, int height) {
        for (Integer label : nodes.keySet()) {
            sortNodeElements(label, width, height);
        }
    }

    public void sortNodeElements(int label, int width, int height) {
        nodes.get(label).sort(width, height);
    }

    public class Node {
        private ArrayList<Integer> elements;
        private ArrayList<Integer> connectedNodes;
        private LinkedList<Integer> line;
        private int label;
        private Integer canonical;
        private Color color;
        private double meanCurvature;
        private double varianceCurvature;
        private double meanPotential;
        private double meanVariationPotential;
        private double meanContrast;
        private Integer start = -1;
        private Integer end = -1;

        public Node(int label, Random rand) {
            this.label = label;
           // Random rand;
            //rand = label > 0 ? new Random(255 / label) : new Random(255);
            color = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());//new Color(label * rand.nextInt());
            elements = new ArrayList<>();
            canonical = -1;
            connectedNodes = new ArrayList<>();
        }


        public int getStart() {

            return start;
        }

        public int getEnd() {
            return end;
        }

        public ArrayList<Integer> getElements() {
            return elements;
        }

        public LinkedList<Integer> getLine() {
            return line;
        }

        public Color getColor() {
            return color;
        }

        public int getLabel() {
            return label;
        }

        public int getSizeElements() {
            return elements.size();
        }

        public double getMeanCurvature() {
            return meanCurvature;
        }

        public double getVarianceCurvature() {
            return varianceCurvature;
        }

        public double getMeanPotential() {
            return meanPotential;
        }

        public double getMeanVariationPotential() {
            return meanVariationPotential;
        }

        public double getMeanContrast() {
            return meanContrast;
        }



        public void setStart(Integer start) {
            this.start = start;
        }

        public void setEnd(Integer end) {
            this.end = end;
        }

        public void setMeanContrast(double meanContrast) {
            this.meanContrast = meanContrast;
        }

        public void addElement(Integer pixel) {
            elements.add(pixel);
        }

        public void clear() {
            elements.clear();
            label = -1;
            canonical = -1;
        }

        public boolean contain(Integer pixel) {
            return elements.contains(pixel);
        }

        public boolean connectedTo(int nodeLabel) {
            return connectedNodes.contains(nodeLabel);
        }

        public void connectTo(int nodeLabel) {
            connectedNodes.add(nodeLabel);
        }

        public boolean isEmptyConnectedNodes() {
            return connectedNodes.isEmpty();
        }

        public void sort(int width, int height) {
            int countNeighboures;
            C:
            {
                for (int i = 0; i < elements.size(); i++) {
                    countNeighboures = 0;
                    for (int j = i + 1; j < elements.size(); j++) {
                        if (PixelsMentor.isNeighboures(i, j, width)) {
                            countNeighboures++;
                        }
                    }
                    if (countNeighboures == 1 || (countNeighboures == 0 && elements.size() == 1)) {
                        start = elements.get(i);
                        break C;
                    }
                }
            }
            line = new LinkedList<>();
            line.add(start);
            int id = start;
            int next = id;
            ArrayList<Integer> neighbours;
            while (true) {
                neighbours = PixelsMentor.defineNeighboursIds(id, width, height);
                for (Integer neighbour : neighbours) {
                    if (neighbour != id && elements.contains(neighbour) && !line.contains(neighbour)) {
                        line.add(neighbour);
                        next = neighbour;
                        break;
                    }
                }
                if (next == id) {
                    break;
                } else {
                    id = next;
                }
            }
            end = id;
        }

        public void countElementsCurvature(int width) {
            meanCurvature = 0;
            if (elements.size() > 2) {
                CurvatureCalculator calc = CurvatureCalculator.getInstance();
                for (int i = 1; i < elements.size() - 1; i++) {
                    double c = calc.countCurvature(elements.get(i), elements.get(i - 1), elements.get(i + 1), width);
                    meanCurvature += c;
                }
                meanCurvature /= (elements.size() - 2);
            } else {
                meanCurvature = 0;
                CurvatureCalculator calc = CurvatureCalculator.getInstance();
                for (Integer element : elements) {
                    calc.setZeroCurvature(element);
                }
            }
        }

        public void countVarianceCurvature() {
            varianceCurvature = 0;
            if (elements.size() > 2) {
                CurvatureCalculator calc = CurvatureCalculator.getInstance();
                for (int i = 1; i < elements.size() - 1; i++) {
                    double c = calc.getCurvature().get(elements.get(i));
                    varianceCurvature += Math.pow(meanCurvature - c, 2);
                }
                varianceCurvature /= (elements.size() - 2);
            } else {
                varianceCurvature = 0;
            }
        }


        public void countMeanAndMeanVariationPotential() {
            for (Integer i : elements) {
                meanPotential += DataCollection.INSTANCE.getLuminance(i);
            }
            meanPotential /= elements.size();
            if (elements.size() > 1) {
                for (int i = 1; i < elements.size(); i++) {
                    meanVariationPotential += Math.abs(DataCollection.INSTANCE.getLuminance(elements.get(i)) - DataCollection.INSTANCE.getLuminance(elements.get(i - 1)));
                }
                meanVariationPotential /= (elements.size() - 1);
            } else {
                meanVariationPotential = DataCollection.INSTANCE.getLuminance(elements.get(0));
            }
        }

        public double countMeanContrast(int p, ArrayList<Integer> neigh, Integer[] luminance) {
            ContrastCalculator calc = ContrastCalculator.getInstance();
            return calc.countContrast(p, neigh, luminance);
        }
    }
}
