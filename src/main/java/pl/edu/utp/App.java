package pl.edu.utp;

import pl.edu.utp.com.Drone;
import pl.edu.utp.com.GridObject;
import pl.edu.utp.com.EntitiesSingleton;
import pl.edu.utp.com.Wall;
import org.json.JSONArray;
import org.json.JSONObject;
import pl.edu.utp.gui.ButtonRenderer;
import pl.edu.utp.gui.Cell;
import pl.edu.utp.com.Point;
import pl.edu.utp.com.Zone;
import pl.edu.utp.behaviour.Behaviour;
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
    private JRadioButton blindIntentionRbtn;
    private JRadioButton avoidingIntentionRbtn;

    private File dronesInput = new File("scenarioDrones.json");
    private File obstaclesInput = new File("scenarioObstacles.json");
    private EntitiesSingleton entitiesSingleton = EntitiesSingleton.getInstance();
    private int windowSize;
    private int gridSize = 1;
    private int stepNr = 1;

    public App() {
        ButtonGroup group = new ButtonGroup();
        group.add(blindIntentionRbtn);
        group.add(avoidingIntentionRbtn);

        MyLogger.log(Level.INFO, "Starting initialization");

        frame.setResizable(false);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        windowSize = screenSize.height * 3 / 5;
        resetScenario();
        //Reset actionListener
        resetButton.addActionListener(e -> resetScenario());
        //NextStep actionListener
        nextStepButton.addActionListener(e -> nextStep());

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
            drawEntities();
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

        MyLogger.log(Level.INFO, "Initialization finished.");
    }

    private void loadConfig() {
        String configInput = "config.json";
        try (Scanner myScanner = new Scanner(new File(configInput))) {
            String myJson = myScanner.useDelimiter("\\Z").next();
            JSONObject obj = new JSONObject(myJson);
            gridSize = obj.getInt("gridSize");
        } catch (Exception e) {
            MyLogger.log(Level.WARNING, "Loading config failed.");
            MyLogger.log(Level.WARNING, e.getMessage());
        }
    }

    private void loadScenario() {
        resetGrid();
        entitiesSingleton.clearEntitiesList();
        try {
            loadDrones();
            loadObstacles();
        } catch (Exception e) {
            MyLogger.log(Level.WARNING, "Encountered problem while loading scenario.");
            MyLogger.log(Level.WARNING, e.getMessage());
            resetGrid();
        }
    }

    private void loadDrones() {
        try (Scanner myScanner = new Scanner(dronesInput)) {
            String myJson = myScanner.useDelimiter("\\Z").next();
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
                entitiesSingleton.addEntityToList(drone);
            }
            drawEntities();
        } catch (Exception e) {
            entitiesSingleton.clearEntitiesList();
            MyLogger.log(Level.WARNING, "Execution of loadDrones failed.");
            MyLogger.log(Level.WARNING, e.getMessage());
        }
    }

    private void loadObstacles() {
        try (Scanner myScanner = new Scanner(obstaclesInput)) {
            String myJson = myScanner.useDelimiter("\\Z").next();
            JSONArray arr = new JSONArray(myJson);
            for (Object obj : arr) {
                JSONObject jObj = (JSONObject) obj;
                Point point = new Point(jObj.getInt("x"), jObj.getInt("y"));
                Wall obstacle = new Wall(point);
                entitiesSingleton.addEntityToList(obstacle);
                gridTable.setValueAt(new Cell("wall", obstacle.getNr(), obstacle.getCol()), obstacle.getPos().getY(), obstacle.getPos().getX());
            }
        } catch (Exception e) {
            entitiesSingleton.clearEntitiesList();
            MyLogger.log(Level.WARNING, "Encountered problem while loading obstacles.");
            MyLogger.log(Level.WARNING, e.getMessage());
        }
    }

    private void resetGrid() {
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
        entitiesSingleton.clearEntitiesList();
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
        entitiesSingleton.getEntitiesList().forEach(this::drawFinishZone);
        //Draw GridObjects (Drones & Walls)
        entitiesSingleton.getEntitiesList().forEach(entity -> {
            Cell cell = new Cell(entity.getType(), entity.getNr(), entity.getCol());
            gridTable.setValueAt(cell, entity.getPos().getY(), entity.getPos().getX());
        });
    }

    private void manageCollisions() {
        //Manage drone collisions
        entitiesSingleton.getEntitiesList().forEach(GridObject::lookForObstacles);
        Behaviour behaviour;
        if (blindIntentionRbtn.isSelected()) {
            behaviour = Behaviour.DEFAULT;
        } else {
            behaviour = Behaviour.AVOIDING;
        }
        entitiesSingleton.getEntitiesList().forEach(go -> go.setIntention(behaviour));
        entitiesSingleton.getEntitiesList().forEach(GridObject::manageCollisions);
        removeDeadDrones();
    }

    private void removeDeadDrones() {
        //Remove drones that will collide
        ArrayList<GridObject> entitiesToRemove = entitiesSingleton.getEntitiesList().stream()
                .filter(GridObject::isDead).collect(Collectors.toCollection(ArrayList::new));
        entitiesToRemove.forEach(entity -> entitiesSingleton.removeEntityFromList(entity));
    }

    private void moveDrones() {
        //Move safe GridObjects (Drones. Walls cant move... duh)
        entitiesSingleton.getEntitiesList().forEach(GridObject::move);
    }

    //Handling next step
    private void nextStep() {
        MyLogger.log(Level.INFO, "Step nr: " + stepNr);

        clearGrid();
        manageCollisions();
        moveDrones();
        drawEntities();

        MyLogger.log(Level.INFO, "End of step nr: " + stepNr++);
    }
}
