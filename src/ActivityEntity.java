public abstract class ActivityEntity extends AnimationEntity {

    int actionPeriod;

    abstract void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    int getActionPeriod(){
        return this.actionPeriod;
    };


    @Override
    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                Functions.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
        scheduler.scheduleEvent(this,
                Functions.createAnimationAction(this, 0),
                this.getAnimationPeriod());
    }
}
