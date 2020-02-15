package pl.edu.utp.com;

import pl.edu.utp.behaviour.AvoidingIntention;
import pl.edu.utp.behaviour.Behaviour;
import pl.edu.utp.behaviour.BlindIntention;
import pl.edu.utp.behaviour.Intention;
import pl.edu.utp.util.MyLogger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

//This class implements Drones and their behaviour
public class Drone implements GridObject {
    private Intention defaultBehaviour = new BlindIntention();
    private Intention avoidingBehaviour = new AvoidingIntention();
    private Intention behaviour = defaultBehaviour;

    private boolean isDead = false;
    private ArrayList<GridObject> obstaclesInRange = new ArrayList<>();
    private String id;
    private Color color;
    private Point position;
    private Point intention = new Point();
    private Zone finishZone;
    private EntitiesSingleton entitiesInstance = EntitiesSingleton.getInstance();

    public Drone(String id, Color col, Zone initZone, Zone finishZone) {
        this.id = id;
        this.color = col;
        Random rand = new Random();
        if (initZone.getField().size() == 1) this.position = initZone.getField().get(0);
        else this.position = initZone.getField().get(rand.nextInt(initZone.getField().size() - 1));
        this.finishZone = finishZone;
    }

    public void move() {
        this.position = new Point(getPos().getX() + intention.getX(), getPos().getY() + intention.getY());
    }

    public ArrayList<GridObject> lookForObstacles() {
        int range = 2;
        int x1 = position.getX() - range;
        int x2 = position.getX() + range;
        int y1 = position.getY() - range;
        int y2 = position.getY() + range;

        obstaclesInRange.clear();

        StringBuilder log = new StringBuilder();
        for (GridObject obstacle : entitiesInstance.getEntitiesList()) {
            if (obstacle.getPos().getX() == position.getX() && obstacle.getPos().getY() == position.getY()) continue;
            if (obstacle.getPos().getX() >= x1 && obstacle.getPos().getX() <= x2
                    && obstacle.getPos().getY() >= y1 && obstacle.getPos().getY() <= y2) {
                obstaclesInRange.add(obstacle);
                log.append("[").append(obstacle.getPos().getX()).append(", ").append(obstacle.getPos().getY()).append("] ");
            }
        }
        MyLogger.log(Level.INFO, "Objects in sight of drone nr" + this.id + ": " + log);

        return obstaclesInRange;
    }

    public void setIntention(Behaviour behaviour) {
        switch (behaviour) {
            case AVOIDING:
                this.behaviour = avoidingBehaviour;
                break;
            case DEFAULT:
                this.behaviour = defaultBehaviour;
                break;
        }
        this.intention = this.behaviour.setIntention(this);
    }

    public boolean manageCollisions() {
        //Sight implemented as lookForObstacles (it makes drone to see only in specified range)
        for (GridObject obj : obstaclesInRange) {
            Point objPosAfterMove = add(obj.getPos(), obj.getIntention());
            Point myPosAfterMove = add(getPos(), getIntention());

            //Same spot collision
            if (objPosAfterMove.getX() == myPosAfterMove.getX() &&
                    objPosAfterMove.getY() == myPosAfterMove.getY()) {
                isDead = true;
                if (obj.getType().equals("drone")) obj.setIsDead(true);
                MyLogger.log(Level.INFO, "Same spot collision drone nr " + this.id + ": ["
                        + myPosAfterMove.getX() + ", " + myPosAfterMove.getY() + "]\t["
                        + objPosAfterMove.getX() + ", " + objPosAfterMove.getY() + "]");
                return true;
            }

            //Changed positions
            if (objPosAfterMove.getX() == position.getX() && objPosAfterMove.getY() == position.getY()
                    && obj.getPos().getX() == myPosAfterMove.getX() && obj.getPos().getY() == myPosAfterMove.getY()) {
                isDead = true;
                if (obj.getType().equals("drone")) obj.setIsDead(true);
                MyLogger.log(Level.INFO, "Change spot collision drone nr " + this.id + " & nr " + obj.getNr() + ".");
                return true;
            }

            //Crossed midair
            if (obj.getPos().getX() == position.getX()) {
                if (Math.abs(obj.getPos().getY() - position.getY()) == 1) {
                    if (obj.getIntention().getY() * -1 == getIntention().getY() &&
                            getIntention().getY() != 0 && obj.getIntention().getY() != 0) {
                        handleCrossMidAirCollision(obj, objPosAfterMove, myPosAfterMove);
                        return true;
                    }
                }
            } else if (obj.getPos().getY() == position.getY()) {
                if (Math.abs(obj.getPos().getX() - position.getX()) == 1) {
                    if (obj.getIntention().getX() * -1 == getIntention().getX() &&
                            getIntention().getX() != 0 && obj.getIntention().getX() != 0) {
                        handleCrossMidAirCollision(obj, objPosAfterMove, myPosAfterMove);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void handleCrossMidAirCollision(GridObject obj, Point objPosAfterMove, Point myPosAfterMove) {
        MyLogger.log(Level.INFO, "Crossed mid-air drone nr " + this.id + " & nr " + obj.getNr() + ". Attempted movements: ["
                + this.getPos().getX() + ", " + this.getPos().getY() + "] -> [" + +myPosAfterMove.getX() + ", " + myPosAfterMove.getY() + "]  &  ["
                + obj.getPos().getX() + ", " + obj.getPos().getY() + "] -> [" + +objPosAfterMove.getX() + ", " + objPosAfterMove.getY() + "].");
        isDead = true;
        if (obj.getType().equals("drone")) obj.setIsDead(true);
    }

    public String getType() {
        return "drone";
    }

    public Color getCol() {
        return color;
    }

    public String getNr() {
        return id;
    }

    public Point getPos() {
        return position;
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

    public void setIsDead(boolean flag) {
        isDead = flag;
    }

    private Point add(Point p1, Point p2) {
        if (p1 == null) p1 = new Point(0, 0);
        if (p2 == null) p2 = new Point(0, 0);
        return new Point(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }
}
