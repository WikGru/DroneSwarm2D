import scene.Point;
import scene.Zone;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
//This class implements Drones and their behaviour
public class Drone implements GridObject {
    String type = "drone";
    private boolean isDead = false;
    private ArrayList<GridObject> obstaclesInRange = new ArrayList<>();
    private int range = 2;
    private String getNr;
    private Color col;
    private scene.Point pos;
    private scene.Point intention = new scene.Point(0, 0);
    //TODO: finishZone (or even Zone class) should be stored in List<Point> to scan if drone is in zone or not
    //TODO: might be easier to count center of zone using List<Point>
    private Zone finishZone;
    private ObstacleSingleton s = ObstacleSingleton.getInstance();
    private Intention intent = new Intention();

    public void move() {
        this.pos = new scene.Point(getPos().getX() + intention.getX(), getPos().getY() + intention.getY());
    }

    public void lookForObstacles() {
        obstaclesInRange = new ArrayList<>();
        for (int x = pos.getX() - range; x < pos.getX() + range; x++) {
            for (int y = pos.getY() - range; y < pos.getY() + range; y++) {
                if (x == pos.getX() && y == pos.getY()) continue;
                for (GridObject obstacle : s.obstacles) {
                    if (obstacle.getPos().getX() == x && obstacle.getPos().getY() == y) obstaclesInRange.add(obstacle);
                }
            }
        }
    }

    public void setIntention(){
        this.intention = intent.setIntention(this);
    }

    public void manageCollisions() {
        //Sight implemented as lookForObstacles (it makes drone see only in range of 2)
        for (GridObject obj : obstaclesInRange) {
            scene.Point objPosAfterMove = addPoints(obj.getPos(), obj.getIntention());
            scene.Point myPosAfterMove = addPoints(getPos(), getIntention());
            System.out.println("[" + myPosAfterMove.getX() + "," + myPosAfterMove.getY() + "]\tgot neighbour at\t[" + objPosAfterMove.getX() + "," + objPosAfterMove.getY() + "]");


            //Same spot collision
            if (objPosAfterMove.getX() == myPosAfterMove.getX() && objPosAfterMove.getY() == myPosAfterMove.getY()) {
                isDead = true;
                if (obj.getType().equals("drone")) obj.isDead(true);
                System.out.println("same spot collision: [" + myPosAfterMove.getX() + "," + myPosAfterMove.getY() + "]\t[" + objPosAfterMove.getX() + "," + objPosAfterMove.getY() + "]");
                continue;
            }

            //Changed positions
            if (objPosAfterMove.getX() == pos.getX() && objPosAfterMove.getY() == pos.getY()
                    && obj.getPos().getX() == myPosAfterMove.getX() && obj.getPos().getY() == myPosAfterMove.getY()) {
                isDead = true;
                if (obj.getType().equals("drone")) obj.isDead(true);
                System.out.println("Change spot collision");

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
                    if (obj.getIntention().getX() * -1 == getIntention().getX()  && getIntention().getX() != 0 && obj.getIntention().getX() != 0) {
                        printCrossMidairData(obj, objPosAfterMove, myPosAfterMove);
                    }
                }
            }
        }
    }

    private void printCrossMidairData(GridObject obj, scene.Point objPosAfterMove, scene.Point myPosAfterMove) {
        System.out.println("Cross midair PRE: [" + pos.getX() + "," + pos.getY() + "]\t[" + obj.getPos().getX() + "," + obj.getPos().getY() + "]");
        System.out.println("Cross midair POST: [" + myPosAfterMove.getX() + "," + myPosAfterMove.getY() + "]\t[" + objPosAfterMove.getX() + "," + objPosAfterMove.getY() + "]");
        System.out.println("Intentions:\t[" + getIntention().getX() + ',' + getIntention().getY() + "]\t[" + obj.getIntention().getX() + ',' + obj.getIntention().getY() + ']');
        isDead = true;
        if (obj.getType().equals("drone")) obj.isDead(true);
    }

    //From point to point
    public Drone(String id, Color col, scene.Point initPos, scene.Point finishPos) {
        this.getNr = id;
        this.col = col;
        this.pos = initPos;
        this.finishZone = new Zone(finishPos, finishPos);
    }

    //From point to zone
    public Drone(String id, Color col, scene.Point initPos, Zone finishZone) {
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
        int x = rand.nextInt(initZone.getP2().getX() - initZone.getP1().getX() + 1) + initZone.getP1().getX();
        int y = rand.nextInt(initZone.getP2().getY() - initZone.getP1().getY() + 1) + initZone.getP1().getY();
        this.pos = new scene.Point(x, y);
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

    public scene.Point getPos() {
        return pos;
    }

    public scene.Point getIntention() {
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

    public scene.Point addPoints(scene.Point p1, scene.Point p2) {
        if (p1 == null) p1 = new scene.Point(0, 0);
        if (p2 == null) p2 = new scene.Point(0, 0);
        return new Point(p1.getX() + p2.getX(), p1.getY() + p2.getY());
    }


}
