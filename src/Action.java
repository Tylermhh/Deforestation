public abstract class Action {

    Entity entity;
    WorldModel world;
    ImageStore imageStore;

    abstract void executeAction(EventScheduler scheduler);
}
