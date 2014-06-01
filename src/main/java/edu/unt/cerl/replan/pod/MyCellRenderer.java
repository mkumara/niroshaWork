package edu.unt.cerl.replan.pod;


import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

 class MyCellRenderer extends JLabel implements ListCellRenderer {
     public MyCellRenderer() {
         setOpaque(true);
     }
    @Override
     public Component getListCellRendererComponent(
         JList list,
         Object value,
         int index,
         boolean isSelected,
         boolean cellHasFocus)
     {
         setText(value.toString());
         setBackground(isSelected ? Color.red : Color.white);
         setForeground(isSelected ? Color.white : Color.black);
         return this;
     }
 }