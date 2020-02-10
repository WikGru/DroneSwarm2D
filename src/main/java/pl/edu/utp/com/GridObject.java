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
    Zone getFinishZone();
    boolean isDead();
    void setIntention(Behaviour aDefault);
    void setIsDead(boolean flag);
    ArrayList<GridObject> lookForObstacles();
    boolean manageCollisions();
    void move();
}
