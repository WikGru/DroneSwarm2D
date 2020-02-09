package pl.edu.utp.com;

import pl.edu.utp.behaviour.Behaviour;

import java.awt.*;
import java.util.ArrayList;

//This interface unites static with dynamic objects on grid.
public interface GridObject {
    String getType();
    String getNr();
    Color getCol();
    Point getPos();
    Point getIntention();
    void setIntention(Behaviour aDefault);
    void move();
    ArrayList<GridObject> lookForObstacles();
    boolean manageCollisions();
    Zone getFinishZone();
    boolean isDead();
    void setIsDead(boolean flag);
}
