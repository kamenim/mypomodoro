package org.mypomodoro.gui.manager;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.ActivityInformationListListener;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;

/**
 * This class just abstracts a JPanel for jlist and a information panel for the
 * items in the list.
 * 
 * @author nikolavp
 * 
 */
public class ListPane extends JPanel implements ActivityInformation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final GridBagConstraints c = new GridBagConstraints();
	private final JList internalActivitiesList;
	private final JTextArea informationArea;
	private final AbstractActivities list;
	/**
	 * width of list cells
	 */
	public static final int CELL_WIDTH = 200;
	private static final Dimension PREFERED_SIZE = new Dimension(200, 100);
	public ListPane(AbstractActivities list, String title) {
		this.list = list;
		setLayout(new GridBagLayout());
		internalActivitiesList = new JList();
		setPreferredSize(PREFERED_SIZE);
		
		this.informationArea = new JTextArea();
		addActivitiesList(title);
		addInformationArea();

	}

	private void addInformationArea() {
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		informationArea.setEditable(false);
		add(new JScrollPane(informationArea), c);
	}

	private void addActivitiesList(String title) {
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 0.8;
		c.fill = GridBagConstraints.BOTH;
		internalActivitiesList.setFixedCellWidth(CELL_WIDTH);
		internalActivitiesList.setBorder(new TitledBorder(new EtchedBorder(),
				title));
		update();
		add(new JScrollPane(internalActivitiesList), c);
		internalActivitiesList
				.addListSelectionListener(new ActivityInformationListListener(
						this));
	}

	public void showInfo(Activity аctivity) {
		String text = "Name: " + аctivity.getName() + "\nDescription:"
				+ аctivity.getDescription() + "\nEstimated Pomodoros: "
				+ аctivity.getEstimatedPoms();
		informationArea.setText(text);
	}

	public void clearInfo() {
		informationArea.setText("");
	}

	public void removeActivity(Activity activity) {
		list.remove(activity);
		update();
	}

	public Activity getSelectedActivity() {
		return (Activity) internalActivitiesList.getSelectedValue();
	}

	public void update() {
		internalActivitiesList.setListData(list.toArray());
	}

	public void addActivity(Activity activity) {
		list.add(activity);
		System.out.println(activity.getPriority());
		update();
	}

	public void addListMouseListener(MouseListener listener) {
		internalActivitiesList.addMouseListener(listener);
	}
}