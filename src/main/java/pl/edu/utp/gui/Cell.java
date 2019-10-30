package pl.edu.utp.gui;

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

    String getType() {
        return type;
    }

    String getId() {
        return id;
    }

    Color getCol() {
        return col;
    }

}
