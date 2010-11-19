package org.mypomodoro.gui.activities;

import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;

/**
 * Table Model for the ActivityList table.
 */
public class ActivityTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String[] columnNames = { "Date", "Name", "Type", "Estpomo", "ID" };
	Object[][] tableData;;

	public ActivityTableModel() {
		populateData();
	}

	/**
	 * Populates the table from the database.
	 */
	private void populateData() {
		ActivityList ac = ActivityList.getList();

		int rowIndex = ac.size();
		int colIndex = columnNames.length;
		tableData = new Object[rowIndex][colIndex];
		Iterator<Activity> iterator = ac.iterator();
		for (int i = 0; iterator.hasNext(); i++) {
			Activity a = iterator.next();
			tableData[i][0] = a.getDate();
			tableData[i][1] = a.getName();
			tableData[i][2] = a.getType();
			tableData[i][3] = a.getEstimatedPoms();
			tableData[i][4] = a.getId();
		}
	}

	@Override
	public int getRowCount() {
		return tableData.length;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= 0)
			return tableData[rowIndex][columnIndex];
		else
			return null;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		tableData[rowIndex][columnIndex] = aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
	}
}
