package org.mypomodoro.gui;

import javax.swing.table.DefaultTableModel;
import org.mypomodoro.gui.activities.Reorderable;
import org.mypomodoro.model.AbstractActivities;

//public abstract class AbstractActivitiesTableModel extends AbstractTableModel implements Reorderable {
public abstract class AbstractActivitiesTableModel extends DefaultTableModel implements Reorderable {

    private static final long serialVersionUID = 20110814L;
    //protected Object[][] tableData;
    //private final String[] columnNames = null;

    public AbstractActivitiesTableModel(Object[][] tableData, String[] fields) {
        super(tableData, fields);
    }

    public AbstractActivitiesTableModel(String[] fields, AbstractActivities activities) {
        //columnNames = fields;
        //populateData(activities);
    }

    /*@Override
     public Object getValueAt(int rowIndex, int columnIndex) {
     if (rowIndex >= 0 && getRowCount() > 0) {
     return super.getValueAt(rowIndex, columnIndex);
     } else {
     return null;
     }
     }*/
    /**
     * Populates the table from the database.
     *
     * @param activities
     */
    //protected abstract void populateData(AbstractActivities activities);

    /*@Override
     public int getRowCount() {
     return tableData.length;
     }

     @Override
     public int getColumnCount() {
     return columnNames.length;
     }

     @Override
     public Object getValueAt(int rowIndex, int columnIndex) {
     if (rowIndex >= 0 && getRowCount() > 0) {
     return tableData[rowIndex][columnIndex];
     } else {
     return null;
     }
     }

     @Override
     public String getColumnName(int column) {
     return columnNames[column];
     }

     @Override
     public Class<?> getColumnClass(int c) {
     return getValueAt(0, c) != null ? getValueAt(0, c).getClass() : null;
     }

    
     @Override
     public boolean isCellEditable(int rowIndex, int columnIndex) {
     return false;
     }

     @Override
     public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
     tableData[rowIndex][columnIndex] = aValue;
     fireTableCellUpdated(rowIndex, columnIndex);
     }*/
    @Override
    public void reorder(int fromIndex, int toIndex) {
        //Object o = getDataVector().remove(from);
        //getDataVector().add(to, o);
        //fireTableDataChanged();
    }

    //public void removeRow(int rowIndex) {
        /*System.out.println("rowIndex = " + rowIndex);
     for (int i = 0; i < tableData.length; i++) {
     ArrayList<Object> list = new ArrayList<Object>();
     System.out.println("before = " + i);
     if (i != rowIndex) {
     for (int j = 0; j < tableData[i].length; j++) {
     list.add(tableData[i][j]);
     System.out.println("after = " + i);
     }
     tableData[i] = list.toArray(new Object[list.size()]);
     }            
     }*/

    /*Object[][] newObjects = new Object[tableData.length-1][];
        
     System.arraycopy(tableData, 0, newObjects, 0, rowIndex);
     System.arraycopy(tableData, rowIndex+1, newObjects, rowIndex, tableData.length-rowIndex-1);
        
     tableData = newObjects;*/
        //fireTableRowsDeleted(rowIndex, rowIndex);
    //}
    /*public Class getColumnClass(int column) {
     return getValueAt(0, column).getClass();
     }*/
}
