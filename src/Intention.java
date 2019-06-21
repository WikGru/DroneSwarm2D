import scene.Point;

public class Intention {
    public Point setIntention(GridObject obj) {
        Point diff = new Point(obj.getFinishZone().getP1().getX() - obj.getPos().getX(), obj.getFinishZone().getP1().getY() - obj.getPos().getY());
        int x = 0;
        int y = 0;
        double ratio;
        double trigger = Math.sqrt(2) / 2;

        if (diff.getX() != 0) {
            x = Math.abs(diff.getX()) / diff.getX();
        }
        if (diff.getY() != 0) {
            y = Math.abs(diff.getY()) / diff.getY();
        }

        if (Math.abs(diff.getX()) >= Math.abs(diff.getY())) {
            ratio = (double) diff.getY() / (double) diff.getX();
            if (Math.abs(ratio) < trigger) {
                y = 0;
            }
        } else if (Math.abs(diff.getX()) < Math.abs(diff.getY())) {
            ratio = (double) diff.getX() / (double) diff.getY();
            if (Math.abs(ratio) < trigger) {
                x = 0;
            }
        }
        if (obj.getPos().getX() == obj.getFinishZone().getP1().getX() && obj.getPos().getY() == obj.getFinishZone().getP1().getY()) {
            return  new Point(0, 0);
        }
        return new Point(x, y);
    }
}
