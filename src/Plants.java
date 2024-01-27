public abstract class Plants extends ActivityEntity implements HealthEntity{
    int health;

    public void setHealth(int change){
        this.health += change;
    }

    public int getHealth(){
        return this.health;
    }

    public boolean transformStump(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        Entity stump = world.createStump(this.id,
                this.position,
                imageStore.getImageList(Functions.STUMP_KEY));

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(stump);

        return true;
    }
}
