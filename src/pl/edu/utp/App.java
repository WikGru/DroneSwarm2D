package pl.edu.utp;

import pl.edu.utp.com.Drone;
import pl.edu.utp.com.GridObject;
import pl.edu.utp.com.ObstacleSingleton;
import pl.edu.utp.com.Wall;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.edu.utp.gui.ButtonRenderer;
import pl.edu.utp.gui.Cell;
import pl.edu.utp.gui.Point;
import pl.edu.utp.gui.Zone;
import pl.edu.utp.util.MyLogger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.*;

//TODO: DONE? abstraction of Intention class (research how to, and implement)
//TODO: Log-points to set in many places
// line 68 - attach button to something: logClearButton.addActionListener(e -> log.clearLogDisplay());
//TODO: learn how to and implement dynamic dependencies system for JSON library

//This is the main App class used to run the simulation
public class App extends Component {
    private static JFrame frame = new JFrame("Simulator");
    private JPanel mainPanel;
    private JPanel gridPanel;
    private JPanel userPanel;
    private JTable gridTable;
    private JTextPane logTextfield;
    private JButton nextStepButton;
    private JButton logClearButton;
    private JButton resetButton;
    private JButton selectScenarioButton;
    private JButton selectEnvironmentButton;
    private JButton editConfigurationButton;

    private File dronesInput = new File("scenarioDrones.json");
    private File obstaclesInput = new File("scenarioObstacles.json");
    private ObstacleSingleton s = ObstacleSingleton.getInstance();
    private int windowSize;
    private int gridSize = 1;
    private int stepNr = 1;

