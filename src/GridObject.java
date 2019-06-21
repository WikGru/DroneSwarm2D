import scene.Point;
import scene.Zone;

import java.awt.*;

//This interface unites static with dynamic objects on grid.
public interface GridObject {
    String getType();
    String getNr();
    Color getCol();
    scene.Point getPos();
    Point getIntention();
    void setIntention();
    void move();
    void lookForObstacles();
    void manageCollisions();
    Zone getFinishZone();
    boolean isDead();
    void isDead(boolean flag);
}
