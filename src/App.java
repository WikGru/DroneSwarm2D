import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

//This is the main App class used to run the simulation
public class App {
    private JPanel mainPanel;
    private JTable gridTable;
    private JPanel gridPanel;
    private JPanel userPanel;
    private JButton nextStepButton;
    private ObstacleSingleton s = ObstacleSingleton.getInstance();

    public App() {
        //Size of displayed window
        int windowSize = 500;
        //Amount of rows and cols
        int gridSize = 10;
        //Amount of placed drones on grid (all are magenta at the moment)
        int droneAmount = 5;

        //Table size pre-set (rows and cols width and height)
        DefaultTableModel model = new DefaultTableModel(gridSize, gridSize);
        gridTable.setModel(model);
        for (int i = 0; i < model.getColumnCount(); i++) {
            gridTable.getColumnModel().getColumn(i).setMinWidth(windowSize / gridSize);
            gridTable.getColumnModel().getColumn(i).setMaxWidth(windowSize / gridSize);
            gridTable.getColumnModel().getColumn(i).setCellRenderer(new ButtonRenderer());
        }
        gridTable.setRowHeight(windowSize / gridSize);


        //NextStep actionListener
        nextStepButton.addActionListener(e -> nextStep());

        //Drone placement (by hand by now/ will be from file .JSON)
        Random rand = new Random();
        Point start;
        Point finish;
        for (int i = 0; i < droneAmount; i++) {
            start = new Point(rand.nextInt(gridSize - 1), rand.nextInt(gridSize - 1));
            finish = new Point(rand.nextInt(gridSize - 1), rand.nextInt(gridSize - 1));
            Drone drone = new Drone("" + i, Color.decode("#FF00FF"), start, finish);
            s.obstacles.add(drone);
            gridTable.setValueAt(new Cell(drone.getNr(), drone.getCol()), drone.getPos().getY(), drone.getPos().getX());
            gridTable.setValueAt(new Cell(drone.getNr(), Color.green), drone.getFinishZone().getP1().getY(), drone.getFinishZone().getP1().getX());
        }

        //Wall placement (by hand now/ will be from file .JSON)
        for (int i = 0; i < 2; i++) {
            Wall wall = new Wall(new Point(i, 1));
            s.obstacles.add(wall);
            gridTable.setValueAt(new Cell("", wall.getCol()), wall.getPos().getY(), wall.getPos().getX());
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
        //Clearing grid
        for (int x = 0; x < gridTable.getColumnCount() - 1; x++) {
            for (int y = 0; y < gridTable.getRowCount() - 1; y++) {
                gridTable.setValueAt(new Cell(), y, x);
            }
        }

        //Input finishZones
        for (GridObject obj : s.obstacles) {
            gridTable.setValueAt(new Cell(obj.getNr(), Color.green), obj.getFinishZone().getP1().getY(), obj.getFinishZone().getP1().getX());
        }

        //Input physical objects (Drones & Walls)
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

        //Move safe physical objects
        for (GridObject obj : s.obstacles) {
            obj.move();
            gridTable.setValueAt(new Cell(obj.getNr(), obj.getCol()), obj.getPos().getY(), obj.getPos().getX());
        }
    }
}
