import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class App {
    private JPanel mainPanel;
    private JTable gridTable;
    private JPanel gridPanel;
    private JPanel userPanel;
    private JButton nextStepButton;
    private ObstacleSingleton s = ObstacleSingleton.getInstance();
    private ArrayList<Point> takenSpots = new ArrayList<>();

    public App() {
        int windowSize = 500;
        int gridSize = 15;
        int droneAmount = 5;

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

        Random rand = new Random();
        Point start;
        Point finish;
        for (int i = 0; i < droneAmount; i++) {
            start = new Point(rand.nextInt(gridSize - 1), rand.nextInt(gridSize - 1));
            finish = new Point(rand.nextInt(gridSize - 1), rand.nextInt(gridSize - 1));
            Drone drone = new Drone(12, Color.decode("#FF00FF"), start, finish);
            s.obstacles.add(drone);
            gridTable.setValueAt(drone.getCol(), drone.getPos().getY(), drone.getPos().getX());
            gridTable.setValueAt(Color.green, drone.getFinishZone().getP1().getY(), drone.getFinishZone().getP1().getX());
        }

//        Drone drone = new Drone(12, Color.decode("#FF00FF"), new Point(4,10), new Point(10,10));
//        Drone drone1 = new Drone(123, Color.decode("#FF00FF"), new Point(10,10), new Point(4,10));
//                    s.obstacles.add(drone);
//            s.obstacles.add(drone1);

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().mainPanel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
    }


    private void nextStep() {
        for (int x = 0; x < gridTable.getColumnCount() - 1; x++) {
            for (int y = 0; y < gridTable.getRowCount() - 1; y++) {
                gridTable.setValueAt(Color.decode("#ebebeb"), y, x);
            }
        }

        for (GridObject obj : s.obstacles) {
            gridTable.setValueAt(Color.green, obj.getFinishZone().getP1().getY(), obj.getFinishZone().getP1().getX());
            gridTable.setValueAt(Color.decode("#ebebeb"), obj.getPos().getY(), obj.getPos().getX());
            obj.lookForObstacles();
            obj.setIntention();
        }

        for (GridObject obj : s.obstacles) {
            obj.manageCollisions();
        }

        ArrayList<GridObject> toRemove = new ArrayList<>();
        for (GridObject obj : s.obstacles) {
            if (obj.isDead()) toRemove.add(obj);
        }

        for (GridObject obj : toRemove) {
            s.obstacles.remove(obj);
        }

        for (GridObject obj : s.obstacles) {
            obj.move();
            gridTable.setValueAt(obj.getCol(), obj.getPos().getY(), obj.getPos().getX());
        }


    }
}
