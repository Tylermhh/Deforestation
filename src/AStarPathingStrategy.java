import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AStarPathingStrategy implements PathingStrategy{

    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        /* Does not check withinReach.  Since only a single step is taken
         * on each call, the caller will need to check if the destination
         * has been reached.
         */

        PriorityQueue<Node> openList = new PriorityQueue<>();
        List<Node> openListCpy = new LinkedList<>();
        HashSet<Point> closedList = new HashSet<>();
        List<Point> path = new LinkedList<>();

        Node startNode = new Node(0, manhattan(start, end), null, start);
        boolean reachedEnd = false;

        openList.add(startNode);
        openListCpy.add(startNode);

        while (!openList.isEmpty() && !(reachedEnd))
        {

            Node curr = openList.remove();
            openListCpy.remove(curr);

            if (withinReach.test(curr.getPosition(), end))
            {
                reachedEnd = true;

                while (!(curr.getPosition().equals(start)))
                {
                    path.add(0, curr.getPosition());
                    curr = curr.getPrev();
                }

                return path;
            }

            else{
                closedList.add(curr.getPosition());

                List<Point> adjPoints = potentialNeighbors.apply(curr.getPosition()).filter(canPassThrough)
                        .filter(p -> !closedList.contains(p)).collect(Collectors.toList());

                for (int i = 0; i < adjPoints.size(); i++)
                {

                    boolean unique = true;

                    Point p = adjPoints.get(i);

                    Node neighbour = new Node(curr.getDistFromStart() + 1, manhattan(p, end), curr, p);

                    for (int j = 0; j < openList.size(); j++)
                    {
                        Node n = openListCpy.get(j);

                        if (n.getPosition().equals(p))
                        {

                            unique = false;
                            if ((n.getDistFromStart() + n.getEstDistToEnd()) >
                                    (neighbour.getEstDistToEnd() + neighbour.getDistFromStart()))
                            {
                                openList.remove(n);
                                openListCpy.remove(n);
                                openList.add(neighbour);
                                openListCpy.add(neighbour);
                            }
                        }
                    }

                    if (unique)
                    {
                        openList.add(neighbour);
                        openListCpy.add(neighbour);
                    }
                }
            }
        }
        return path;
    }

    public int manhattan(Point p1, Point p2){
        return Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY());
    }
}
