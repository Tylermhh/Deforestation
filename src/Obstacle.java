import processing.core.PImage;

import java.util.List;

public class Obstacle extends AnimationEntity{


    public Obstacle(
            String id,
            Point position,
            List<PImage> images,
            int animationPeriod)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.animationPeriod = animationPeriod;
    }


}
