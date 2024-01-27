public class Node implements Comparable<Node>{
    private Point position;
    private Node prev;
    private int distFromStart;
    private int estDistToEnd;

    public Node(int dS, int dE, Node n, Point p){
        this.position = p;
        this.prev = n;
        this.distFromStart = dS;
        this.estDistToEnd = dE;
    }

    public Point getPosition(){
        return this.position;
    }

    public Node getPrev(){
        return this.prev;
    }

    public int getDistFromStart(){
        return distFromStart;
    }

    public int getEstDistToEnd(){
        return estDistToEnd;
    }

    @Override
    public int compareTo(Node o) {
        int thisTotal = this.distFromStart + this.estDistToEnd;
        int otherTotal = o.distFromStart + o.estDistToEnd;

        if (thisTotal > otherTotal)
            return 1;

        else if (thisTotal < otherTotal)
            return -1;

        return 0;
    }
}
