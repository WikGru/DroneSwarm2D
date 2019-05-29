import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class App {
    private JPanel mainPanel;
    private JTable gridTable;
    private JPanel gridPanel;
    private JPanel userPanel;
    private JButton nextStepButton;
    private ArrayList<Drone> droneList = new ArrayList<>();
    private ArrayList<Point> takenSpots = new ArrayList<>();

    public App() {
        int windowSize = 700;
        int gridSize = 40;

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
        for (int i = 0; i < 20; i++) {
            start = new Point(rand.nextInt(gridSize - 1), rand.nextInt(gridSize - 1));
            finish = new Point(rand.nextInt(gridSize - 1), rand.nextInt(gridSize - 1));
            Drone drone = new Drone(12, Color.decode("#FF00FF"), start, finish);
            droneList.add(drone);
            gridTable.setValueAt(drone.getCol(), drone.getPos().getY(), drone.getPos().getX());
            gridTable.setValueAt(Color.green, drone.getFinishZone().getP1().getY(), drone.getFinishZone().getP1().getX());
        }
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
        double trigger = Math.sqrt(2.0) / 2;
        int x = 0;
        int y = 0;
        double ratio = 0;
        Point diff;
        takenSpots = new ArrayList<>();

        for (Drone drone : droneList) {
            gridTable.setValueAt(Color.green, drone.getFinishZone().getP1().getY(), drone.getFinishZone().getP1().getX());
            gridTable.setValueAt(Color.decode("#ebebeb"), drone.getPos().getY(), drone.getPos().getX());
            diff = drone.countDiff();


            if (diff.getX() != 0) {
                x = Math.abs(diff.getX()) / diff.getX();
            }
            if (diff.getY() != 0) {
                y = Math.abs(diff.getY()) / diff.getY();
            }

            if (Math.abs(diff.getX()) >= Math.abs(diff.getY())) {
                ratio = (double) diff.getY() / (double) diff.getX();
                if (Math.abs(ratio) < trigger) {
                    y = 0;
                }
            } else if (Math.abs(diff.getX()) < Math.abs(diff.getY())) {
                ratio = (double) diff.getX() / (double) diff.getY();
                if (Math.abs(ratio) < trigger) {
                    x = 0;
                }
            }
            if (diff.getY() == 0 && diff.getX() == 0) {
                x = 0;
                y = 0;
            }
            drone.move(new Point(x, y));
            gridTable.setValueAt(drone.getCol(), drone.getPos().getY(), drone.getPos().getX());

            takenSpots.add(drone.getPos());
        }

        manageCollisions();
    }

    private void manageCollisions() {
        ArrayList<Drone> dronesToDelete = new ArrayList<>();

        //Same spot collision
        for (Drone drone : droneList) {
            int counter = 0;
            for (Point spot : takenSpots) {
                if (spot.getX() == drone.getPos().getX() && spot.getY() == drone.getPos().getY()) {
                    counter++;
                }
            }
            if (counter > 1) {
                dronesToDelete.add(drone);
            }

        }
        //End of same spot collision

        for (Drone drone : dronesToDelete) {
            droneList.remove(drone);
            gridTable.setValueAt(Color.decode("#ebebeb"), drone.getPos().getY(), drone.getPos().getX());
            System.out.println("Collision on spot: " + drone.getPos().getX() + "\t" + drone.getPos().getY());
        }

    }
}
