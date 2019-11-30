package pl.edu.utp.com;

import pl.edu.utp.gui.Point;
import pl.edu.utp.gui.Zone;
import pl.edu.utp.util.Behaviour;

import java.awt.*;

//This interface unites static with dynamic objects on grid.
public interface GridObject {
    String getType();
    String getNr();
    Color getCol();
    Point getPos();
    Point getIntention();
    void setIntention(Behaviour aDefault);
    void move();
    void lookForObstacles();
    void manageCollisions();
    Zone getFinishZone();
    boolean isDead();
    void isDead(boolean flag);
}
