package org.example;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CustomTableCellRendererForDuplicates extends DefaultTableCellRenderer {
    private static List<LaptopEntity> laptopsShown;
    private boolean isRowDuplicate;

    private static Set<Integer> editedRows = new HashSet<>();

    private String regex;

    public CustomTableCellRendererForDuplicates() {
        this.regex = null;
    }

    public CustomTableCellRendererForDuplicates(String regex) {
        this.regex = regex;
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        isRowDuplicate = false;
        LaptopEntity tempLaptop = new LaptopEntity(
                table.getModel().getValueAt(row, 0) == null ? "" : table.getModel().getValueAt(row, 0).toString(),
                table.getModel().getValueAt(row, 1) == null ? "" : table.getModel().getValueAt(row, 1).toString(),
                table.getModel().getValueAt(row, 2) == null ? "" : table.getModel().getValueAt(row, 2).toString(),
                table.getModel().getValueAt(row, 3) == null ? "" : table.getModel().getValueAt(row, 3).toString(),
                table.getModel().getValueAt(row, 4) == null ? "" : table.getModel().getValueAt(row, 4).toString(),
                table.getModel().getValueAt(row, 5) == null ? "" : table.getModel().getValueAt(row, 5).toString(),
                table.getModel().getValueAt(row, 6) == null ? "" : table.getModel().getValueAt(row, 6).toString(),
                table.getModel().getValueAt(row, 7) == null ? "" : table.getModel().getValueAt(row, 7).toString(),
                table.getModel().getValueAt(row, 8) == null ? "" : table.getModel().getValueAt(row, 8).toString(),
                table.getModel().getValueAt(row, 9) == null ? "" : table.getModel().getValueAt(row, 9).toString(),
                table.getModel().getValueAt(row, 10) == null ? "" : table.getModel().getValueAt(row, 10).toString(),
                table.getModel().getValueAt(row, 11) == null ? "" : table.getModel().getValueAt(row, 11).toString(),
                table.getModel().getValueAt(row, 12) == null ? "" : table.getModel().getValueAt(row, 12).toString(),
                table.getModel().getValueAt(row, 13) == null ? "" : table.getModel().getValueAt(row, 13).toString(),
                table.getModel().getValueAt(row, 14) == null ? "" : table.getModel().getValueAt(row, 14).toString()
        );
        if(laptopsShown != null && !laptopsShown.isEmpty()){
            isRowDuplicate = laptopsShown.stream().anyMatch(x -> x.equals(tempLaptop));
        }

        if(editedRows.contains(row)){
            component.setBackground(Color.white);
        }
        else if(isRowDuplicate){
            component.setBackground(Color.RED);
            //System.out.println("Znaleziono duplikat");
        }
        else {
            component.setBackground(Color.GRAY);
        }

        if(value == null || value.equals("")){
            component.setBackground(Color.YELLOW);
        }
        else if (regex != null && !value.toString().matches(regex)){
            component.setBackground(Color.ORANGE);
        }



        return component;
    }

    public void addEditedRow(int index){
        editedRows.add(index);
    }

    public void clearEditedRows(){
        editedRows.clear();
    }

    public static void setLaptopsShown(List<LaptopEntity> laptopsShown) {
        CustomTableCellRendererForDuplicates.laptopsShown = laptopsShown;
    }
}
