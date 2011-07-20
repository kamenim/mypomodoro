package org.mypomodoro.gui.todo;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mypomodoro.Main;
import org.mypomodoro.gui.ActivityInformationListListener;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;

/**
 * Panel that keeps that time, and does all the stuff with the ToDo List. Uses a
 * pomodoro timer and a break timer, each looping into the other without
 * stopping.
 * 
 * @author Brian Wetzel 
 * @author Phil Karoo
 */
public class ToDoListPanel extends JPanel {

    private final ToDoList toDoList = ToDoList.getList();
    private final ToDoJList toDoJList = new ToDoJList(toDoList);
    private final JLabel pomodoroTime = new JLabel();
    private final InformationPanel informationPanel = new InformationPanel(this);
    private final CommentPanel commentPanel = new CommentPanel(this);
    private final OverestimationPanel overestimationPanel = new OverestimationPanel(this);
    private final UnplannedPanel unplannedPanel = new UnplannedPanel(this);
    private final JLabel iconLabel = new JLabel("", JLabel.CENTER);
    private final Pomodoro pomodoro = new Pomodoro(this);
    private final GridBagConstraints gbc = new GridBagConstraints();

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
                } else if (toDoList.size() == 0) { // empty list                    
                    ToDoIconLabel.clearIconLabel(iconLabel);
                    unplannedPanel.clearForm();
                    if (pomodoro.inPomodoro()) {
                        pomodoro.stop();
                        pomodoro.getTimerPanel().setStart();
                    }
                }
            }
        });

        toDoJList.addListSelectionListener(new ToDoIconListListener(this));
        toDoJList.addListSelectionListener(new ActivityInformationListListener(informationPanel));
        toDoJList.addListSelectionListener(new ActivityInformationListListener(commentPanel));
        toDoJList.addListSelectionListener(new ActivityInformationListListener(overestimationPanel));
        toDoJList.addListSelectionListener(new ActivityInformationListListener(unplannedPanel));

        addTodoList();
        addTimerPanel();
        addToDoIconPanel();
        addTabPane();
    }

    private void addTodoList() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 0.7;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(toDoJList), gbc);
    }

    private void addTimerPanel() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 0.6;
        gbc.gridheight = 1;
        TimerPanel timerPanel = new TimerPanel(pomodoro, pomodoroTime);
        add(wrapInBackgroundImage(timerPanel,
                new ImageIcon(Main.class.getResource("/images/myPomodoroIconNoTime250.png")),
                JLabel.TOP, JLabel.LEADING), gbc);
        pomodoro.setTimerPanel(timerPanel);
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
        backgroundImage.setPreferredSize(new Dimension(250, 240));
        backgroundImage.setMinimumSize(new Dimension(260, 250));

        backgroundImage.setVerticalAlignment(verticalAlignment);
        backgroundImage.setHorizontalAlignment(horizontalAlignment);

        backgroundPanel.add(backgroundImage, gbc);

        return backgroundPanel;
    }

    private void completeTask() {
        Activity selectedToDo = (Activity) toDoJList.getSelectedValue();
        if (selectedToDo != null) {
            selectedToDo.setIsCompleted(true);
            selectedToDo.setDate(new Date()); // update date
            ReportList reportList = ReportList.getList();
            reportList.add(selectedToDo);
            toDoList.remove(selectedToDo);
            if (pomodoro.inPomodoro() && pomodoro.getCurrentToDo().equals(selectedToDo)) {
                pomodoro.stop();
                pomodoro.getTimerPanel().setStart();
            }
            refresh();
        }
    }

    public void completeTaskWithWarning() {
        Activity selectedToDo = (Activity) toDoJList.getSelectedValue();
        if (!pomodoro.inPomodoro() || !pomodoro.getCurrentToDo().equals(selectedToDo)) {
            if (selectedToDo != null) {
                JFrame window = new JFrame();
                if (selectedToDo.getActualPoms() <= 0) {
                    String title = ControlPanel.labels.getString("ToDoListPanel.Complete ToDo");
                    String message = ControlPanel.labels.getString("ToDoListPanel.This ToDo has no finished pomodoro (real). Complete anyway?");
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        completeTask();
                        title = ControlPanel.labels.getString("ToDoListPanel.Complete ToDo");
                        message = ControlPanel.labels.getString("ToDoListPanel.ToDo moved to Report List");
                        JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    completeTask();
                    String title = ControlPanel.labels.getString("ToDoListPanel.Complete ToDo");
                    String message = ControlPanel.labels.getString("ToDoListPanel.ToDo moved to Report List");
                    JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
                }
            }
        } else {
            JFrame window = new JFrame();
            String title = ControlPanel.labels.getString("ToDoListPanel.Complete ToDo");
            String message = ControlPanel.labels.getString("ToDoListPanel.Please either stop or finish the current pomodoro (recommended)");
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
    }

    public void saveComment(String comment) {
        Activity selectedToDo = (Activity) toDoJList.getSelectedValue();
        if (selectedToDo != null) {
            selectedToDo.setNotes(comment);
            JFrame window = new JFrame();
            String title = ControlPanel.labels.getString("Common.Add comment");
            String message = ControlPanel.labels.getString("Common.Comment saved");
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
    }

    public ToDoJList getToDoJList() {
        return toDoJList;
    }

    public JLabel getIconLabel() {
        return iconLabel;
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
}