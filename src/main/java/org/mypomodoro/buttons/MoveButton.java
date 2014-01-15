package org.mypomodoro.buttons;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;
import javax.swing.JOptionPane;
import org.mypomodoro.Main;

import org.mypomodoro.gui.manager.ListPane;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Move button
 *
 * @author Phil Karoo
 */
public class MoveButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;
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
        List<Activity> selectedActivities = from.getSelectedActivities();
        boolean alreadyAgreed = false;
        for (Activity selectedActivity : selectedActivities) {
            if (selectedActivity != null) {
                if (selectedActivity.isActivity()) { // Not ToDo
                    if (!selectedActivity.isDateToday()) {
                        String title = Labels.getString("ManagerListPanel.Add activity to ToDo List");
                        String message = Labels.getString("ManagerListPanel.The date of activity {0} is not today. Proceed anyway?", selectedActivity.getName());
                        int reply = JOptionPane.showConfirmDialog(Main.gui, message,
                                title, JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.NO_OPTION) {
                            continue; // go to the next one
                        } else if (reply == JOptionPane.CLOSED_OPTION) {
                            break;
                        }
                    }
                    if (to.isMaxNbTotalEstimatedPomReached(selectedActivity) && !alreadyAgreed) {
                        String title = Labels.getString("ManagerListPanel.Add activity to ToDo List");
                        String message = Labels.getString(
                                "ManagerListPanel.Max nb of pomodoros per day reached ({0}). Proceed anyway?",
                                org.mypomodoro.gui.ControlPanel.preferences.getMaxNbPomPerDay());
                        int reply = JOptionPane.showConfirmDialog(Main.gui, message,
                                title, JOptionPane.YES_NO_OPTION);
                        if (reply == JOptionPane.YES_OPTION) {
                            alreadyAgreed = true;
                        } else {
                            break; // get out of the loop
                        }
                    }
                }
                from.removeActivity(selectedActivity);
                to.addActivity(selectedActivity);                
            }
        }
    }
}
