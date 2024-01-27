public abstract class AnimationEntity extends Entity {
    int animationPeriod;

    int getAnimationPeriod(){
        return this.animationPeriod;
    };

    void nextImage(){
        this.imageIndex = (this.imageIndex + 1) % this.images.size();
    };

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore)
    {
        scheduler.scheduleEvent(this,
                Functions.createAnimationAction(this, 0),
                this.getAnimationPeriod());
    }
}
