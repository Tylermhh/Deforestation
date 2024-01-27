import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class EvilFairy extends ActivityEntity implements MotionEntity{

    public EvilFairy(
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

    @Override
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        ArrayList<Entity> kinds = new ArrayList<>();
        kinds.add(new DudeNotFull(null, null, null, 0, 0, 0, 0, 0));
        kinds.add(new DudeFull(null, null, null, 0, 0, 0, 0, 0));
        Optional<Entity> EvilFairyTarget =
                world.findNearest(this.position, kinds);

        if (EvilFairyTarget.isPresent()) {
            this.moveTo(world, EvilFairyTarget.get(), scheduler);
        }

        else{
            transformGood(world, scheduler, imageStore);
        }

        scheduler.scheduleEvent(this,
                Functions.createActivityAction(this, world, imageStore),
                this.actionPeriod);
    }

    @Override
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.position.adjacent(target.getPosition())) {
            ((HealthEntity)target).setHealth(-1);
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

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy search = new AStarPathingStrategy();

        Predicate<Point> canPassThrough = p -> world.withinBounds(p) && !(world.isOccupied(p));
        BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);

        List<Point> path = search.computePath(this.position, destPos, canPassThrough, withinReach,
                PathingStrategy.CARDINAL_NEIGHBORS);

        if (!(path.isEmpty()))
            return path.get(0);

        return this.position;
    }

    public boolean transformGood(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        Entity fairy = world.createFairy("GOOD_" + this.id,
                this.position,
                51,
                51,
                imageStore.getImageList(Functions.FAIRY_KEY));

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(fairy);
        VirtualWorld.fairy_count++;
        ((ActivityEntity) fairy).scheduleActions(scheduler, world, imageStore);

        return true;
    }
}
