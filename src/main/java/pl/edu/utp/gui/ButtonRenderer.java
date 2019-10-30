package pl.edu.utp.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class ButtonRenderer extends JButton implements TableCellRenderer {
    private Color col;
    private String type = "";

    public ButtonRenderer() {
        setBorderPainted(false);
        setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        int diameter = 40;
        int radius = diameter / 2;

        if (type.equals("drone")) {
            g.setColor(col);
            g.drawOval(getWidth() / 2 - radius, getHeight() / 2 - radius, diameter, diameter);
            g.fillOval(getWidth() / 2 - radius, getHeight() / 2 - radius, diameter, diameter);
            g.setFont(getFont());
            FontMetrics metrics = g.getFontMetrics(getFont());
            int stringWidth = metrics.stringWidth(getText());
            int stringHeight = metrics.getHeight();
            g.setColor(Color.BLACK);
            g.drawString(getText(), getWidth() / 2 - stringWidth / 2, getHeight() / 2 + stringHeight / 4);
        } else {
            super.paintComponent(g);
        }
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Cell obj = (Cell) value;
        if (obj == null) {
            obj = new Cell();
        }

        col = obj.getCol();
        if (obj.getType() != null) {
            type = obj.getType();
        }


        setBackground(obj.getCol());
        setText(obj.getId());
        return this;
    }
}