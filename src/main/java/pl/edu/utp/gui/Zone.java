package pl.edu.utp.gui;

import java.util.ArrayList;

public class Zone {
    private ArrayList<Point> field = new ArrayList<>();
    private Point center;

    public Zone(Point p1, Point p2) {
        Point startPoint = new Point();
        Point endPoint = new Point();
        if (p1.getX() > p2.getX()) {
            startPoint.setX(p2.getX());
            endPoint.setX(p1.getX());
        } else {
            startPoint.setX(p1.getX());
            endPoint.setX(p2.getX());
        }

        if (p1.getY() > p2.getY()) {
            startPoint.setY(p2.getY());
            endPoint.setY(p1.getY());
        } else {
            startPoint.setY(p1.getY());
            endPoint.setY(p2.getY());
        }

        for (int x = startPoint.getX(); x <= endPoint.getX(); x++) {
            for (int y = startPoint.getY(); y <= endPoint.getY(); y++) {
                field.add(new Point(x, y));
            }
        }
        calculateCenter();
    }

    public ArrayList<Point> getField() {
        return field;
    }

    private void calculateCenter() {
        int x = 0;
        int y = 0;
        for (Point p : field) {
            x += p.getX();
            y += p.getY();
        }
        center = new Point(x / field.size(), y / field.size());
    }

    public Point getCenterPoint() {
        return center;
    }
}
