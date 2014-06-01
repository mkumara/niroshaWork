/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.unt.cerl.replan.modules.type2Vulnerabilities.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author sarat
 */
public class Type2TableModel extends AbstractTableModel {

    ArrayList<String> columnNames;
    List<List<String>> listOfLists;
    int numColumns;

    public Type2TableModel() {
        columnNames = new ArrayList<String>();
        listOfLists = new ArrayList<List<String>>();
        numColumns = 0;
    }

    @Override
    public int getRowCount() {
        return listOfLists.size();
    }

    @Override
    public int getColumnCount() {
        return numColumns;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return listOfLists.get(rowIndex).get(columnIndex);
//        String ret;
//        if (listOfLists.get(rowIndex).get(columnIndex) != null) {
//            ret = listOfLists.get(rowIndex).get(columnIndex);
//        } else {
//            ret = "0";
//        }
//        return ret;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        String el = (String) value;
        listOfLists.get(row).set(column, el);
        fireTableCellUpdated(row, column);

    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    public void addRow(ArrayList<String> newRow) {

        listOfLists.add(newRow);
        int row = listOfLists.size();
        fireTableRowsInserted(row - 1, row - 1);

    }
    
    public void updateRowStructure(int numFieldsToAdd) {
        Iterator<List<String>> itRows = listOfLists.iterator();
        while(itRows.hasNext()) {
            List<String> row = itRows.next();
            for(int i=0; i<numFieldsToAdd; i++){
            row.add("-");
            }
        }
        
    }

    public void addColumn(String colName) {
        columnNames.add(colName);
        numColumns++;
    }

}
