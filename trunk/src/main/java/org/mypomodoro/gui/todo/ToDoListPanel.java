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
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.CompoundIcon;

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
    private final JLabel pomodoroTime = new JLabel();
    private final InformationPanel informationPanel = new InformationPanel(this);
    private final JLabel iconLabel = new JLabel("", JLabel.CENTER);
    private final Pomodoro pomodoro = new Pomodoro(pomodoroTime, toDoJList, informationPanel, this);

    public Pomodoro getPomodoro() {
        return pomodoro;
    }

    public ToDoListPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        addTodoList(gbc);
        addTimerPanel(gbc);

        toDoJList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                JList list = (JList) e.getSource();
                Activity selectedToDo = (Activity) list.getSelectedValue();
                if (!pomodoro.inPomodoro()) {
                    pomodoro.setCurrentToDo(selectedToDo);
                }

            }
        });

        toDoJList.addListSelectionListener(new ToDoIconListListener(this, pomodoro));
        addToDoIconPanel(gbc, iconLabel);

        toDoJList.addListSelectionListener(new ActivityInformationListListener(informationPanel));
        final CommentPanel commentPanel = new CommentPanel(this);
        toDoJList.addListSelectionListener(new ActivityInformationListListener(commentPanel));
        final OverestimationPanel overestimationPanel = new OverestimationPanel(this);
        addTabPane(gbc, informationPanel, commentPanel, overestimationPanel);
    }

    private void addTodoList(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 0.7;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(toDoJList), gbc);
    }

    private void addTimerPanel(GridBagConstraints gbc) {
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

    private void addToDoIconPanel(GridBagConstraints gbc, JLabel iconLabel) {
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;
        add(iconLabel, gbc);
    }

    private void addTabPane(GridBagConstraints gbc,
            InformationPanel informationPanel, CommentPanel commentPanel, OverestimationPanel overestimationPanel) {
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(new TabPane(informationPanel, commentPanel, overestimationPanel, new InterruptPanel(toDoList)), gbc);
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
            toDoJList.update();
            pomodoro.stop();
        }
    }

    public void completeTaskWithWarning() {
        if (!pomodoro.inPomodoro()) {
            Activity selectedToDo = (Activity) toDoJList.getSelectedValue();
            if (selectedToDo != null) {
                JFrame window = new JFrame();
                if (selectedToDo.getActualPoms() <= 0) {
                    String title = "Complete ToDo";
                    String message = "ToDo  \"" + selectedToDo.getName() + "\" has no finished (real) pomodoro. Complete anyway?";
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        completeTask();
                        title = "Complete ToDo";
                        message = "ToDo  \"" + selectedToDo.getName() + "\" added to Report List";
                        JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    completeTask();
                    String title = "Complete ToDo";
                    String message = "ToDo  \"" + selectedToDo.getName() + "\" added to Report List";
                    JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
                }
            }
        } else {
            JFrame window = new JFrame();
            String title = "Complete ToDo";
            String message = "Please either stop or finish (recommended) the current pomodoro";
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
    }

    public void saveComment(String comment) {
        Activity selectedToDo = (Activity) toDoJList.getSelectedValue();
        if (selectedToDo != null) {
            selectedToDo.setNotes(comment);
            JFrame window = new JFrame();
            String title = "Add comment";
            String message = "Comment saved for ToDo \"" + selectedToDo.getName() + "\"";
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
    }

    public void saveOverestimation(int overestimatedPomodoros) {
        Activity selectedToDo = (Activity) toDoJList.getSelectedValue();
        if (selectedToDo != null) {
            selectedToDo.setOverestimatedPoms(selectedToDo.getOverestimatedPoms() + overestimatedPomodoros);
            JFrame window = new JFrame();
            String title = "Overestimation";
            String message = "Estimated pomodoros for ToDo \"" + selectedToDo.getName() + "\" increased by " + overestimatedPomodoros + " pomodoros";
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
            informationPanel.showInfo(selectedToDo); // refresh info panel            
            if (pomodoro.getCurrentToDo().equals(selectedToDo)) { // refresh
                setIconLabel();
            }
        }
    }

    public void setIconLabel() {
        Activity selectedToDo = (Activity) toDoJList.getSelectedValue();
        if (selectedToDo != null) {
            //iconLabel.setOpaque(true);
            //iconLabel.setBackground(Color.white);
            iconLabel.setText(selectedToDo.getName());
            int arraySize = selectedToDo.getEstimatedPoms();
            if (selectedToDo.getOverestimatedPoms() > 0) {
                arraySize += selectedToDo.getOverestimatedPoms() + 1;
            }
            Icon[] icons = new Icon[arraySize];
            // Estimated pomodoros
            for (int i = 0; i < selectedToDo.getEstimatedPoms(); i++) {
                if (i < selectedToDo.getActualPoms()) {
                    icons[i] = new ImageIcon(Main.class.getResource("/images/squareCross.png"));
                } else {
                    icons[i] = new ImageIcon(Main.class.getResource("/images/square.png"));
                }
            }
            // Overestimated pomodoros
            if (selectedToDo.getOverestimatedPoms() > 0) {
                // Plus symbol
                icons[selectedToDo.getEstimatedPoms()] = new ImageIcon(Main.class.getResource("/images/plus.png"));
                // Overestimated pomodoros
                for (int i = selectedToDo.getEstimatedPoms() + 1; i < arraySize; i++) {
                    if (i < selectedToDo.getActualPoms() + 1) {
                        icons[i] = new ImageIcon(Main.class.getResource("/images/squareCross.png"));
                    } else {
                        icons[i] = new ImageIcon(Main.class.getResource("/images/square.png"));
                    }
                }
            }
            CompoundIcon icon = new CompoundIcon(2, icons);
            iconLabel.setIcon(icon);
            iconLabel.setVerticalTextPosition(JLabel.CENTER);
            iconLabel.setHorizontalTextPosition(JLabel.LEFT);
        } else {
            iconLabel.setText("");
            iconLabel.setIcon(null);
        }
    }
}