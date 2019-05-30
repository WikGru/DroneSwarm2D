import java.util.ArrayList;

public class ObstacleSingleton {
    private static ObstacleSingleton single_instance = null;

    public static ObstacleSingleton getInstance() {
        if (single_instance == null)
        {
            single_instance = new ObstacleSingleton();
        }
        return single_instance;
    }

    public ArrayList<Drone> obstacles;

    public ObstacleSingleton() {
        obstacles = new ArrayList<>();
    }
}
