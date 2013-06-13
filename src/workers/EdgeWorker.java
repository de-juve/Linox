package workers;

import java.util.TreeMap;

public class EdgeWorker {
    private volatile static EdgeWorker worker;
    private TreeMap<Integer, Edge> edges;
    private int label;

    private EdgeWorker() {
        edges = new TreeMap<>();
        label = -1;
    }

    public static EdgeWorker getInstance() {
        if (worker == null) {
            synchronized (EdgeWorker.class) {
                if (worker == null) {
                    worker = new EdgeWorker();
                }
            }
        }
        return worker;
    }

    public void addEdge(int nodeLabel1, int nodeLabel2) {
        label++;
        edges.put(label, new Edge(nodeLabel1, nodeLabel2));
    }

    public TreeMap<Integer, Edge> getEdges() {
        return edges;
    }

    public int getLabel() {
        return label;
    }

    public void clear() {
        edges = new TreeMap<>();
        label = -1;
    }

    public class Edge {
        private int nodeLabel1;
        private int nodeLabel2;

        public int getNodeLabel1() {
            return nodeLabel1;
        }

        public int getNodeLabel2() {
            return nodeLabel2;
        }

        public Edge(int nodeLabel1, int nodeLabel2) {
            this.nodeLabel1 = nodeLabel1;
            this.nodeLabel2 = nodeLabel2;
        }
    }
}
