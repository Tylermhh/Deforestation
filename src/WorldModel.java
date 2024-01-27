import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel
{
    private int numRows;
    private int numCols;
    private Background background[][];
    private Entity occupancy[][];
    private Set<Entity> entities;

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    public int getNumRows(){
        return this.numRows;
    }

    public int getNumCols(){
        return this.numCols;
    }

    public Set<Entity> getEntities(){
        return this.entities;
    }

    public Background getBackgroundCell(Point pos) {
        return this.background[pos.getY()][pos.getX()];
    }

    public void setBackgroundCell(Point pos, Background background)
    {
        this.background[pos.getY()][pos.getX()] = background;
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }

    public void setOccupancyCell(Point pos, Entity entity)
    {
        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(this.getOccupancyCell(pos));
        }
        else {
            return Optional.empty();
        }
    }

    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && this.getOccupancyCell(pos) != null;
    }

    public Optional<PImage> getBackgroundImage(Point pos)
    {
        if (this.withinBounds(pos)) {
            return Optional.of(this.getBackgroundCell(pos).getCurrentImage());
        }
        else {
            return Optional.empty();
        }
    }

    public void setBackground(Point pos, Background background)
    {
        if (this.withinBounds(pos)) {
            this.setBackgroundCell(pos, background);
        }
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < this.numRows && pos.getX() >= 0
                && pos.getX() < this.numCols;
    }

    public void addEntity(Entity entity) {
        if (this.withinBounds(entity.getPosition())) {
            this.setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

    public void removeEntity(Entity entity) {
        this.removeEntityAt(entity.getPosition());
    }

    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && this.getOccupancyCell(pos) != null) {
            Entity entity = this.getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            this.setOccupancyCell(pos, null);
        }
    }

    public void tryAddEntity(Entity entity) {
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        this.addEntity(entity);
    }

    public Optional<Entity> findNearest(Point pos, List<Entity> kinds)
    {
        List<Entity> ofType = new LinkedList<>();
        for (Entity kind: kinds)
        {
            for (Entity entity : this.entities) {
                if (entity.getClass() == kind.getClass()) {
                    ofType.add(entity);
                }
            }
        }

        return Functions.nearestEntity(ofType, pos);
    }

    public void moveEntity(Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (this.withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            this.removeEntityAt(pos);
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public boolean parseBackground(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.BGND_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.BGND_COL]),
                    Integer.parseInt(properties[Functions.BGND_ROW]));
            String id = properties[Functions.BGND_ID];
            this.setBackground(pt,
                    new Background(id, imageStore.getImageList(id)));
        }

        return properties.length == Functions.BGND_NUM_PROPERTIES;
    }

    public boolean parseSapling(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.SAPLING_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.SAPLING_COL]),
                    Integer.parseInt(properties[Functions.SAPLING_ROW]));
            String id = properties[Functions.SAPLING_ID];
            int health = Integer.parseInt(properties[Functions.SAPLING_HEALTH]);
            Entity entity = new Sapling(id, pt, imageStore.getImageList(
                    Functions.SAPLING_KEY),
                    Functions.SAPLING_ACTION_ANIMATION_PERIOD,
                    Functions.SAPLING_ACTION_ANIMATION_PERIOD, health,
                    Functions.SAPLING_HEALTH_LIMIT);
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.SAPLING_NUM_PROPERTIES;
    }

    public boolean parseDude(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.DUDE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.DUDE_COL]),
                    Integer.parseInt(properties[Functions.DUDE_ROW]));
            Entity entity = this.createDudeNotFull(properties[Functions.DUDE_ID],
                    pt,
                    Integer.parseInt(properties[Functions.DUDE_HEALTH]),
                    Integer.parseInt(properties[Functions.DUDE_ACTION_PERIOD]),
                    Integer.parseInt(properties[Functions.DUDE_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[Functions.DUDE_LIMIT]),
                    imageStore.getImageList(Functions.DUDE_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.DUDE_NUM_PROPERTIES;
    }

    public boolean parseFairy(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.FAIRY_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.FAIRY_COL]),
                    Integer.parseInt(properties[Functions.FAIRY_ROW]));
            Entity entity = this.createFairy(properties[Functions.FAIRY_ID],
                    pt,
                    Integer.parseInt(properties[Functions.FAIRY_ACTION_PERIOD]),
                    Integer.parseInt(properties[Functions.FAIRY_ANIMATION_PERIOD]),
                    imageStore.getImageList(Functions.FAIRY_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.FAIRY_NUM_PROPERTIES;
    }


    public boolean parseTree(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.TREE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.TREE_COL]),
                    Integer.parseInt(properties[Functions.TREE_ROW]));
            Entity entity = this.createTree(properties[Functions.TREE_ID],
                    pt,
                    Integer.parseInt(properties[Functions.TREE_ACTION_PERIOD]),
                    Integer.parseInt(properties[Functions.TREE_ANIMATION_PERIOD]),
                    Integer.parseInt(properties[Functions.TREE_HEALTH]),
                    imageStore.getImageList(Functions.TREE_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.TREE_NUM_PROPERTIES;
    }

    public boolean parseObstacle(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.OBSTACLE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.OBSTACLE_COL]),
                    Integer.parseInt(properties[Functions.OBSTACLE_ROW]));
            Entity entity = this.createObstacle(properties[Functions.OBSTACLE_ID], pt,
                    Integer.parseInt(properties[Functions.OBSTACLE_ANIMATION_PERIOD]),
                    imageStore.getImageList(Functions.OBSTACLE_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.OBSTACLE_NUM_PROPERTIES;
    }

    public boolean parseSummon(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.SUMMON_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.SUMMON_COL]),
                    Integer.parseInt(properties[Functions.SUMMON_ROW]));
            Entity entity = this.createSummon(properties[Functions.SUMMON_ID], pt,
                    Integer.parseInt(properties[Functions.SUMMON_ANIMATION_PERIOD]),
                    imageStore.getImageList(Functions.SUMMON_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.OBSTACLE_NUM_PROPERTIES;
    }

    public boolean parseHouse(
            String[] properties, ImageStore imageStore)
    {
        if (properties.length == Functions.HOUSE_NUM_PROPERTIES) {
            Point pt = new Point(Integer.parseInt(properties[Functions.HOUSE_COL]),
                    Integer.parseInt(properties[Functions.HOUSE_ROW]));
            Entity entity = this.createHouse(properties[Functions.HOUSE_ID], pt,
                    imageStore.getImageList(Functions.HOUSE_KEY));
            this.tryAddEntity(entity);
        }

        return properties.length == Functions.HOUSE_NUM_PROPERTIES;
    }

    private House createHouse(
            String id, Point position, List<PImage> images)
    {
        return new House(id, position, images);
    }

    public Obstacle createObstacle(
            String id, Point position, int animationPeriod, List<PImage> images) {
        return new Obstacle(id, position, images,
                animationPeriod);
    }

    public Summon createSummon(
            String id, Point position, int animationPeriod, List<PImage> images) {
        return new Summon(id, position, images, animationPeriod);
    }

    public static Tree createTree(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            int health,
            List<PImage> images)
    {
        return new Tree(id, position, images,
                actionPeriod, animationPeriod, health);
    }

    public Stump createStump(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Stump(id, position, images);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public Sapling createSapling(
            String id,
            Point position,
            List<PImage> images)
    {
        return new Sapling(id, position, images,
                Functions.SAPLING_ACTION_ANIMATION_PERIOD, Functions.SAPLING_ACTION_ANIMATION_PERIOD, 0,
                Functions.SAPLING_HEALTH_LIMIT);
    }

    public Fairy createFairy(
            String id,
            Point position,
            int actionPeriod,
            int animationPeriod,
            List<PImage> images)
    {
        VirtualWorld.fairy_count++;
        return new Fairy(id, position, images,
                actionPeriod, animationPeriod);
    }

    // need resource count, though it always starts at 0
    public DudeNotFull createDudeNotFull(
            String id,
            Point position,
            int health,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images)
    {
        return new DudeNotFull(id, position, images, health, resourceLimit, 0,
                actionPeriod, animationPeriod);
    }

    // don't technically need resource count ... full
    public DudeFull createDudeFull(
            String id,
            Point position,
            int health,
            int actionPeriod,
            int animationPeriod,
            int resourceLimit,
            List<PImage> images) {
        return new DudeFull(id, position, images, health, resourceLimit, 0,
                actionPeriod, animationPeriod);
    }
}
