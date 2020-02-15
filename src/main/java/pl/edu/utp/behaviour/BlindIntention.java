package pl.edu.utp.behaviour;

import pl.edu.utp.com.GridObject;
import pl.edu.utp.com.Point;

public class BlindIntention implements Intention {
    public Point setIntention(GridObject obj) {
        for (Point p : obj.getFinishZone().getField()) {
            if (p.getX() == obj.getPos().getX() &&
                p.getY() == obj.getPos().getY()) {
                return new Point();
            }
        }

        int diffX = obj.getFinishZone().getCenterPoint().getX() - obj.getPos().getX();
        int diffY = obj.getFinishZone().getCenterPoint().getY() - obj.getPos().getY();
        Point diff = new Point(diffX, diffY);

        int x = 0;
        int y = 0;
        double ratio;


        if (diff.getX() != 0) x = Math.abs(diff.getX()) / diff.getX();
        if (diff.getY() != 0) y = Math.abs(diff.getY()) / diff.getY();

        double trigger = Math.sqrt(2) / 2;
        if (Math.abs(diff.getX()) >= Math.abs(diff.getY())) {
            ratio = (double) diff.getY() / (double) diff.getX();
            if (Math.abs(ratio) < trigger) y = 0;
        } else if (Math.abs(diff.getX()) < Math.abs(diff.getY())) {
            ratio = (double) diff.getX() / (double) diff.getY();
            if (Math.abs(ratio) < trigger) x = 0;
        }

        return new Point(x, y);
    }
}
