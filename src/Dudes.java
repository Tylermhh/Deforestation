import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public abstract class Dudes extends ActivityEntity implements HealthEntity, MotionEntity{

    int resourceCount;
    int health;

    @Override
    public Point nextPosition(WorldModel world, Point destPos)
    {

        PathingStrategy search = new AStarPathingStrategy();
        Stump stump = new Stump(null, null, null);

        Predicate<Point> canPassThrough = p -> world.withinBounds(p) &&
                (!(world.isOccupied(p)) || world.getOccupancyCell(p).getClass() == stump.getClass());

        BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);

        List<Point> path = search.computePath(this.position, destPos, canPassThrough, withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);

        if(!(path.isEmpty()))
            return path.get(0);

        return this.position;
    }

    public boolean transformDead(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        Entity tomb = world.createObstacle(this.id + "'s grave",
                this.position,
                0,
                imageStore.getImageList("tomb"));

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(tomb);

        return true;
    }

    @Override
    public void setHealth(int c) {
        this.health += c;
    }

    @Override
    public int getHealth() {
        return this.health;
    }

    public void boost(){
        this.actionPeriod = 50;
        this.animationPeriod = 4;
    }

}
