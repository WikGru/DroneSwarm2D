package pl.edu.utp.behaviour;

import pl.edu.utp.com.GridObject;
import pl.edu.utp.gui.Point;

public class AvoidingIntention implements Intention {
    int x;
    public Point setIntention(GridObject obj) {
        return new Point(0, 0);
    }
}
