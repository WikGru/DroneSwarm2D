package pl.edu.utp.com;

import pl.edu.utp.gui.Point;
import pl.edu.utp.gui.Zone;
import pl.edu.utp.util.MyLogger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

//This class implements Drones and their behaviour
public class Drone extends Intention implements GridObject {
    String type = "drone";
    private boolean isDead = false;
    private ArrayList<GridObject> obstaclesInRange = new ArrayList<>();
    private int range = 2;
    private String getNr;
    private Color col;
    private Point pos;
    private Point intention = new Point();
    private Zone finishZone;
    private ObstacleSingleton s = ObstacleSingleton.getInstance();
    private static final Logger LOGGER = Logger.getLogger(Drone.class.getName());

    public void move() {
        this.pos = new Point(getPos().getX() + intention.getX(), getPos().getY() + intention.getY());
    }

    public void lookForObstacles() {
        int x1 = pos.getX() - range;
        int x2 = pos.getX() + range;
        int y1 = pos.getY() - range;
        int y2 = pos.getY() + range;

        obstaclesInRange = new ArrayList<>();

        StringBuilder log = new StringBuilder();
        for (GridObject obstacle : s.obstacles) {
            if (obstacle.getPos().getX() == pos.getX() && obstacle.getPos().getY() == pos.getY()) continue;
            if (obstacle.getPos().getX() >= x1 && obstacle.getPos().getX() <= x2
                    && obstacle.getPos().getY() >= y1 && obstacle.getPos().getY() <= y2) {
                obstaclesInRange.add(obstacle);
                log.append("[").append(obstacle.getPos().getX()).append(", ").append(obstacle.getPos().getY()).append("] ");
            }
        }
        MyLogger.log(Level.INFO,"Objects in sight of drone nr" + this.getNr + ": " + log);
    }

    public void setIntention() {
        this.intention = setIntention(this);
    }

    public void manageCollisions() {
        int dist;

        //Sight implemented as lookForObstacles (it makes drone to see only in specified range)
        for (GridObject obj : obstaclesInRange) {
            Point objPosAfterMove = addPoints(obj.getPos(), obj.getIntention());
            Point myPosAfterMove = addPoints(getPos(), getIntention());

            //Same spot collision
            if (objPosAfterMove.getX() == myPosAfterMove.getX() && objPosAfterMove.getY() == myPosAfterMove.getY()) {
                isDead = true;
                if (obj.getType().equals("drone")) obj.isDead(true);
                MyLogger.log(Level.INFO,"Same spot collision drone nr" + this.getNr + ": ["
                        + myPosAfterMove.getX() + ", " + myPosAfterMove.getY() + "]\t["
                        + objPosAfterMove.getX() + ", " + objPosAfterMove.getY() + "]");
                continue;
            }

            //Changed positions
            if (objPosAfterMove.getX() == pos.getX() && objPosAfterMove.getY() == pos.getY()
                    && obj.getPos().getX() == myPosAfterMove.getX() && obj.getPos().getY() == myPosAfterMove.getY()) {
                isDead = true;
                if (obj.getType().equals("drone")) obj.isDead(true);
                MyLogger.log(Level.INFO,"Change spot collision drone nr"+this.getNr+" & "+ obj.getNr()+".");
                continue;
            }

            //Crossed midair
            if (obj.getPos().getX() == pos.getX()) {
                if (Math.abs(obj.getPos().getY() - pos.getY()) == 1) {
                    if (obj.getIntention().getY() * -1 == getIntention().getY() && getIntention().getY() != 0 && obj.getIntention().getY() != 0) {
                        printCrossMidairData(obj, objPosAfterMove, myPosAfterMove);
                    }
                }
            } else if (obj.getPos().getY() == pos.getY()) {
                if (Math.abs(obj.getPos().getX() - pos.getX()) == 1) {
                    if (obj.getIntention().getX() * -1 == getIntention().getX() && getIntention().getX() != 0 && obj.getIntention().getX() != 0) {
                        printCrossMidairData(obj, objPosAfterMove, myPosAfterMove);
                    }
                }
            }
        }
    }

    private void printCrossMidairData(GridObject obj, Point objPosAfterMove, Point myPosAfterMove) {
        //TODO: check if it works properly. Prepare scenario where it should happen.
        System.out.println("Cross midair PRE: [" + pos.getX() + "," + pos.getY() + "]\t[" + obj.getPos().getX() + "," + obj.getPos().getY() + "]");
        System.out.println("Cross midair POST: [" + myPosAfterMove.getX() + "," + myPosAfterMove.getY() + "]\t[" + objPosAfterMove.getX() + "," + objPosAfterMove.getY() + "]");
        System.out.println("Intentions:\t[" + getIntention().getX() + ',' + getIntention().getY() + "]\t[" + obj.getIntention().getX() + ',' + obj.getIntention().getY() + ']');
        isDead = true;
        if (obj.getType().equals("drone")) obj.isDead(true);
    }

    //From point to point
    public Drone(String id, Color col, Point initPos, Point finishPos) {
        this.getNr = id;
        this.col = col;
        this.pos = initPos;
        this.finishZone = new Zone(finishPos, finishPos);
    }

    //From point to zone
    public Drone(String id, Color col, Point initPos, Zone finishZone) {
        this.getNr = id;
        this.col = col;
        this.pos = initPos;
        this.finishZone = finishZone;
    }

    //From zone to zone
    public Drone(String id, Color col, Zone initZone, Zone finishZone) {
        this.getNr = id;
        this.col = col;
        Random rand = new Random();
        if (initZone.getField().size() == 1) this.pos = initZone.getField().get(0);
        else this.pos = initZone.getField().get(rand.nextInt() % initZone.getField().size() - 1);
        this.finishZone = finishZone;
    }

    public String getType() {
        return type;
    }

    public Color getCol() {
        return col;
    }

    public String getNr() {
        return getNr;
    }

    public Point getPos() {
        return pos;
    }

    public Point getIntention() {
        return intention;
    }

    public Zone getFinishZone() {
        return finishZone;
    }

    public void setFinishZone(Zone finishZone) {
        this.finishZone = finishZone;
    }

    public boolean isDead() {
        return isDead;
    }

    @Override
    public void isDead(boolean flag) {
        isDead = flag;
    }

    public Point addPoints(Point p1, Point p2) {
        if (p1 == null) p1 = new Point(0, 0);
        if (p2 == null) p2 = new Point(0, 0);
        return new Point(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }
}
