package pl.edu.utp.com;

import pl.edu.utp.gui.Point;

abstract class Intention {
    Point setIntention(GridObject obj) {
        for(Point p : obj.getFinishZone().getField()){
            if(p.getX() == obj.getPos().getX() && p.getY() == obj.getPos().getY()) return new Point();
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
        if (obj.getPos().getX() == obj.getFinishZone().getCenterPoint().getX()
                && obj.getPos().getY() == obj.getFinishZone().getCenterPoint().getY()) {
            return new Point(0, 0);
        }
        return new Point(x, y);
    }
}
