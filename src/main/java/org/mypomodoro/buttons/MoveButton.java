package org.mypomodoro.buttons;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.manager.ListPane;
import org.mypomodoro.model.Activity;

/**
 * Move button
 *   
 * @author Phil Karoo
 */
public class MoveButton extends MyButton {

    private static final Dimension BUTTON_SIZE = new Dimension(100, 30);

    public MoveButton(String label, final ListPane from, final ListPane to) {
        super(label);
        setMinimumSize(BUTTON_SIZE);
        setPreferredSize(BUTTON_SIZE);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                move(from, to);
            }
        });
    }

    public static void move(final ListPane from, final ListPane to) {
        Activity selectedActivity = from.getSelectedActivity();
        if (selectedActivity != null) {
            if (selectedActivity.isActivity()) { // Not ToDo
                if (!selectedActivity.isDateToday()) {
                    JFrame window = new JFrame();
                    String title = ControlPanel.labels.getString("ManagerListPanel.Add activity to ToDo List");
                    String message = ControlPanel.labels.getString("ManagerListPanel.The date of this activity is not today. Proceed anyway?");
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        if (to.isMaxNbTotalEstimatedPomReached(selectedActivity)) {
                            title = ControlPanel.labels.getString("ManagerListPanel.Add activity to ToDo List");
                            message = ControlPanel.labels.getString("ManagerListPanel.Max nb of pomodoros per day reached ({0}). Proceed anyway?", org.mypomodoro.gui.ControlPanel.preferences.getMaxNbPomPerDay());
                            reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                            if (reply == JOptionPane.YES_OPTION) {
                                from.removeActivity(selectedActivity);
                                to.addActivity(selectedActivity);
                            }
                        } else {
                            from.removeActivity(selectedActivity);
                            to.addActivity(selectedActivity);
                        }
                    }
                } else if (to.isMaxNbTotalEstimatedPomReached(selectedActivity)) {
                    JFrame window = new JFrame();
                    String title = ControlPanel.labels.getString("ManagerListPanel.Add activity to ToDo List");
                    String message = ControlPanel.labels.getString("ManagerListPanel.Max nb of pomodoros per day reached ({0}). Proceed anyway?", org.mypomodoro.gui.ControlPanel.preferences.getMaxNbPomPerDay());
                    int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        from.removeActivity(selectedActivity);
                        to.addActivity(selectedActivity);
                    }
                } else {
                    from.removeActivity(selectedActivity);
                    to.addActivity(selectedActivity);
                }
            } else { // ToDo
                from.removeActivity(selectedActivity);
                to.addActivity(selectedActivity);
            }
        }
    }
}