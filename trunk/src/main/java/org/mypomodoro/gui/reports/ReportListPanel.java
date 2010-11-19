package org.mypomodoro.gui.reports;

import java.awt.BorderLayout;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;

/**
 * GUI for viewing the Report List.
 * 
 * @author Brian Wetzel
 */
public class ReportListPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// table for displaying completed activities
	private final JTable table = new JTable(getTableModel());
	// information panel for selected activities
	private final InformationArea informationArea = new InformationArea();
	private final static String[] columnNames = { "Date", "Name",
			"Estimated Poms", "Actual Poms", "# Interruptions", "Unplanned",
			"Voided", "ID" };
	// static variable for selecting the id
	public static final int ID = 7;

	public ReportListPanel() {
		setLayout(new BorderLayout());
		setBorder(new TitledBorder(new EtchedBorder(), "Report List"));
		add(new JScrollPane(table), BorderLayout.NORTH);
		add(new JScrollPane(informationArea), BorderLayout.SOUTH);
		showInfoOnSelectionChange();
	}

	private static TableModel getTableModel() {
		return new AbstractActivitiesTableModel(columnNames, ReportList
				.getList()) {

			/**
					 * 
					 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateData(AbstractActivities activities) {
				AbstractActivities ac = ReportList.getList();
				int rowIndex = ac.size();
				int colIndex = columnNames.length;
				Iterator<Activity> iterator = ac.iterator();
				tableData = new Object[rowIndex][colIndex];
				for (int i = 0; i < ac.size() && iterator.hasNext(); i++) {
					Activity currentActivity = iterator.next();
					tableData[i][0] = currentActivity.getDate();
					tableData[i][1] = currentActivity.getName();
					tableData[i][2] = currentActivity.getEstimatedPoms();
					tableData[i][3] = currentActivity.getActualPoms();
					tableData[i][4] = currentActivity.getNumInterruptions();
					tableData[i][5] = currentActivity.isUnplanned();
					tableData[i][6] = currentActivity.isVoided();
					tableData[i][7] = currentActivity.getId();
				}
			}

		};
	}

	private void showInfoOnSelectionChange() {
		table.getSelectionModel().addListSelectionListener(
				new ActivityInformationTableListener(ReportList.getList(),
						table, informationArea, ID));
	}

	public void refresh() {
		table.setModel(getTableModel());
	}
}
