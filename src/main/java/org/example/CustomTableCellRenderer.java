package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component  component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(value == null || value.equals("")){
            component.setBackground(Color.YELLOW);
        }
        else component.setBackground(table.getBackground());

        return component;
    }
}
