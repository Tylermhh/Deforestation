import processing.core.PImage;

import java.util.List;

public abstract class Entity {
    String id;
    Point position;
    List<PImage> images;
    int imageIndex;

    public String getId(){
        return this.id;
    };

    public Point getPosition(){
        return this.position;
    };

    public PImage getCurrentImage(){
        return this.images.get(this.imageIndex);
    };

    public void setPosition(Point p){
        this.position = p;
    };

    public void setImages(List<PImage> newImages){
        this.images = newImages;
    }
}
