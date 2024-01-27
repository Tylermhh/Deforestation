import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
public class PathingTest {
    @Test
    public void testSingleStepNoObstacles() {
        boolean[][] grid = {
                {true, true, true},
                {true, true, true},
                {true, true, true}
                // ^^^^ Goal
        };
        Point start = new Point(0, 0);
        Point end = new Point(2, 2);
        PathingStrategy ps = new SingleStepPathingStrategy();
        List<Point> path = ps.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.getY()][p.getX()],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );
        // (0, 1)
        assertEquals(List.of(new Point(0, 1)), path);
    }

    @Test
    public void testAStarWithNoObstacles() {
        boolean[][] grid = {
                {true, true, true},
                {true, true, true},
                {true, true, true}
                          // ^^^^ Goal
        };
        Point start = new Point(0, 0);
        Point end = new Point(2, 2);
        PathingStrategy ps = new AStarPathingStrategy();
        List<Point> path = ps.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.getY()][p.getX()],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertEquals(3, path.size());

        for (int i = 0; i < path.size() - 1; i++){
            assertTrue(path.get(i).adjacent(path.get(i + 1)));
        }

    }

    @Test
    public void testAStarWithOneObstacle() {
        boolean[][] grid = {
                {true, true, true},
                {false, true, true},
                {true, true, true}
        };
        Point start = new Point(0, 0);
        Point end = new Point(2, 2);
        PathingStrategy ps = new AStarPathingStrategy();
        List<Point> path = ps.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.getY()][p.getX()],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );
        assertEquals(3, path.size());

        for (int i = 0; i < path.size() - 1; i++){
            assertTrue(path.get(i).adjacent(path.get(i + 1)));
        }
    }

    public void testAStarGoingAway() {
        boolean[][] grid = {
                {true, true, true, true},
                {true, false, true, true},
                {true, true, false, true},
                {false, false, false, true}, // goal is last col of this row
                {true, true, true, true}
        };
        Point start = new Point(2, 1);
        Point end = new Point(3, 3);
        PathingStrategy ps = new AStarPathingStrategy();
        List<Point> path = ps.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.getY()][p.getX()],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );
        assertEquals(8, path.size());

        for (int i = 0; i < path.size() - 1; i++){
            assertTrue(path.get(i).adjacent(path.get(i + 1)));
        }
    }

    @Test
    public void testAStarWithNoPath() {
        boolean[][] grid = {
                {true, false, true},
                {false, false, true},
                {true, true, true}
        };
        Point start = new Point(0, 0);
        Point end = new Point(2, 2);
        PathingStrategy ps = new AStarPathingStrategy();
        List<Point> path = ps.computePath(
                start, end,
                p -> withinBounds(p, grid) && grid[p.getY()][p.getX()],
                (p1, p2) -> p1.adjacent(p2),
                PathingStrategy.CARDINAL_NEIGHBORS
        );

        assertEquals(0, path.size());
    }

    private static boolean withinBounds(Point p, boolean[][] grid) {
        return p.getY() >= 0 && p.getY() < grid.length &&
                p.getX() >= 0 && p.getX() < grid[0].length;
    }

}