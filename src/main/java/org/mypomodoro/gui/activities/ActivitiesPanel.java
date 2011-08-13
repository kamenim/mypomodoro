package org.mypomodoro.gui.activities;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import org.mypomodoro.gui.AbstractActivitiesTableModel;
import org.mypomodoro.gui.ActivityEditTableListener;
import org.mypomodoro.gui.ActivityInformationTableListener;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * GUI for viewing what is in the ActivityList. This can be changed later. Right
 * now it uses a TableModel to build the JTable. Table Listeners can be added to
 * save cell edits to the ActivityCollection which can then be saved to the data
 * layer.
 * 
 * @author Brian Wetzel
 * @author Phil Karoo
 */
public class ActivitiesPanel extends JPanel {
	private static final long serialVersionUID = 20110814L;

	JTable table = new JTable(getTableModel());
	private static final String[] columnNames = { "U",
			Labels.getString("Common.Date"), Labels.getString("Common.Title"),
			Labels.getString("Common.Estimated pomodoros"),
			Labels.getString("Common.Type"), "ID" };
	public static final int ID_KEY = 5;
	private int selectedActivityId = 0;
	private int selectedRowIndex = 0;

	public ActivitiesPanel() {
		setLayout(new GridBagLayout());
		init();

		GridBagConstraints gbc = new GridBagConstraints();

		addActivitiesTable(gbc);
		addTabPane(gbc);
	}

	private void addActivitiesTable(GridBagConstraints gbc) {
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 0.5;
		gbc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(table), gbc);

