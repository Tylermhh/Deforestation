import processing.core.PImage;

import java.util.List;

public class Stump extends Entity {

    public Stump(
            String id,
            Point position,
            List<PImage> images)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

}
