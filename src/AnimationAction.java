public class AnimationAction extends Action{
    private int repeatCount;

    public AnimationAction (Entity entity,
                           WorldModel world,
                           ImageStore imageStore,
                           int repeatCount){
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = repeatCount;
    }

    @Override
    public void executeAction(EventScheduler scheduler){
        this.executeAnimationAction(scheduler);
    }

    public void executeAnimationAction(EventScheduler scheduler) {
        if (this.entity instanceof AnimationEntity) {
            ((AnimationEntity)this.entity).nextImage();

            if (this.repeatCount != 1) {
                scheduler.scheduleEvent(this.entity,
                        Functions.createAnimationAction(this.entity,
                                Math.max(this.repeatCount - 1,
                                        0)),
                        ((AnimationEntity)this.entity).getAnimationPeriod());
            }
        }
    }
}
