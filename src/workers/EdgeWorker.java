package workers;

import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 20.04.12
 * Time: 16:33
 * To change this template use File | Settings | File Templates.
 */
public class EdgeWorker {
    private volatile static EdgeWorker worker;
    private TreeMap<Integer, Edge> edges;
    private int label;

    private EdgeWorker() {
        edges = new TreeMap<Integer, Edge>();
        label = 0;
    }

    public static EdgeWorker getInstance() {
        if(worker == null) {
            synchronized (EdgeWorker.class) {
                if(worker == null) {
                    worker = new EdgeWorker();
                }
            }
        }
        return worker;
    }

    public void addEdge(NodeWorker.Node start, NodeWorker.Node end) {
        edges.put(label, new Edge(start, end));
        label++;
    }

    public void addEdge(int start, int end) {
        edges.put(label, new Edge( NodeWorker.getInstance().getNode(start), NodeWorker.getInstance().getNode(end)));
        label++;
    }

    public TreeMap<Integer, Edge> getEdges() {
        return edges;
    }

    public void clear() {
        edges = new TreeMap<Integer, Edge>();
        label = 0;
    }

    public class Edge {
        private NodeWorker.Node start;

        public NodeWorker.Node getStart() {
            return start;
        }

        public NodeWorker.Node getEnd() {
            return end;
        }

        private NodeWorker.Node end;

        public void setStart(NodeWorker.Node start) {
            this.start = start;
        }

        public void setEnd(NodeWorker.Node end) {
            this.end = end;
        }

        public Edge(NodeWorker.Node start, NodeWorker.Node end)
        {
            this.start = start;
            this.end = end;
        }
    }
}
