package org.mypomodoro.gui.activities;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;

/**
 * GUI for viewing what is in the ActivityList. This can be changed later. Right
 * now it uses a TableModel to build the JTable. Table Listeners can be added to
 * save cell edits to the ActivityCollection which can then be saved to the data
 * layer.
 * 
 * @author Brian Wetzel
 */
public class ActivitiesPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	JTable table = new JTable(getTableModel());

	private boolean shouldRefresh = false;
	private static final String[] columnNames = { "Date", "Name", "Type",
			"Estpomo", "ID" };
	public static final int ID_KEY = 4;

	public ActivitiesPanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		setBorder(new TitledBorder(new EtchedBorder(), "Activity List"));

		// Add the table
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(new JScrollPane(table), gbc);

		// Add the Control Panel
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0.2;
		JTabbedPane controlPane = new JTabbedPane();
		add(controlPane, gbc);

		DetailsPane detailsPane = new DetailsPane(table);
		controlPane.add("Details", detailsPane);
		controlPane.add("Create Task", new JScrollPane(new CreatePanel()));

		showSelectedItemDetails(detailsPane);
	}

	private static TableModel getTableModel() {
		return new AbstractActivitiesTableModel(columnNames, ActivityList
				.getList()) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateData(AbstractActivities activities) {
				int rowIndex = activities.size();
				int colIndex = columnNames.length;
				tableData = new Object[rowIndex][colIndex];
				Iterator<Activity> iterator = activities.iterator();
				for (int i = 0; iterator.hasNext(); i++) {
					Activity a = iterator.next();
					tableData[i][0] = a.getDate();
					tableData[i][1] = a.getName();
					tableData[i][2] = a.getType();
					tableData[i][3] = a.getEstimatedPoms();
					tableData[i][4] = a.getId();
				}
			}
		};
	}

	private void showSelectedItemDetails(final DetailsPane detailsPane) {
		table.getSelectionModel().addListSelectionListener(
				new ActivityInformationTableListener(ActivityList.getList(),
						table, detailsPane, ID_KEY));
	}

	public void refresh() {
		if (shouldRefresh) {
			shouldRefresh = false;
			System.out.println("Update");
			table.setModel(getTableModel());
		}
	}

	JTable getTable() {
		return table;
	}
}
