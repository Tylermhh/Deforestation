import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Demon extends ActivityEntity implements MotionEntity{

    public Demon(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        ArrayList<Entity> kinds = new ArrayList<>();
        kinds.add(new Fairy(null, null, null, 0, 0));
        Optional<Entity> demonTarget =
                world.findNearest(this.position, kinds);

        if (demonTarget.isPresent()) {

            Point tgtPos = demonTarget.get().getPosition();

            if (this.moveTo(world, demonTarget.get(), scheduler)) {
                EvilFairy evilFairy = new EvilFairy("evil_" + demonTarget.get().getId(),
                        tgtPos,
                        imageStore.getImageList(Functions.EVILFAIRY_KEY),
                        200, 51);

                world.addEntity(evilFairy);
                evilFairy.scheduleActions(scheduler, world, imageStore);
            }
        }

        else{
            kinds.add(new Summon(null, null, null, 0));

            if (this.moveTo(world, world.findNearest(this.position, kinds).get(), scheduler)) {
                scheduler.unscheduleAllEvents(this);
                world.removeEntity(this);
            }
    }

        scheduler.scheduleEvent(this,
                Functions.createActivityAction(this, world, imageStore),
                this.actionPeriod);
    }


    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.position.adjacent(target.getPosition())) {
            if (target.getClass() == new Fairy(null, null, null, 0,0).getClass()) {
                world.removeEntity(target);
                scheduler.unscheduleAllEvents(target);
                VirtualWorld.fairy_count--;
            }
            return true;
        }
        else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.position.equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    public Point nextPosition(WorldModel world, Point destPos)
    {

        PathingStrategy search = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = p -> world.withinBounds(p) && !(world.isOccupied(p));
        BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);

        List<Point> path = search.computePath(this.position, destPos, canPassThrough, withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (!(path.isEmpty()))
            return path.get(0);

        return this.position;
    }
}
