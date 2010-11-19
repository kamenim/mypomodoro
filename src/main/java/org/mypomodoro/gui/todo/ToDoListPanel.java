package org.mypomodoro.gui.todo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.mypomodoro.Main;
import org.mypomodoro.gui.ActivityInformationListListener;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;

/**
 * Panel that keeps that time, and does all the stuff with the ToDo List. Uses a
 * pomodoro timer and a break timer, each looping into the other without
 * stopping.
 * 
 * @author Brian Wetzel
 */
public class ToDoListPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private final ToDoList toDoList = ToDoList.getList();
	private final ToDoJList toDoJList = new ToDoJList(ToDoList.getList());
	private final JLabel pomodoroTimer = new JLabel();
	private final Pomodoro pomodoro = new Pomodoro(pomodoroTimer);

	public Pomodoro getPomodoro() {
		return pomodoro;
	}

	public ToDoListPanel() throws IOException, FontFormatException {
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		addTodoList(gbc);
		final InformationPanel informationPanel = new InformationPanel(this);
		toDoJList.addListSelectionListener(new ActivityInformationListListener(
				informationPanel));

		addTimerPanel(gbc);
		addTabPane(gbc, informationPanel);
	}

	private void addTabPane(GridBagConstraints gbc,
			InformationPanel informationPanel) {
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.BOTH;
		add(new TabPane(informationPanel, new InterruptPanel(toDoList)), gbc);
	}

	private void addTimerPanel(GridBagConstraints gbc) throws IOException,
			FontFormatException {
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = .3;
		gbc.weighty = .5;
		add(wrapInBackgroundImage(new TimerPanel(pomodoro, pomodoroTimer),
				new ImageIcon(Main.class
						.getResource("/images/myPomodoroIconNoTime250.png")),
				JLabel.TOP, JLabel.LEADING), gbc);
	}

	private void addTodoList(GridBagConstraints gbc) {
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.5;
		gbc.weighty = .5;
		gbc.fill = GridBagConstraints.BOTH;
		add(new JScrollPane(toDoJList), gbc);
	}

	public void refresh() {
		toDoJList.update();
	}

	private static JPanel wrapInBackgroundImage(JComponent component,
			Icon backgroundIcon, int verticalAlignment, int horizontalAlignment) {

		// make the passed in swing component transparent
		component.setOpaque(false);

		// create wrapper JPanel
		JPanel backgroundPanel = new JPanel(new GridBagLayout());
		backgroundPanel.setBackground(Color.white);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		// add the passed in swing component first to ensure that it is in front
		backgroundPanel.add(component, gbc);

		// create a label to paint the background image
		JLabel backgroundImage = new JLabel(backgroundIcon);

		// set minimum and preferred sizes so that the size of the image
		// does not affect the layout size
		backgroundImage.setPreferredSize(new Dimension(250, 250));
		backgroundImage.setMinimumSize(new Dimension(260, 260));

		backgroundImage.setVerticalAlignment(verticalAlignment);
		backgroundImage.setHorizontalAlignment(horizontalAlignment);

		backgroundPanel.add(backgroundImage, gbc);

		return backgroundPanel;
	}

	public void completeTask() {
		Activity currentActivity = toDoList.currentActivity();
		currentActivity.setIsCompleted(true);
		AbstractActivities reportList = ReportList.getList();
		reportList.add(currentActivity);
		toDoList.remove(currentActivity);
		toDoJList.update();
		pomodoro.stop();
	}
}
