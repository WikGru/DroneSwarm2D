import com.sun.javafx.geom.Vec2d;
import com.sun.javafx.geom.Vec4d;

import java.awt.*;
import java.util.Random;

public class Drone {
    private int nr;
    private Color col;
    private Point pos;
    private Zone finishZone;

    public Point countDiff(){
        Point diff = new Point(getFinishZone().getP1().getX() - getPos().getX(),getFinishZone().getP1().getY() - getPos().getY());
        return diff;
    }

    public Point move(Point dir) {
        this.pos = new Point(getPos().getX() + dir.getX(), getPos().getY() + dir.getY());
        return getPos();
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

    public Zone getFinishZone() {
        return finishZone;
    }

    public void setFinishZone(Zone finishZone) {
        this.finishZone = finishZone;
    }


}
