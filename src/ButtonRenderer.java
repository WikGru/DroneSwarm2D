import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * @version 1.0 11/09/98
 */
public class ButtonRenderer extends JButton implements TableCellRenderer {

    public ButtonRenderer() {
        setBorderPainted(false);
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Cell obj = (Cell) value;
        if (obj == null) {
            obj = new Cell();
        }
        setBackground(obj.getCol());
        setText(obj.getId());
        return this;
    }
}