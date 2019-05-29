import java.awt.*;

public class Wall {
    private Color col;
    private Point pos;

    public Wall(Point pos) {
        this.col = Color.DARK_GRAY;
        this.pos = pos;
    }


    public Color getCol() {
        return col;
    }

    public void setCol(Color col) {
        this.col = col;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }
}
