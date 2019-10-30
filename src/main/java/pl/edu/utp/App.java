package pl.edu.utp;

import pl.edu.utp.com.Drone;
import pl.edu.utp.com.GridObject;
import pl.edu.utp.com.EntitiesSingleton;
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
import java.util.stream.Collectors;

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
    private JButton nextStepButton;
    private JButton resetButton;
    private JButton selectScenarioButton;
    private JButton selectEnvironmentButton;
    private JButton editConfigurationButton;

    private File dronesInput = new File("scenarioDrones.json");
    private File obstaclesInput = new File("scenarioObstacles.json");
    private EntitiesSingleton entitiesSingleton = EntitiesSingleton.getInstance();
    private int windowSize;
    private int gridSize = 1;
    private int stepNr = 1;

    public App() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tH:%1$tM:%1$tS.%1$tL] [%4$-7s] %5$entitiesSingleton %n");

        MyLogger.log(Level.INFO, "Setup app settings and scenarios");

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

        MyLogger.log(Level.INFO, "App setup finished.");
    }

    private void loadConfig() {
        MyLogger.log(Level.INFO, "Loading config.");
        try {
            String configInput = "config.json";
            String myJson = new Scanner(new File(configInput)).useDelimiter("\\Z").next();
            JSONObject obj = new JSONObject(myJson);
            gridSize = obj.getInt("gridSize");
            // This block configure the logger with handler and formatter

            MyLogger.log(Level.INFO, "Config loaded.");
        } catch (Exception e) {
            MyLogger.log(Level.WARNING, "Execution of loadConfig failed.");
            e.printStackTrace();
        }
    }

    private void loadScenario() {
        MyLogger.log(Level.INFO, "Loading scenario.");
        resetGrid();
        entitiesSingleton.entitiesList.clear();
        try {
            loadDrones();
            loadObstacles();
        } catch (Exception e) {
            resetGrid();
        }
    }

    private void loadDrones() {
        MyLogger.log(Level.INFO, "Loading drones.");
        try {
            String myJson = new Scanner(dronesInput).useDelimiter("\\Z").next();
            JSONArray arr = new JSONArray(myJson);
            String sBegin = "begin";
            String sEnd = "end";
            for (Object obj : arr) {
                JSONObject jObj = (JSONObject) obj;

                int id = jObj.getInt("id");
                String color = jObj.getString("color");
                JSONObject jStartingZone = jObj.getJSONObject("startingZone");
                JSONObject jFinishZone = jObj.getJSONObject("finishZone");

                //Starting Zone parser
                Point startingZoneStart = new Point(jStartingZone.getJSONObject(sBegin).getInt("x"), jStartingZone.getJSONObject(sBegin).getInt("y"));
                Point startingZoneEnd = new Point(jStartingZone.getJSONObject(sEnd).getInt("x"), jStartingZone.getJSONObject(sEnd).getInt("y"));
                Zone startingZone = new Zone(startingZoneStart, startingZoneEnd);
                //Finish Zone parser
                Point finishZoneStart = new Point(jFinishZone.getJSONObject(sBegin).getInt("x"), jFinishZone.getJSONObject(sBegin).getInt("y"));
                Point finishZoneEnd = new Point(jFinishZone.getJSONObject(sEnd).getInt("x"), jFinishZone.getJSONObject(sEnd).getInt("y"));
                Zone finishZone = new Zone(finishZoneStart, finishZoneEnd);

                //Drone creating and init drawing
                Drone drone = new Drone("" + id, Color.decode(color), startingZone, finishZone);
                entitiesSingleton.entitiesList.add(drone);
                gridTable.setValueAt(new Cell(drone.getType(), drone.getNr(), drone.getCol()), drone.getPos().getY(), drone.getPos().getX());
                for (Point p : drone.getFinishZone().getField()) {
                    gridTable.setValueAt(new Cell("wall", drone.getNr(), Color.decode("#bcf7b5")), p.getY(), p.getX());
                }
            }
            MyLogger.log(Level.INFO, "Drones loaded.");
        } catch (Exception e) {
            entitiesSingleton.entitiesList.clear();
            MyLogger.log(Level.WARNING, "Execution of loadDrones failed.");
            e.printStackTrace();
        }
    }

    private void loadObstacles() {
        MyLogger.log(Level.INFO, "Loading entitiesList.");
        try {
            String myJson = new Scanner(obstaclesInput).useDelimiter("\\Z").next();
            JSONArray arr = new JSONArray(myJson);
            for (Object obj : arr) {
                JSONObject jObj = (JSONObject) obj;
                Point point = new Point(jObj.getInt("x"), jObj.getInt("y"));
                Wall obstacle = new Wall(point);
                entitiesSingleton.entitiesList.add(obstacle);
                gridTable.setValueAt(new Cell("wall", obstacle.getNr(), obstacle.getCol()), obstacle.getPos().getY(), obstacle.getPos().getX());
            }
            MyLogger.log(Level.INFO, "Obstacles loaded.");
        } catch (Exception e) {
            entitiesSingleton.entitiesList.clear();
            MyLogger.log(Level.WARNING, "Execution of loadObstacles failed.");
            e.printStackTrace();
        }
    }

    private void resetGrid() {
        MyLogger.log(Level.INFO, "Resetting grid.");
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
        MyLogger.log(Level.INFO, "Grid reset.");
    }

    private void resetScenario() {
        MyLogger.log(Level.INFO, "Resetting setup and scenario.");
        entitiesSingleton.entitiesList.clear();
        MyLogger.log(Level.INFO, "GridObjects cleared.");
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
        for (int x = 0; x < gridTable.getColumnCount(); x++) {
            for (int y = 0; y < gridTable.getRowCount(); y++) {
                gridTable.setValueAt(new Cell(), y, x);
            }
        }
    }

    //Draws finishZone for given GridObject (Drone)
    private void drawFinishZone(GridObject obj) {
        obj.getFinishZone().getField().stream()
                .filter(p -> p.getX() != obj.getPos().getX() || p.getY() != obj.getPos().getY()).forEachOrdered(p -> {
            Cell cell = new Cell("wall", obj.getNr(), Color.decode("#bcf7b5"));
            gridTable.setValueAt(cell, p.getY(), p.getX());
        });
    }

    private void drawEntities() {
        //Draw finishZones
        entitiesSingleton.entitiesList.forEach(this::drawFinishZone);
        //Draw GridObjects (Drones & Walls)
        entitiesSingleton.entitiesList.forEach(entity -> {
            gridTable.setValueAt(new Cell(), entity.getPos().getY(), entity.getPos().getX());
            entity.lookForObstacles();
            entity.setIntention();
        });
    }

    private void manageCollisions() {
        //Manage drone collisions
        entitiesSingleton.entitiesList.forEach(GridObject::manageCollisions);
        removeDeadDrones();
    }

    private void removeDeadDrones() {
        //Remove drones that will collide
        ArrayList<GridObject> entitiesToRemove = entitiesSingleton.entitiesList.stream()
                .filter(GridObject::isDead).collect(Collectors.toCollection(ArrayList::new));
        entitiesToRemove.forEach(entity -> entitiesSingleton.entitiesList.remove(entity));
    }

    private void moveDrones() {
        //Move safe GridObjects (Drones. Walls cant move... duh)
        entitiesSingleton.entitiesList.forEach(entity -> {
            entity.move();
            Cell cell = new Cell(entity.getType(), entity.getNr(), entity.getCol());
            gridTable.setValueAt(cell, entity.getPos().getY(), entity.getPos().getX());
        });
    }

    //Handling next step
    private void nextStep() {
        MyLogger.log(Level.INFO, "Step nr: " + stepNr++);

        clearGrid();
        drawEntities();
        manageCollisions();
        moveDrones();
    }
}
