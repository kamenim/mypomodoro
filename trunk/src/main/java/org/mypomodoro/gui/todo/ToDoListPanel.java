package org.mypomodoro.gui.todo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.mypomodoro.Main;
import org.mypomodoro.gui.ActivityInformationListListener;
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
	private final ToDoList toDoList = ToDoList.getList();
	private final ToDoJList toDoJList = new ToDoJList(toDoList);
	private final JLabel pomodoroTimerLabel = new JLabel();
	private final Pomodoro pomodoro = new Pomodoro(pomodoroTimerLabel);

	public Pomodoro getPomodoro() {
		return pomodoro;
	}

	public ToDoListPanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		addTodoList(gbc);
		final InformationPanel informationPanel = new InformationPanel(this);
		toDoJList.addListSelectionListener(new ActivityInformationListListener(
				informationPanel));
        final CommentPanel commentPanel = new CommentPanel(this);
		toDoJList.addListSelectionListener(new ActivityInformationListListener(
				commentPanel));
        final OverestimationPanel overestimationPanel = new OverestimationPanel(this);
		toDoJList.addListSelectionListener(new ActivityInformationListListener(
				overestimationPanel));

		addTimerPanel(gbc);
		addTabPane(gbc, informationPanel, commentPanel, overestimationPanel);
	}

	private void addTabPane(GridBagConstraints gbc,
			InformationPanel informationPanel, CommentPanel commentPanel, OverestimationPanel overestimationPanel) {
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
        gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		add(new TabPane(informationPanel, commentPanel, overestimationPanel, new InterruptPanel(toDoList)), gbc);
	}

	private void addTimerPanel(GridBagConstraints gbc) {
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 0.3;
		gbc.weighty = 0.7;
		add(wrapInBackgroundImage(new TimerPanel(pomodoro, pomodoroTimerLabel),
				new ImageIcon(Main.class
						.getResource("/images/myPomodoroIconNoTime250.png")),
				JLabel.TOP, JLabel.LEADING), gbc);
	}

	private void addTodoList(GridBagConstraints gbc) {
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.7;
		gbc.weighty = 0.7;
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
		//backgroundPanel.setBackground(Color.white);
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

	private void completeTask() {
        Activity currentActivity = toDoList.currentActivity();
        currentActivity.setIsCompleted(true);
        ReportList reportList = ReportList.getList();
        reportList.add(currentActivity);
        toDoList.remove(currentActivity);
        toDoJList.update();
        pomodoro.stop();
	}

    public void completeTaskWithWarning() {        
        if (!pomodoro.inPomodoro()) {
            completeTask();
            JFrame window = new JFrame();
            Activity selectedToDo = (Activity)toDoJList.getSelectedValue();
            String title = "Complete ToDo";
            String message = "ToDo  \"" + selectedToDo.getName() + "\" added to Report List";
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        } else {
          JFrame window = new JFrame();
          String title = "Complete ToDo";
          String message = "Please either stop or finish (recommended) the current pomodoro";
          JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
	}

    public void saveComment(String comment) {
        Activity selectedToDo = (Activity)toDoJList.getSelectedValue();
		if (selectedToDo != null) {
            selectedToDo.setNotes(comment);
            JFrame window = new JFrame();
            String title = "Add comment";
            String message = "Comment saved for ToDo \"" + selectedToDo.getName() + "\"";
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }              
    }

    public void saveOverestimation(int overestimatedPomodoros) {
        Activity selectedToDo = (Activity)toDoJList.getSelectedValue();
		if (selectedToDo != null) {
            selectedToDo.setEstimatedPoms(selectedToDo.getEstimatedPoms() + overestimatedPomodoros);
            JFrame window = new JFrame();
            String title = "Overestimation";
            String message = "Estimated pomodoros for ToDo \"" + selectedToDo.getName() + "\" increased by " + overestimatedPomodoros + " pomodoros";
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
    }
}