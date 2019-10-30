package pl.edu.utp.com;

import java.util.ArrayList;

public class EntitiesSingleton {
    private static EntitiesSingleton single_instance = null;

    public static EntitiesSingleton getInstance() {
        if (single_instance == null)
        {
            single_instance = new EntitiesSingleton();
        }
        return single_instance;
    }

    public ArrayList<GridObject> entitiesList;

    private EntitiesSingleton() {
        entitiesList = new ArrayList<>();
    }
}
