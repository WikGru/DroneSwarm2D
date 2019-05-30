import java.awt.*;
import java.util.List;

public interface GridObject {
    String getType();
    int getNr();
    Color getCol();
    Point getPos();
    Point getIntention();
    void setIntention();
    void move();
    void lookForObstacles();
    void manageCollisions();
    Zone getFinishZone();
    boolean isDead();
    void isDead(boolean flag);
}
