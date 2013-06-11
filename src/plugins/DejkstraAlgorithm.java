package plugins;
//don't useage!

import graph.Edge;
import graph.Node;

import java.util.ArrayList;
import java.util.HashSet;

public class DejkstraAlgorithm {
    public Node[] nodes;
    public Edge[] edges;
    public Node begin;

    public Node[] getNodes() {
        return nodes;
    }

    public Edge[] getEdges() {
        return edges;
    }

    public Node getBegin() {
        return begin;
    }

    public DejkstraAlgorithm(Node[] _nodes, Edge[] _edges) {
        nodes = _nodes;
        edges = _edges;
    }

    public void run(Node begin) {
        if (nodes.length == 0 || edges.length == 0) {
            System.out.println("Массивы вершин и ребер не заданы");
        }
        this.begin = begin;
        oneStep(begin);

        for (Node node : nodes) {
            Node another = getAnotherUncheckedNode();
            if (another != null) {
                oneStep(another);
            } else
                break;
        }
    }

    public void oneStep(Node beginNode) {
        for (Node n : getNeighboures(beginNode)) {
            if (!n.isChecked()) {   //не отмечена
                int market = beginNode.getMarket() + getEdge(n, beginNode).getWeight();
                if (n.getMarket() > market) {
                    n.setMarket(market);
                    n.setParentNode(beginNode);
                }
            }
        }
        beginNode.setChecked(true); //вычеркиваем
    }

    // Поиск соседей для вершины. Для неориентированного графа ищутся все соседи.
    private ArrayList<Node> getNeighboures(Node node) {
        ArrayList<Node> firstpoint = new ArrayList<Node>();
        for (Edge edge : edges) {
            if (edge.getStart() == node)
                firstpoint.add(edge.getEnd());
        }
        ArrayList<Node> secondpoints = new ArrayList<Node>();
        for (Edge edge : edges) {
            if (edge.getEnd() == node)
                secondpoints.add(edge.getStart());
        }
        ArrayList<Node> totalpoints = new ArrayList<Node>();
        totalpoints.addAll(firstpoint);
        totalpoints.addAll(secondpoints);

        //IEnumerable<Node> firstpoints = from ff in edges where ff.Start == currpoint select ff.End;
        //IEnumerable<Node> secondpoints = from sp in edges where sp.End == currpoint select sp.Start;
        //IEnumerable<Node> totalpoints = firstpoints.Concat<Node>(secondpoints);
        //return totalpoints;

        if (totalpoints.size() > 1) {
            HashSet<Node> hs = new HashSet<Node>();
            hs.addAll(totalpoints);
            totalpoints.clear();
            totalpoints.addAll(hs);
        }
        return totalpoints;
    }

    // Получаем ребро, соединяющее 2 входные точки
    private Edge getEdge(Node a, Node b) {
        ArrayList<Edge> ed = new ArrayList<Edge>();
        for (Edge edge : edges) {
            if ((edge.getStart() == a && edge.getEnd() == b) || (edge.getEnd() == a && edge.getStart() == b))
                ed.add(edge);
        }

        if (ed.size() > 2 || ed.size() == 0) {
            try {
                throw new Exception("Не найдено ребро между соседями!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ed.get(0);
    }

    // Получаем очередную неотмеченную вершину, "ближайшую" к заданной.
    private Node getAnotherUncheckedNode() {
        ArrayList<Node> uncheckedNodes = new ArrayList<Node>();
        for (Node node : nodes) {
            if (!node.isChecked) {
                uncheckedNodes.add(node);
            }
        }

        if (uncheckedNodes.size() == 0)
            return null;

        Node minN = uncheckedNodes.get(0);
        for (Node node : uncheckedNodes) {
            if (node.getMarket() < minN.getMarket())
                minN = node;
        }
        return minN;
    }

    public ArrayList<Node> minPath(Node begin, Node end) {
        ArrayList<Node> nodes = new ArrayList<Node>();

        Node node = new Node();
        node = end;
        while (node != begin) {
            nodes.add(node);
            node = node.getParentNode();
        }
        return nodes;

    }
}
