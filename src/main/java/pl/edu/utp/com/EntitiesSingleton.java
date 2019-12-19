package pl.edu.utp.com;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;

public class EntitiesSingleton {
    private static EntitiesSingleton single_instance = null;

    public static EntitiesSingleton getInstance() {
        if (single_instance == null) {
            single_instance = new EntitiesSingleton();
        }
        return single_instance;
    }

    public ArrayList<GridObject> getEntitiesList() {
        return entitiesList;
    }

    public void setEntitiesList(ArrayList<GridObject> entitiesList) {
        this.entitiesList = entitiesList;
    }

    public void addEntityToList(GridObject entity){
        this.entitiesList.add(entity);
    }

    public void removeEntityFromList(GridObject entity){
        this.entitiesList.remove(entity);
    }

    public void clearEntitiesList() {
        this.entitiesList.clear();
    }

    private ArrayList<GridObject> entitiesList;

    private EntitiesSingleton() {
        entitiesList = new ArrayList<>();
    }
}
