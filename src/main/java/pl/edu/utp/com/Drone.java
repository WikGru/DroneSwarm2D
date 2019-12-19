package pl.edu.utp.com;

import pl.edu.utp.behaviour.AvoidingIntention;
import pl.edu.utp.behaviour.BlindIntention;
import pl.edu.utp.behaviour.Intention;
import pl.edu.utp.gui.Point;
import pl.edu.utp.gui.Zone;
import pl.edu.utp.util.Behaviour;
import pl.edu.utp.util.MyLogger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

//This class implements Drones and their behaviour
public class Drone implements GridObject {
    private Intention defaultBehaviour = new BlindIntention();
    private Intention avoidingBehaviour = new AvoidingIntention();

    private Intention behaviour = defaultBehaviour;

    private String type = "drone";
    private boolean isDead = false;
    private ArrayList<GridObject> obstaclesInRange = new ArrayList<>();
    private int range = 2;
    private String getNr;
    private Color col;
    private Point pos;
    private Point intention = new Point();
    private Zone finishZone;
    private EntitiesSingleton s = EntitiesSingleton.getInstance();
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
        for (GridObject obstacle : s.getEntitiesList()) {
            if (obstacle.getPos().getX() == pos.getX() && obstacle.getPos().getY() == pos.getY()) continue;
            if (obstacle.getPos().getX() >= x1 && obstacle.getPos().getX() <= x2
                    && obstacle.getPos().getY() >= y1 && obstacle.getPos().getY() <= y2) {
                obstaclesInRange.add(obstacle);
                log.append("[").append(obstacle.getPos().getX()).append(", ").append(obstacle.getPos().getY()).append("] ");
            }
        }
        MyLogger.log(Level.INFO, "Objects in sight of drone nr" + this.getNr + ": " + log);
    }

    public void setIntention(Behaviour behaviour) {
        switch(behaviour){
            case AVOIDING:
                this.behaviour = avoidingBehaviour;
                break;
            case DEFAULT:
                this.behaviour = defaultBehaviour;
                break;
        }
        this.intention = this.behaviour.setIntention(this);

    }

    public void manageCollisions() {
        //Sight implemented as lookForObstacles (it makes drone to see only in specified range)
        for (GridObject obj : obstaclesInRange) {
            Point objPosAfterMove = add(obj.getPos(), obj.getIntention());
            Point myPosAfterMove = add(getPos(), getIntention());

            //Same spot collision
            if (objPosAfterMove.getX() == myPosAfterMove.getX() && objPosAfterMove.getY() == myPosAfterMove.getY()) {
                isDead = true;
                if (obj.getType().equals("drone")) obj.isDead(true);
                MyLogger.log(Level.INFO, "Same spot collision drone nr" + this.getNr + ": ["
                        + myPosAfterMove.getX() + ", " + myPosAfterMove.getY() + "]\t["
                        + objPosAfterMove.getX() + ", " + objPosAfterMove.getY() + "]");
                continue;
            }

            //Changed positions
            if (objPosAfterMove.getX() == pos.getX() && objPosAfterMove.getY() == pos.getY()
                    && obj.getPos().getX() == myPosAfterMove.getX() && obj.getPos().getY() == myPosAfterMove.getY()) {
                isDead = true;
                if (obj.getType().equals("drone")) obj.isDead(true);
                MyLogger.log(Level.INFO, "Change spot collision drone nr" + this.getNr + " & nr" + obj.getNr() + ".");
                continue;
            }

            //Crossed midair
            if (obj.getPos().getX() == pos.getX()) {
                if (Math.abs(obj.getPos().getY() - pos.getY()) == 1) {
                    if (obj.getIntention().getY() * -1 == getIntention().getY() && getIntention().getY() != 0 && obj.getIntention().getY() != 0) {
                        handleCrossMidAirCollision(obj, objPosAfterMove, myPosAfterMove);
                    }
                }
            } else if (obj.getPos().getY() == pos.getY()) {
                if (Math.abs(obj.getPos().getX() - pos.getX()) == 1) {
                    if (obj.getIntention().getX() * -1 == getIntention().getX() && getIntention().getX() != 0 && obj.getIntention().getX() != 0) {
                        handleCrossMidAirCollision(obj, objPosAfterMove, myPosAfterMove);
                    }
                }
            }
        }
    }

    private void handleCrossMidAirCollision(GridObject obj, Point objPosAfterMove, Point myPosAfterMove) {
        MyLogger.log(Level.INFO, "Crossed mid-air drone nr" + this.getNr + " & " + obj.getNr() + ".");
        MyLogger.log(Level.INFO, "----Attempted movements: ["
                + this.getPos().getX() + ", " + this.getPos().getY() + "] -> [" + +myPosAfterMove.getX() + ", " + myPosAfterMove.getY() + "]  &  ["
                + obj.getPos().getX() + ", " + obj.getPos().getY() + "] -> [" + +objPosAfterMove.getX() + ", " + objPosAfterMove.getY() + "].");
        isDead = true;
        if (obj.getType().equals("drone")) obj.isDead(true);
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

    public boolean isDead() {
        return isDead;
    }

    @Override
    public void isDead(boolean flag) {
        isDead = flag;
    }

    private Point add(Point p1, Point p2) {
        if (p1 == null) p1 = new Point(0, 0);
        if (p2 == null) p2 = new Point(0, 0);
        return new Point(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }
}
