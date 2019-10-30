package pl.edu.utp.com;

import pl.edu.utp.gui.Point;
import pl.edu.utp.gui.Zone;

import java.awt.*;
//This class implements static objects with constant position on grid
public class Wall implements GridObject {
    private String type;
    private Color col;
    private Point pos;

    public Wall(Point pos) {
        this.type = "wall";
        this.col = Color.DARK_GRAY;
        this.pos = pos;
    }


    public String getType() {
        return type;
    }

    public String getNr() {
        return "";
    }

    public Color getCol() {
        return col;
    }

    public Point getPos() {
        return pos;
    }

    @Override
    public Point getIntention() {
        return new Point(0,0);
    }

    @Override
    public void setIntention() {
        //Static obstacle
        //Stones has no intentions
    }

    @Override
    public void move() {
        //Static obstacle
        //Stones has no intentions
    }

    @Override
    public void lookForObstacles() {
        //Static obstacle
        //Stones has no intentions
    }

    @Override
    public void manageCollisions() {
        //Static obstacle
        //Stones has no intentions
    }

    @Override
    public Zone getFinishZone() {
        return new Zone(getPos(),getPos());
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public void isDead(boolean flag) {

    }

    public void setPos(Point pos) {
        this.pos = pos;
    }
}