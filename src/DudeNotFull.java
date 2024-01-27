import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DudeNotFull extends Dudes{

    private int resourceLimit;

    public DudeNotFull(
            String id,
            Point position,
            List<PImage> images,
            int health,
            int resourceLimit,
            int resourceCount,
            int actionPeriod,
            int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.health = health;
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {

        if (this.health <= 10){
            boost();
        }

        if (this.health <= 0){
            transformDead(world, scheduler, imageStore);
        }

        ArrayList<Entity> kinds = new ArrayList<>();
        kinds.add(new Tree(null, null, null, 0, 0,0));
        kinds.add(new Sapling(null, null, null, 0, 0,0,0));
        Optional<Entity> target =
                world.findNearest(this.position, kinds);

        if (!target.isPresent() || !this.moveTo(world,
                target.get(),
                scheduler)
                || !transform(world, scheduler, imageStore))
        {
            scheduler.scheduleEvent(this,
                    Functions.createActivityAction(this, world, imageStore),
                    this.actionPeriod);
        }
    }

    public boolean transform(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {

        if (this.resourceCount >= this.resourceLimit) {
            DudeFull dudeFull = world.createDudeFull(this.id,
                    this.position, this.health,
                    this.actionPeriod,
                    this.animationPeriod,
                    this.resourceLimit,
                    this.images);

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(dudeFull);
            dudeFull.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (this.position.adjacent(target.getPosition())) {
            this.resourceCount += 1;
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
}
