package graph;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 23.04.12
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public class Node {
        public int market;
        public int id;
        public boolean isChecked;
        public Node parentNode;

    public int getMarket() {
        return market;
    }

    public void setMarket(int market) {
        this.market = market;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public Node(int market, boolean isChecked, int id)
        {
            this.market = market;
            this.isChecked = isChecked;
            this.id = id;
            parentNode = new Node();
        }

        public Node()
        {
        }
}
