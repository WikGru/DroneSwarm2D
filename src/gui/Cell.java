package gui;

import java.awt.*;
//This class is used to pass data to table as a Cell package
public class Cell {
    private String type;
    private String id;
    private Color col;

    public Cell() {
        this.id = "";
        this.col = Color.decode("#EBEBEB");
    }

    public Cell(String type, String id, Color col) {
        this.type = type;
        this.id = id;
        this.col = col;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
