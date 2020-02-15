package pl.edu.utp.com;

import pl.edu.utp.behaviour.Behaviour;

import java.awt.*;
import java.util.ArrayList;

//This class implements static objects with constant position on grid
public class Wall implements GridObject {
    private String type;
    private Color color;
    private Point position;

    public Wall(Point pos) {
        this.type = "wall";
        this.color = Color.DARK_GRAY;
        this.position = pos;
    }

    public String getType() {
        return type;
    }

    public String getNr() {
        return "";
    }

    public Color getCol() {
        return color;
    }

    public Point getPos() {
        return position;
    }

    public Point getIntention() {
        return new Point(0,0);
    }

    public void setIntention(Behaviour aDefault) {
        //Static obstacle
        //Stones has no intentions
    }

    public void move() {
        //Static obstacle
        //Stones has no intentions
    }

    public ArrayList<GridObject> lookForObstacles() {
        //Static obstacle
        //Stones has no intentions
        return new ArrayList<>();
    }

    public boolean manageCollisions() {
        //Static obstacle
        //Stones has no intentions
        return false;
    }

    public Zone getFinishZone() {
        return new Zone(getPos(),getPos());
    }

    public boolean isDead() {
        // Wall cannot be dead
        return false;
    }

    public void setIsDead(boolean flag) {
        // Wall is always alive therefore no setting here
    }
}
