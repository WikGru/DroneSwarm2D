package gui;

import com.Drone;
import com.GridObject;
import com.ObstacleSingleton;
import com.Wall;
import org.json.JSONArray;
import util.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

//This is the main App class used to run the simulation
public class App {
    private int stepCounter = 0;
    private String dronesAlive = "";
    private JPanel mainPanel;
    private JTable gridTable;
    private JPanel gridPanel;
    private JPanel userPanel;
    private JButton nextStepButton;
    private JTextPane logTextfield;
    private JButton logClearButton;
    private JButton resetButton;
    private Logger log;
    private ObstacleSingleton s = ObstacleSingleton.getInstance();

    int windowSize = 0;
    int gridSize = 1;

    public App() {
        resetGrid();
        //Reset actionListener
        resetButton.addActionListener(e -> resetGrid());
        //NextStep actionListener
        nextStepButton.addActionListener(e -> nextStep());
        //ClearLog actionListener
        logClearButton.addActionListener(e -> log.clearLogDisplay());
    }

    private void readData(){
//        JSONArray a
    }

    public void resetGrid() {
        s.obstacles.clear();
        //Size of displayed window
        windowSize = 500;
        //Amount of rows and cols
        gridSize = 10;
        //Amount of placed drones on grid (all are magenta at the moment)
        int droneAmount = 5;
        //Attach logDisplayField to Logger
        log = new Logger(logTextfield);

        //Table size pre-set (rows and cols width and height)
        DefaultTableModel model = new DefaultTableModel(gridSize, gridSize);
        gridTable.setModel(model);
        for (int i = 0; i < model.getColumnCount(); i++) {
            gridTable.getColumnModel().getColumn(i).setMinWidth(windowSize / gridSize);
            gridTable.getColumnModel().getColumn(i).setMaxWidth(windowSize / gridSize);
            gridTable.getColumnModel().getColumn(i).setCellRenderer(new ButtonRenderer());
        }
        gridTable.setRowHeight(windowSize / gridSize);




        //TODO: com.Drone placement (by hand by now/ will be from file .JSON)
        Random rand = new Random();
        Point start;
        Point finish;
        for (int i = 0; i < droneAmount; i++) {
            start = new Point(rand.nextInt(gridSize - 1), rand.nextInt(gridSize - 1));
            finish = new Point(rand.nextInt(gridSize - 1), rand.nextInt(gridSize - 1));
            Drone drone = new Drone("" + i, Color.decode("#FF00FF"), start, finish);
            s.obstacles.add(drone);
            gridTable.setValueAt(new Cell(drone.getType(), drone.getNr(), drone.getCol()), drone.getPos().getY(), drone.getPos().getX());
            gridTable.setValueAt(new Cell("wall", drone.getNr(), Color.decode("#bcf7b5")), drone.getFinishZone().getP1().getY(), drone.getFinishZone().getP1().getX());

        }

        //TODO: com.Wall placement (by hand now/ will be from file .JSON)
        for (int i = 0; i < 2; i++) {
            Wall wall = new Wall(new Point(i, 1));
            s.obstacles.add(wall);
            gridTable.setValueAt(new Cell(wall.getType(), "", wall.getCol()), wall.getPos().getY(), wall.getPos().getX());
        }
    }

    public static void main(String[] args) {
        //Frame pre-set
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().mainPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
    }

    //Handling next step
    private void nextStep() {
        stepCounter++;

        dronesAlive = "[";
        for (GridObject obj : s.obstacles) {
            if (obj.getType().equals("drone") && !obj.isDead()) {
                dronesAlive += obj.getNr() + ", ";
            }
        }
        dronesAlive += "]";

        log.writeToLogDisplay("Step: " + stepCounter);
        log.writeToLogDisplay("Drones alive: " + dronesAlive);

        log.writeToLogFile("Step: " + stepCounter);
        log.writeToLogFile("Drones alive: " + dronesAlive);
        //Clearing grid
        for (int x = 0; x < gridTable.getColumnCount() - 1; x++) {
            for (int y = 0; y < gridTable.getRowCount() - 1; y++) {
                gridTable.setValueAt(new Cell(), y, x);
            }
        }

        //Input finishZones
        for (GridObject obj : s.obstacles) {
            gridTable.setValueAt(new Cell("wall", obj.getNr(), Color.decode("#bcf7b5")), obj.getFinishZone().getP1().getY(), obj.getFinishZone().getP1().getX());
        }

        log.writeToLogDisplay("Intentions:");
        log.writeToLogFile("Intentions:");

        //Input physical objects (Drones & Walls)
        for (GridObject obj : s.obstacles) {
            gridTable.setValueAt(new Cell(), obj.getPos().getY(), obj.getPos().getX());
            obj.lookForObstacles();
            obj.setIntention();
            if (obj.getType().equals("drone")) {
                log.writeToLogDisplay(obj.getNr() + "  ->  [" + obj.getIntention().getX() + ", " + obj.getIntention().getY() + "]");
                log.writeToLogFile(obj.getNr() + "  ->  [" + obj.getIntention().getX() + ", " + obj.getIntention().getY() + "]");
            }
        }

        //Manage drone collisions
        for (GridObject obj : s.obstacles) {
            obj.manageCollisions();
        }

        //Remove drones that will collide
        ArrayList<GridObject> toRemove = new ArrayList<>();
        for (GridObject obj : s.obstacles) {
            if (obj.isDead()) toRemove.add(obj);
        }
        for (GridObject obj : toRemove) {
            s.obstacles.remove(obj);
        }

        //Move safe physical objects
        for (GridObject obj : s.obstacles) {
            obj.move();
            gridTable.setValueAt(new Cell(obj.getType(), obj.getNr(), obj.getCol()), obj.getPos().getY(), obj.getPos().getX());
        }
    }
}
