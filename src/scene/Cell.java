package scene;

import java.awt.*;
//This class is used to pass data to table as a scene.Cell package
public class Cell {
    private String id;
    private Color col;

    public Cell() {
        this.id = "";
        this.col = Color.decode("#EBEBEB");
    }

    public Cell(String id, Color col) {
        this.id = id;
        this.col = col;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Color getCol() {
        return col;
    }

    public void setCol(Color col) {
        this.col = col;
    }
}
