package org.mypomodoro.gui.todo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mypomodoro.Main;
import org.mypomodoro.buttons.MuteButton;
import org.mypomodoro.gui.ActivityInformationListListener;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;

/**
 * Panel that keeps that time, and does all the stuff with the ToDo List. Uses a
 * pomodoro timer and a break timer, each looping into the other without
 * stopping.
 * 
 * @author Brian Wetzel
 * @author Phil Karoo
 */
public class ToDoListPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private final ToDoList toDoList = ToDoList.getList();
    private final JLabel pomodoroTime = new JLabel();
    private final InformationPanel informationPanel = new InformationPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final OverestimationPanel overestimationPanel = new OverestimationPanel(
            this);
    private final UnplannedPanel unplannedPanel = new UnplannedPanel(this);
    private final JLabel iconLabel = new JLabel("", JLabel.CENTER);
    private final Pomodoro pomodoro = new Pomodoro(this);
    private final ToDoJList toDoJList = new ToDoJList(toDoList, pomodoro);
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JLabel pomodorosRemainingLabel = new JLabel("", JLabel.LEFT);

    public ToDoListPanel() {
        setLayout(new GridBagLayout());

        toDoJList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                JList list = (JList) e.getSource();
                Activity selectedToDo = (Activity) list.getSelectedValue();
                if (selectedToDo != null) {
                    if (!pomodoro.inPomodoro()) {
                        pomodoro.setCurrentToDo(selectedToDo);
                    }
                    toDoJList.setSelectedRowIndex(selectedToDo.getId());
                } else if (toDoList.isEmpty()) { // empty list
                    refreshIconLabels();
                    unplannedPanel.clearForm();
                    if (pomodoro.inPomodoro()) { // completed or moved to
                        // Activity List
                        pomodoro.stop();
                        pomodoro.getTimerPanel().setStart();
                    }
                    pomodoro.setCurrentToDo(null);
                    // refresh remaining Pomodoros label
                    PomodorosRemainingLabel.showRemainPomodoros(
                            pomodorosRemainingLabel, toDoList);
                }
            }
        });

        toDoJList.addListSelectionListener(new ToDoIconListListener(this));
        toDoJList.addListSelectionListener(new ActivityInformationListListener(
                informationPanel));
        toDoJList.addListSelectionListener(new ActivityInformationListListener(
                commentPanel));
        toDoJList.addListSelectionListener(new ActivityInformationListListener(
                overestimationPanel));
        toDoJList.addListSelectionListener(new ActivityInformationListListener(
                unplannedPanel));

        addTodoList();
        addTimerPanel();
        addRemainingPomodoroPanel();
        addToDoIconPanel();
        addTabPane();
    }

    private void addTodoList() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 0.7;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(toDoJList), gbc);
    }

    private void addTimerPanel() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 0.6;
        gbc.gridheight = 1;
        TimerPanel timerPanel = new TimerPanel(pomodoro, pomodoroTime, toDoJList);
        add(wrapInBackgroundImage(
                timerPanel,
                new MuteButton(pomodoro),
                new ImageIcon(Main.class.getResource("/images/myPomodoroIconNoTime250.png")),
                JLabel.TOP, JLabel.LEADING), gbc);
        pomodoro.setTimerPanel(timerPanel);
    }

    private void addRemainingPomodoroPanel() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;
        add(pomodorosRemainingLabel, gbc);

        PomodorosRemainingLabel.showRemainPomodoros(pomodorosRemainingLabel,
                toDoList);
    }

    private void addToDoIconPanel() {
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;
        add(iconLabel, gbc);
    }

    private void addTabPane() {
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new TabPane(this, unplannedPanel), gbc);
    }

    public void refresh() {
        toDoJList.update();
        // refresh icon labels
        refreshIconLabels();
        // refresh remaining Pomodoros label
        PomodorosRemainingLabel.showRemainPomodoros(pomodorosRemainingLabel,
                toDoList);
    }

    private JPanel wrapInBackgroundImage(TimerPanel timerPanel,
            MuteButton muteButton, Icon backgroundIcon, int verticalAlignment,
            int horizontalAlignment) {

        // make the passed in swing component transparent
        timerPanel.setOpaque(false);

        // create wrapper JPanel
        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        if (ControlPanel.preferences.getTicking()
                || ControlPanel.preferences.getRinging()) {
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.EAST;
            muteButton.setOpaque(true);
            muteButton.setBorder(new LineBorder(Color.BLACK, 2));
            backgroundPanel.add(muteButton, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            // add the passed in swing component first to ensure that it is in
            // front
            backgroundPanel.add(timerPanel, gbc);
        } else {
            gbc.gridx = 0;
            gbc.gridy = 0;
            // add the passed in swing component first to ensure that it is in
            // front
            backgroundPanel.add(timerPanel, gbc);
        }

        // create a label to paint the background image
        JLabel backgroundImage = new JLabel(backgroundIcon);

        // set minimum and preferred sizes so that the size of the image
        // does not affect the layout size
        backgroundImage.setPreferredSize(new Dimension(250, 240));
        backgroundImage.setMinimumSize(new Dimension(260, 250));

        backgroundImage.setVerticalAlignment(verticalAlignment);
        backgroundImage.setHorizontalAlignment(horizontalAlignment);

        backgroundPanel.add(backgroundImage, gbc);

        return backgroundPanel;
    }

    private void completeTask(Activity selectedToDo) {
        selectedToDo.setIsCompleted(true);
        selectedToDo.setDate(new Date());
        selectedToDo.databaseUpdate();
        ReportList reportList = ReportList.getList();
        reportList.add(selectedToDo);
        toDoList.remove(selectedToDo);
        // break time : timer must be stopped if list is empty
        if (toDoList.isEmpty()) {
            pomodoro.stop();
            pomodoro.getTimerPanel().setStart();
        }
    }

    public void completeTaskWithWarning() {
        Activity selectedToDo = (Activity) toDoJList.getSelectedValue();
        if (!pomodoro.inPomodoro()
                || !pomodoro.getCurrentToDo().equals(selectedToDo)) {
            if (selectedToDo != null) {
                JFrame window = new JFrame();
                if (selectedToDo.getActualPoms() <= 0) {
                    String title = Labels.getString("ToDoListPanel.Complete ToDo");
                    String message = Labels.getString("ToDoListPanel.This ToDo has no finished pomodoro (real). Complete anyway?");
                    int reply = JOptionPane.showConfirmDialog(window, message,
                            title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        completeTask(selectedToDo);
                        refresh();
                        title = Labels.getString("ToDoListPanel.Complete ToDo");
                        message = Labels.getString("ToDoListPanel.ToDo moved to Report List");
                        JOptionPane.showConfirmDialog(window, message, title,
                                JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    String title = Labels.getString("ToDoListPanel.Complete ToDo");
                    String message = Labels.getString("ToDoListPanel.Are you sure to complete this ToDo?");
                    int reply = JOptionPane.showConfirmDialog(window, message,
                            title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        completeTask(selectedToDo);
                        refresh();
                        title = Labels.getString("ToDoListPanel.Complete ToDo");
                        message = Labels.getString("ToDoListPanel.ToDo moved to Report List");
                        JOptionPane.showConfirmDialog(window, message, title,
                                JOptionPane.DEFAULT_OPTION);
                    }
                }
            }
        } else {
            JFrame window = new JFrame();
            String title = Labels.getString("ToDoListPanel.Complete ToDo");
            String message = Labels.getString("ToDoListPanel.Please either stop or finish the current pomodoro (recommended)");
            JOptionPane.showConfirmDialog(window, message, title,
                    JOptionPane.DEFAULT_OPTION);
        }
    }

    private void completeAllTasks() {
        toDoList.completeAll();
        // break time : timer must be stopped (the list is empty)
        pomodoro.stop();
        pomodoro.getTimerPanel().setStart();
    }

    public void completeAllTasksWithWarning() {
        if (!pomodoro.inPomodoro()) {
            JFrame window = new JFrame();
            String title = Labels.getString("ToDoListPanel.Complete ALL ToDo");
            String message = Labels.getString("ToDoListPanel.Are you sure to complete ALL ToDo?");
            int reply = JOptionPane.showConfirmDialog(window, message, title,
                    JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                completeAllTasks();
                refresh();
                // refresh remaining Pomodoros label
                PomodorosRemainingLabel.showRemainPomodoros(
                        pomodorosRemainingLabel, toDoList);
            }
        } else {
            JFrame window = new JFrame();
            String title = Labels.getString("ToDoListPanel.Complete ToDo");
            String message = Labels.getString("ToDoListPanel.Please either stop or finish the current pomodoro (recommended)");
            JOptionPane.showConfirmDialog(window, message, title,
                    JOptionPane.DEFAULT_OPTION);
        }
    }

    public void saveComment(String comment) {
        Activity selectedToDo = (Activity) toDoJList.getSelectedValue();
        if (selectedToDo != null) {
            selectedToDo.setNotes(comment);
            selectedToDo.databaseUpdate();
            JFrame window = new JFrame();
            String title = Labels.getString("Common.Add comment");
            String message = Labels.getString("Common.Comment saved");
            JOptionPane.showConfirmDialog(window, message, title,
                    JOptionPane.DEFAULT_OPTION);
        }
    }

    public ToDoJList getToDoJList() {
        return toDoJList;
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }

    public JLabel getPomodorosRemainingLabel() {
        return pomodorosRemainingLabel;
    }

    public Pomodoro getPomodoro() {
        return pomodoro;
    }

    public InformationPanel getInformationPanel() {
        return informationPanel;
    }

    public CommentPanel getCommentPanel() {
        return commentPanel;
    }

    public OverestimationPanel getOverestimationPanel() {
        return overestimationPanel;
    }

    public UnplannedPanel getUnplannedPanel() {
        return unplannedPanel;
    }

    public ToDoList getToDoList() {
        return toDoList;
    }

    public JLabel getPomodoroTime() {
        return pomodoroTime;
    }

    public void refreshIconLabels() {
        Activity selectedToDo = (Activity) toDoJList.getSelectedValue();
        if (selectedToDo != null) {
            Activity currentToDo = pomodoro.getCurrentToDo();
            if (pomodoro.inPomodoro()) {
                ToDoIconLabel.showIconLabel(iconLabel, currentToDo, Color.RED);
                ToDoIconLabel.showIconLabel(unplannedPanel.getIconLabel(), currentToDo, Color.RED);
                if (selectedToDo.getId() != currentToDo.getId()) {
                    ToDoIconLabel.showIconLabel(informationPanel.getIconLabel(), selectedToDo);
                    ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), selectedToDo);
                    ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), selectedToDo);
                } else {
                    ToDoIconLabel.showIconLabel(informationPanel.getIconLabel(), currentToDo, Color.RED);
                    ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), currentToDo, Color.RED);
                    ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), currentToDo, Color.RED);
                }
            } else {
                if (currentToDo != null && selectedToDo.getId() != currentToDo.getId()) {
                    ToDoIconLabel.showIconLabel(iconLabel, currentToDo);
                    ToDoIconLabel.showIconLabel(unplannedPanel.getIconLabel(), currentToDo);
                } else {
                    ToDoIconLabel.showIconLabel(iconLabel, selectedToDo);
                    ToDoIconLabel.showIconLabel(unplannedPanel.getIconLabel(), selectedToDo);
                }
                ToDoIconLabel.showIconLabel(informationPanel.getIconLabel(), selectedToDo);
                ToDoIconLabel.showIconLabel(commentPanel.getIconLabel(), selectedToDo);
                ToDoIconLabel.showIconLabel(overestimationPanel.getIconLabel(), selectedToDo);
            }
        } else { // empty list
            ToDoIconLabel.clearIconLabel(iconLabel);
            ToDoIconLabel.clearIconLabel(unplannedPanel.getIconLabel());
            ToDoIconLabel.clearIconLabel(informationPanel.getIconLabel());
            ToDoIconLabel.clearIconLabel(commentPanel.getIconLabel());
            ToDoIconLabel.clearIconLabel(overestimationPanel.getIconLabel());
        }
    }
}