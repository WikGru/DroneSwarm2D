package gui;

import com.Drone;
import com.GridObject;
import com.ObstacleSingleton;
import com.Wall;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

//This is the main App class used to run the simulation
public class App {
    private JPanel mainPanel;
    private JTable gridTable;
    private JPanel gridPanel;
    private JPanel userPanel;
    private JButton nextStepButton;
    private JTextPane logTextfield;
    private JButton logClearButton;
    private JButton resetButton;

    private String dronesInput = "scenarioDrones.json";
    private String obstaclesInput = "scenarioObstacles.json";
    private String configInput = "config.json";
    private int stepCounter = 0;
    private String dronesAlive = "";
    private Logger log;
    private ObstacleSingleton s = ObstacleSingleton.getInstance();

    private int windowSize = 0;
    private int gridSize = 1;

    public App() {
        //Attach logDisplayField to Logger
        log = new Logger(logTextfield);

        resetGrid();
        //Reset actionListener
        resetButton.addActionListener(e -> resetGrid());
        //NextStep actionListener
        nextStepButton.addActionListener(e -> nextStep());
        //ClearLog actionListener
        logClearButton.addActionListener(e -> log.clearLogDisplay());
    }

    private void loadConfig() {
        try {
            String myJson = new Scanner(new File(configInput)).useDelimiter("\\Z").next();
            JSONObject obj = new JSONObject(myJson);
            windowSize = obj.getInt("windowSize");
            gridSize = obj.getInt("gridSize");
        } catch (Exception e) {
            log.writeToLogFile(Logger._msgType.ERR,"Could not read from config.json file");
            e.printStackTrace();
        }
    }

    private void loadScenario() {
       loadDrones();
       loadObstacles();
    }

    private void loadDrones(){
        try {
            String myJson = new Scanner(new File(dronesInput)).useDelimiter("\\Z").next();
            JSONArray arr = new JSONArray(myJson);
            for (Object obj : arr) {
                JSONObject jObj = (JSONObject) obj;

                int id = jObj.getInt("id");
                String color = jObj.getString("color");
                JSONObject startingZone = jObj.getJSONObject("startingZone");
                JSONObject finishZone = jObj.getJSONObject("finishZone");

                Point start = new Point(startingZone.getJSONObject("begin").getInt("x"),startingZone.getJSONObject("begin").getInt("y"));
                Point finish = new Point(finishZone.getJSONObject("begin").getInt("x"),finishZone.getJSONObject("begin").getInt("y"));

                Drone drone = new Drone("" + id, Color.decode(color), start, finish);
                s.obstacles.add(drone);
                gridTable.setValueAt(new Cell(drone.getType(), drone.getNr(), drone.getCol()), drone.getPos().getY(), drone.getPos().getX());
                gridTable.setValueAt(new Cell("wall", drone.getNr(), Color.decode("#bcf7b5")), drone.getFinishZone().getP1().getY(), drone.getFinishZone().getP1().getX());

            }
        } catch (Exception e) {
            log.writeToLogFile(Logger._msgType.ERR,"Could not read from scenarioDrones.json file");
            e.printStackTrace();
        }
    }

    private void loadObstacles(){
        try {
            String myJson = new Scanner(new File(obstaclesInput)).useDelimiter("\\Z").next();
            JSONArray arr = new JSONArray(myJson);
            for (Object obj : arr) {
                JSONObject jObj = (JSONObject) obj;

                Point point = new Point(jObj.getInt("x"),jObj.getInt("y"));

                Wall obstacle = new Wall(point);

                s.obstacles.add(obstacle);
                gridTable.setValueAt(new Cell("wall", obstacle.getNr(), obstacle.getCol()), obstacle.getPos().getY(), obstacle.getPos().getX());
            }
        } catch (Exception e) {
            log.writeToLogFile(Logger._msgType.ERR,"Could not read from scenarioObstacles.json file");
            e.printStackTrace();
        }
    }

    public void resetGrid() {
        log.writeToLogFile(Logger._msgType.MSG,"");
        log.writeToLogFile(Logger._msgType.MSG,"/--------------------------------------\\");
        log.writeToLogFile(Logger._msgType.MSG,"\\----------New scenario start----------/");
        stepCounter = 0;
        s.obstacles.clear();

        //Reload config file
        loadConfig();



        //Table size pre-set (rows and cols width and height)
        DefaultTableModel model = new DefaultTableModel(gridSize, gridSize);
        gridTable.setModel(model);
        for (int i = 0; i < model.getColumnCount(); i++) {
            gridTable.getColumnModel().getColumn(i).setMinWidth(windowSize / gridSize);
            gridTable.getColumnModel().getColumn(i).setMaxWidth(windowSize / gridSize);
            gridTable.getColumnModel().getColumn(i).setCellRenderer(new ButtonRenderer());
        }
        gridTable.setRowHeight(windowSize / gridSize);

        //Reload scenario data
        loadScenario();
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

        log.writeToLogFile(Logger._msgType.MSG,"STEP: " + stepCounter);
        log.writeToLogFile(Logger._msgType.MSG,"Drones alive: " + dronesAlive);
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
        log.writeToLogFile(Logger._msgType.MSG, "Intentions:");

        //Input physical objects (Drones & Walls)
        for (GridObject obj : s.obstacles) {
            gridTable.setValueAt(new Cell(), obj.getPos().getY(), obj.getPos().getX());
            obj.lookForObstacles();
            obj.setIntention();
            if (obj.getType().equals("drone")) {
                log.writeToLogDisplay(obj.getNr() + "  ->  [" + obj.getIntention().getX() + ", " + obj.getIntention().getY() + "]");
                log.writeToLogFile(Logger._msgType.MSG,obj.getNr() + "  ->  [" + obj.getIntention().getX() + ", " + obj.getIntention().getY() + "]");
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
