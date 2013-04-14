package graph;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 23.04.12
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class Edge {
    public int weight;
    public Node start;
    public Node end;

    public Edge(Node start, Node end, int weight)
    {
        this.start = start;
        this.end = end;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public Node getStart() {
        return start;
    }

    public Node getEnd() {
        return end;
    }
}
