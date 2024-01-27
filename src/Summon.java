import processing.core.PImage;

import java.util.List;

public class Summon extends ActivityEntity{

    private int delay;

    public Summon (String id,
                   Point position,
                   List<PImage> images,
                   int animationPeriod){

        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.animationPeriod = animationPeriod;
        this.delay = 40;
    }

    public void refreshDelay(){
        this.delay = 40;
    }


    @Override
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (this.delay <= 0){

            if (VirtualWorld.fairy_count !=0) {
                Background crater = new Background("crater" + this.position, imageStore.getImageList("crater"));
                world.setBackground(new Point(this.position.getX(), this.position.getY() - 1), crater);


                for (int i = 0; i < 6; i++) {
                    Demon demon = new Demon("Demon #" + VirtualWorld.demon_count,
                            new Point(this.position.getX(), this.position.getY() - 1),
                            imageStore.getImageList(Functions.DEMON_KEY),
                            100, 4);

                    world.addEntity(demon);
                    demon.scheduleActions(scheduler, world, imageStore);
                    VirtualWorld.demon_count++;

                }
                System.out.println("Summoning SUCCESS!");
            }
            else{
                System.out.println("No fairies to corrupt! Summoning FAILED");
            }

            this.refreshDelay();

            scheduler.unscheduleAllEvents(this);

            return;
        }

        this.delay--;

        scheduler.scheduleEvent(this,
                Functions.createActivityAction(this, world, imageStore),
                this.actionPeriod);
    }
}