		recordSelectedRowId();
	}

	private void addTabPane(GridBagConstraints gbc) {
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		JTabbedPane controlPane = new JTabbedPane();
		DetailsPane detailsPane = new DetailsPane(table);
		controlPane.add(Labels.getString("Common.Details"), detailsPane);
		EditPanel edit = new EditPanel();
		JScrollPane editPane = new JScrollPane(edit);
		controlPane.add(Labels.getString("Common.Edit"), editPane);
		CommentPanel commentPanel = new CommentPanel(this);
		controlPane.add(Labels.getString("Common.Comment"), commentPanel);
		add(controlPane, gbc);

		showSelectedItemDetails(detailsPane);
		showSelectedItemEdit(edit);
		showSelectedItemComment(commentPanel);
	}

	private static TableModel getTableModel() {
		AbstractActivitiesTableModel tableModel = new AbstractActivitiesTableModel(
				columnNames, ActivityList.getList()) {
			private static final long serialVersionUID = 20110814L;

			@Override
			protected void populateData(AbstractActivities activities) {
				int rowIndex = activities.size();
				int colIndex = columnNames.length;
				tableData = new Object[rowIndex][colIndex];
				Iterator<Activity> iterator = activities.iterator();
				for (int i = 0; iterator.hasNext(); i++) {
					Activity a = iterator.next();
					tableData[i][0] = a.isUnplanned();
					tableData[i][1] = DateUtil.getFormatedDate(a.getDate());
					tableData[i][2] = a.getName();
					String poms = "" + a.getEstimatedPoms();
					if (a.getOverestimatedPoms() > 0) {
						poms += " + " + a.getOverestimatedPoms();
					}
					tableData[i][3] = poms;
					tableData[i][4] = a.getType();
					tableData[i][5] = a.getId();
				}
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				if (columnIndex == ID_KEY - 3 || columnIndex == ID_KEY - 1) { // make
																				// Title
																				// and
																				// Type
																				// columns
																				// editable
					return true;
				} else {
					return false;
				}
			}
		};

		tableModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				TableModel model = (TableModel) e.getSource();
				// String columnName = model.getColumnName(column);
				Object data = model.getValueAt(row, column);
				Integer ID = (Integer) model.getValueAt(row, ID_KEY); // ID
				Activity act = Activity.getActivity(ID.intValue());
				String sData = (String) data;
				if (column == ID_KEY - 3 && sData.length() > 0) { // Title
																	// (cannot
																	// be empty)
					act.setName(sData);
					act.databaseUpdate();
				} else if (column == ID_KEY - 1) { // Type
					act.setType(sData);
					act.databaseUpdate();
				}
				ActivityList.getList().update(); // always refresh list
			}
		});

		return tableModel;
	}

	private void recordSelectedRowId() {
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {

					@Override
					public void valueChanged(ListSelectionEvent e) {
						int row = table.getSelectedRow();
						if (row > -1) {
							selectedActivityId = (Integer) table.getModel()
									.getValueAt(row, ID_KEY); // ID
							selectedRowIndex = row;
						}
					}
				});
	}

	private void showSelectedItemDetails(DetailsPane detailsPane) {
		table.getSelectionModel().addListSelectionListener(
				new ActivityInformationTableListener(ActivityList.getList(),
						table, detailsPane, ID_KEY));
	}

	private void showSelectedItemEdit(EditPanel editPane) {
		table.getSelectionModel().addListSelectionListener(
				new ActivityEditTableListener(ActivityList.getList(), table,
						editPane, ID_KEY));
	}

	private void showSelectedItemComment(CommentPanel commentPanel) {
		table.getSelectionModel().addListSelectionListener(
				new ActivityInformationTableListener(ActivityList.getList(),
						table, commentPanel, ID_KEY));
	}

	public void refresh() {
		try {
			table.setModel(getTableModel());
		} catch (Exception e) {
			// do nothing
		}
		init();
	}

	private void init() {
		// Centre Date, estimated pomodoros column
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(ID_KEY - 4).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(ID_KEY - 2).setCellRenderer(dtcr);
		// hide ID column
		table.getColumnModel().getColumn(ID_KEY).setMaxWidth(0);
		table.getColumnModel().getColumn(ID_KEY).setMinWidth(0);
		table.getColumnModel().getColumn(ID_KEY).setPreferredWidth(0);
		// Set width of column Unplanned
		table.getColumnModel().getColumn(0).setMaxWidth(30);
		table.getColumnModel().getColumn(0).setMinWidth(30);
		table.getColumnModel().getColumn(0).setPreferredWidth(30);
		// Set width of column Date
		table.getColumnModel().getColumn(1).setMaxWidth(80);
		table.getColumnModel().getColumn(1).setMinWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(80);
		// enable sorting
		if (table.getModel().getRowCount() > 0) {
			table.setAutoCreateRowSorter(true);
		}
		selectActivity();
		setBorder(new TitledBorder(new EtchedBorder(),
				Labels.getString("ActivityListPanel.Activity List") + " ("
						+ ActivityList.getListSize() + ")"));
	}

	public void saveComment(String comment) {
		int row = table.getSelectedRow();
		if (row > -1) {
			Integer id = (Integer) table.getModel().getValueAt(
					table.convertRowIndexToModel(row), ID_KEY);
			Activity selectedActivity = ActivityList.getList().getById(id);
			if (selectedActivity != null) {
				selectedActivity.setNotes(comment);
				selectedActivity.databaseUpdate();
				JFrame window = new JFrame();
				String title = Labels.getString("Common.Add comment");
				String message = Labels.getString("Common.Comment saved");
				JOptionPane.showConfirmDialog(window, message, title,
						JOptionPane.DEFAULT_OPTION);
			}
		}
	}

	private void selectActivity() {
		int index = 0;
		if (!ActivityList.getList().isEmpty()) {
			if (ActivityList.getList().getById(selectedActivityId) == null) { // Activity
																				// deleted
																				// (removed
																				// from
																				// the
																				// list)
				index = selectedRowIndex;
				if (ActivityList.getListSize() < selectedRowIndex + 1) { // Activity
																			// deleted
																			// (end
																			// of
																			// the
																			// list)
					--index;
				}
			} else if (selectedActivityId != 0) {
				Iterator<Activity> iActivity = ActivityList.getList()
						.iterator();
				while (iActivity.hasNext()) {
					if (iActivity.next().getId() == selectedActivityId) {
						break;
					}
					index++;
				}
			}
		}
		if (!ActivityList.getList().isEmpty()) {
			table.setRowSelectionInterval(index, index);
		}
	}
}