    public App() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tH:%1$tM:%1$tS.%1$tL] [%4$-7s] %5$s %n");

        MyLogger.log(Level.INFO,"Setup app settings and scenarios");

        frame.setResizable(false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        windowSize = screenSize.height * 3 / 5;
        resetScenario();
        //Reset actionListener
        resetButton.addActionListener(e -> resetScenario());
        //NextStep actionListener
        nextStepButton.addActionListener(e -> nextStep());
        //ClearLog actionListener
        //TODO: attach button to something: logClearButton.addActionListener(e -> <some function>);

        //Dialog with textfield to edit windowSize and gridSize
        editConfigurationButton.addActionListener(e -> {
            JDialog edit = new EditConfiguration();
            edit.setLocationRelativeTo(frame);
            edit.setVisible(true);
        });
        //Dialog with FileChooser for choosing Drone scenario JSON file
        selectScenarioButton.addActionListener(e -> {
            JFileChooser scenarioDrones = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON files", "json");
            scenarioDrones.setFileFilter(filter);
            int returnVal = scenarioDrones.showDialog(frame, "Select");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                dronesInput = scenarioDrones.getSelectedFile();
            }
            loadScenario();
        });
        //Dialog with FileChooser for choosing Obstacles scenario JSON file
        selectEnvironmentButton.addActionListener(e -> {
            JFileChooser scenarioWalls = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON files", "json");
            scenarioWalls.setFileFilter(filter);
            int returnVal = scenarioWalls.showDialog(frame, "Select");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                obstaclesInput = scenarioWalls.getSelectedFile();
            }
            loadScenario();
        });

        MyLogger.log(Level.INFO,"App setup finished.");
    }

    private void loadConfig() {
        MyLogger.log(Level.INFO,"Loading config.");
        try {
            String configInput = "config.json";
            String myJson = new Scanner(new File(configInput)).useDelimiter("\\Z").next();
            JSONObject obj = new JSONObject(myJson);
            gridSize = obj.getInt("gridSize");
            // This block configure the logger with handler and formatter

            MyLogger.log(Level.INFO,"Config loaded.");
        } catch (Exception e) {
            MyLogger.log(Level.WARNING,"Execution of loadConfig failed.");
            e.printStackTrace();
        }
    }

    private void loadScenario() {
        MyLogger.log(Level.INFO,"Loading scenario.");
        resetGrid();
        s.obstacles.clear();
        try {
            loadDrones();
        } catch (Exception e) {
            ArrayList<GridObject> delete = new ArrayList<>();
            for (GridObject obj : delete) {
                s.obstacles.remove(obj);
            }
            resetGrid();
        }
        try {
            loadObstacles();
        } catch (Exception e) {
            ArrayList<GridObject> delete = new ArrayList<>();
            for (GridObject obj : s.obstacles) {
                if (obj.getType().equals("wall")) delete.add(obj);
            }
            for (GridObject obj : delete) {
                s.obstacles.remove(obj);
            }
            resetGrid();
        }

    }

    private void loadDrones() {
        MyLogger.log(Level.INFO,"Loading drones.");
        try {
            String myJson = new Scanner(dronesInput).useDelimiter("\\Z").next();
            JSONArray arr = new JSONArray(myJson);
            for (Object obj : arr) {
                JSONObject jObj = (JSONObject) obj;

                int id = jObj.getInt("id");
                String color = jObj.getString("color");
                JSONObject jStartingZone = jObj.getJSONObject("startingZone");
                JSONObject jFinishZone = jObj.getJSONObject("finishZone");

                //Starting Zone parser
                Point startingZoneStart = new Point(jStartingZone.getJSONObject("begin").getInt("x"), jStartingZone.getJSONObject("begin").getInt("y"));
                Point startingZoneEnd = new Point(jStartingZone.getJSONObject("end").getInt("x"), jStartingZone.getJSONObject("end").getInt("y"));
                Zone startingZone = new Zone(startingZoneStart, startingZoneEnd);
                //Finish Zone parser
                Point finishZoneStart = new Point(jFinishZone.getJSONObject("begin").getInt("x"), jFinishZone.getJSONObject("begin").getInt("y"));
                Point finishZoneEnd = new Point(jFinishZone.getJSONObject("end").getInt("x"), jFinishZone.getJSONObject("end").getInt("y"));
                Zone finishZone = new Zone(finishZoneStart, finishZoneEnd);

                //Drone creating and init drawing
                Drone drone = new Drone("" + id, Color.decode(color), startingZone, finishZone);
                s.obstacles.add(drone);
                gridTable.setValueAt(new Cell(drone.getType(), drone.getNr(), drone.getCol()), drone.getPos().getY(), drone.getPos().getX());
                for (Point p : drone.getFinishZone().getField()) {
                    gridTable.setValueAt(new Cell("wall", drone.getNr(), Color.decode("#bcf7b5")), p.getY(), p.getX());
                }
            }
            MyLogger.log(Level.INFO,"Drones loaded.");
        } catch (Exception e) {
            MyLogger.log(Level.WARNING,"Execution of loadDrones failed.");
            e.printStackTrace();
        }
    }

    private void loadObstacles() {
        MyLogger.log(Level.INFO,"Loading obstacles.");
        try {
            String myJson = new Scanner(obstaclesInput).useDelimiter("\\Z").next();
            JSONArray arr = new JSONArray(myJson);
            for (Object obj : arr) {
                JSONObject jObj = (JSONObject) obj;
                Point point = new Point(jObj.getInt("x"), jObj.getInt("y"));
                Wall obstacle = new Wall(point);
                s.obstacles.add(obstacle);
                gridTable.setValueAt(new Cell("wall", obstacle.getNr(), obstacle.getCol()), obstacle.getPos().getY(), obstacle.getPos().getX());
            }
            MyLogger.log(Level.INFO,"Obstacles loaded.");
        } catch (Exception e) {
            MyLogger.log(Level.WARNING,"Execution of loadObstacles failed.");
            e.printStackTrace();
        }
    }

    private void resetGrid() {
        MyLogger.log(Level.INFO,"Resetting grid.");
        //Table size pre-set (rows and cols width and height)
        DefaultTableModel model = new DefaultTableModel(gridSize, gridSize);
        gridTable.setModel(model);
        for (int i = 0; i < model.getColumnCount(); i++) {
            gridTable.getColumnModel().getColumn(i).setMinWidth(windowSize / gridSize);
            gridTable.getColumnModel().getColumn(i).setMaxWidth(windowSize / gridSize);
            gridTable.getColumnModel().getColumn(i).setCellRenderer(new ButtonRenderer());
        }
        gridTable.setRowHeight(windowSize / gridSize);

        clearGrid();
        MyLogger.log(Level.INFO,"Grid reset.");
    }

    private void resetScenario() {
        MyLogger.log(Level.INFO,"Resetting setup and scenario.");
        s.obstacles.clear();
        MyLogger.log(Level.INFO,"GridObjects cleared.");
        stepNr = 1;
        loadConfig();
        resetGrid();
        loadScenario();
    }

    public static void main(String[] args) {
        //Frame pre-set
        frame.setContentPane(new App().mainPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
    }

    //Clearing grid
    private void clearGrid() {
        for (int x = 0; x < gridTable.getColumnCount() - 1; x++) {
            for (int y = 0; y < gridTable.getRowCount() - 1; y++) {
                gridTable.setValueAt(new Cell(), y, x);
            }
        }
    }

    //Draws finishZone for given GridObject (Drone)
    private void drawFinishZone(GridObject obj) {
        for (Point p : obj.getFinishZone().getField()) {
            if (p.getX() == obj.getPos().getX() && p.getY() == obj.getPos().getY()) continue;
            gridTable.setValueAt(new Cell("wall", obj.getNr(), Color.decode("#bcf7b5")), p.getY(), p.getX());
        }
    }

    //Handling next step
    private void nextStep() {
        MyLogger.log(Level.INFO,"Step nr: " + stepNr++);
        clearGrid();

        //Draw GridObjects (Drones & Walls)
        for (GridObject obj : s.obstacles) {
            gridTable.setValueAt(new Cell(), obj.getPos().getY(), obj.getPos().getX());
            obj.lookForObstacles();
            obj.setIntention();
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

        //Move safe GriObjects (Drones. Walls cant move... duh)
        for (GridObject obj : s.obstacles) {
            obj.move();
            gridTable.setValueAt(new Cell(obj.getType(), obj.getNr(), obj.getCol()), obj.getPos().getY(), obj.getPos().getX());
        }

        //Draw finishZones
        for (GridObject obj : s.obstacles) {
            drawFinishZone(obj);
        }

    }
}
