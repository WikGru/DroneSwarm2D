import com.sun.javafx.geom.Vec2d;
import com.sun.javafx.geom.Vec4d;

import java.awt.*;
import java.util.Random;

public class Drone {
    private int nr;
    private Color col;
    private Point pos;
    private Point intention;
    private Zone finishZone;

    public Point countIntention() {
        Point diff = new Point(getFinishZone().getP1().getX() - pos.getX(), getFinishZone().getP1().getY() - pos.getY());
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
        if (diff.getY() == 0 && diff.getX() == 0) {
            x = 0;
            y = 0;
        }
        move(new Point(x,y));
        return new Point(x, y);
    }

    public Point move(Point dir) {
        this.pos = new Point(getPos().getX() + dir.getX(), getPos().getY() + dir.getY());
        return getPos();
    }

    public void manageCollisions(){
        System.out.println("Collision on spot: " + pos.getX() + "\t" + pos.getY());
    }

    //From point to point
    public Drone(int nr, Color col, Point initPos, Point finishPos) {
        this.nr = nr;
        this.col = col;
        this.pos = initPos;
        this.finishZone = new Zone(finishPos, finishPos);
    }

    //From point to zone
    public Drone(int nr, Color col, Point initPos, Zone finishZone) {
        this.nr = nr;
        this.col = col;
        this.pos = initPos;
        this.finishZone = finishZone;
    }

    //From zone to zone
    public Drone(int nr, Color col, Zone initZone, Zone finishZone) {
        this.nr = nr;
        this.col = col;
        Random rand = new Random();
        int x = rand.nextInt(initZone.getP2().getX() - initZone.getP1().getX() + 1) + initZone.getP1().getX();
        int y = rand.nextInt(initZone.getP2().getY() - initZone.getP1().getY() + 1) + initZone.getP1().getY();
        this.pos = new Point(x, y);
        this.finishZone = finishZone;
    }

    public Color getCol() {
        return col;
    }

    public void setCol(Color col) {
        this.col = col;
    }

    public int getNr() {
        return nr;
    }

    public void setNr(int nr) {
        this.nr = nr;
    }

    public Point getPos() {
        return pos;
    }

    public Point getIntention() {
        return intention;
    }

    public void setIntention(Point intention) {
        this.intention = intention;
    }

    public Zone getFinishZone() {
        return finishZone;
    }

    public void setFinishZone(Zone finishZone) {
        this.finishZone = finishZone;
    }


}
