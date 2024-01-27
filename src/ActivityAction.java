public class ActivityAction extends Action{

    public ActivityAction (Entity entity,
                           WorldModel world,
                           ImageStore imageStore){
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
    }

    @Override
    public void executeAction(EventScheduler scheduler){
        this.executeActivityAction(scheduler);
    }

    public void executeActivityAction(EventScheduler scheduler) {
        if (this.entity instanceof  ActivityEntity) {
            ((ActivityEntity)this.entity).executeActivity(this.world, this.imageStore, scheduler);
        }
    }
}
