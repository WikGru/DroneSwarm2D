package pl.edu.utp.behaviour;

import pl.edu.utp.com.GridObject;
import pl.edu.utp.gui.Point;
import pl.edu.utp.util.MyLogger;

import java.util.logging.Level;

public class AvoidingIntention implements Intention {
    public Point setIntention(GridObject obj) {
        for (Point p : obj.getFinishZone().getField()) {
            if (p.getX() == obj.getPos().getX() && p.getY() == obj.getPos().getY()) return new Point();
        }
        Point diff = new Point(obj.getFinishZone().getCenterPoint().getX() - obj.getPos().getX(),
                obj.getFinishZone().getCenterPoint().getY() - obj.getPos().getY());
        int x = 0;
        int y = 0;
        double ratio;
        double trigger = Math.sqrt(2) / 2;

        if (diff.getX() != 0) x = Math.abs(diff.getX()) / diff.getX();
        if (diff.getY() != 0) y = Math.abs(diff.getY()) / diff.getY();


        if (Math.abs(diff.getX()) >= Math.abs(diff.getY())) {
            ratio = (double) diff.getY() / (double) diff.getX();
            if (Math.abs(ratio) < trigger) y = 0;
        } else if (Math.abs(diff.getX()) < Math.abs(diff.getY())) {
            ratio = (double) diff.getX() / (double) diff.getY();
            if (Math.abs(ratio) < trigger) x = 0;
        }


        Point tempIntention = new Point(x, y);
        if (!verifyIntention(obj, tempIntention)) {
            if (tempIntention.getY() != 0 && tempIntention.getX() != 0) {
                x = tempIntention.getY();
                y = 0;
                if(!verifyIntention(obj, new Point(x,y))){
                    x = 0;
                    y = tempIntention.getX();
                }
            } else {
                x = tempIntention.getY();
                y = tempIntention.getX();
            }
        }

        MyLogger.log(Level.INFO,"Drone nr" + obj.getNr() + "\tavoiding by moving to" + "\t[" + x + ",\t" + y + "]");
        return new Point(x, y);
    }

    private boolean verifyIntention(GridObject obj, Point intention) {
        for (GridObject obstacle : obj.lookForObstacles()) {
            Point obstacleAfter = add(obstacle.getPos(), obstacle.getIntention());
            Point objAfter = add(obj.getPos(), intention);

            //Same spot collision
            if (obstacleAfter.getX() == objAfter.getX() && obstacleAfter.getY() == objAfter.getY()) {
                return false;
            }

            //Changed positions
            if (obstacleAfter.getX() == obj.getPos().getX() && obstacleAfter.getY() == obj.getPos().getY()
                    && obstacle.getPos().getX() == objAfter.getX() && obstacle.getPos().getY() == objAfter.getY()) {
                return false;
            }

            //Crossed midair
            if (obstacle.getPos().getX() == obj.getPos().getX()) {
                if (Math.abs(obstacle.getPos().getY() - obj.getPos().getY()) == 1) {
                    if (obstacle.getIntention().getY() * -1 == intention.getY() && intention.getY() != 0 && obstacle.getIntention().getY() != 0) {
                        return false;
                    }
                }
            } else if (obstacle.getPos().getY() == obj.getPos().getY()) {
                if (Math.abs(obstacle.getPos().getX() - obj.getPos().getX()) == 1) {
                    if (obstacle.getIntention().getX() * -1 == intention.getX() && intention.getX() != 0 && obstacle.getIntention().getX() != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private Point add(Point p1, Point p2) {
        if (p1 == null) p1 = new Point(0, 0);
        if (p2 == null) p2 = new Point(0, 0);
        return new Point(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }
}
