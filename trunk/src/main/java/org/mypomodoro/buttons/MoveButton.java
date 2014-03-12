package org.mypomodoro.buttons;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import org.mypomodoro.Main;
import org.mypomodoro.gui.AbstractActivitiesPanel;
import org.mypomodoro.gui.PreferencesPanel;
import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.gui.reports.ReportsPanel;

import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;

/**
 * Move button
 *
 */
public class MoveButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;
    private static final Dimension BUTTON_SIZE = new Dimension(100, 30);

    public MoveButton(String label, final AbstractActivitiesPanel panel) {
        super(label);
        setMinimumSize(BUTTON_SIZE);
        setPreferredSize(BUTTON_SIZE);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                move(panel);
            }
        });
    }

    public void move(final AbstractActivitiesPanel panel) {
        if (panel.getTable().getSelectedRowCount() > 0) {
            if (panel.getTable().getSelectedRowCount() == panel.getTable().getRowCount()
                    && panel instanceof ReportsPanel) { // complete all at once                       
                panel.moveAll();
                panel.refresh();
            } else {
                boolean agreedToMorePomodoros = false;
                int increment = 0;
                int[] rows = panel.getTable().getSelectedRows();
                for (int row : rows) {
                    row = row - increment;
                    Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                    Activity selectedActivity = panel.getActivityById(id);
                    if (panel instanceof ActivitiesPanel) {
                        String activityName = selectedActivity.getName().length() > 25 ? selectedActivity.getName().substring(0, 25) + "..." : selectedActivity.getName();
                        if (!PreferencesPanel.preferences.getAgileMode()) {
                            if (!selectedActivity.isDateToday()) {
                                String title = Labels.getString("ManagerListPanel.Add activity to ToDo List");
                                String message = Labels.getString("ManagerListPanel.The date of activity {0} is not today. Proceed anyway?", activityName);
                                int reply = JOptionPane.showConfirmDialog(Main.gui, message,
                                        title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                if (reply == JOptionPane.NO_OPTION) {
                                    continue; // go to the next one
                                } else if (reply == JOptionPane.CLOSED_OPTION) {
                                    break;
                                }
                            }
                            if (isMaxNbTotalEstimatedPomReached(selectedActivity) && !agreedToMorePomodoros) {
                                String title = Labels.getString("ManagerListPanel.Add activity to ToDo List");
                                String message = Labels.getString(
                                        "ManagerListPanel.Max nb of pomodoros per day reached ({0}). Proceed anyway?",
                                        org.mypomodoro.gui.PreferencesPanel.preferences.getMaxNbPomPerDay());
                                int reply = JOptionPane.showConfirmDialog(Main.gui, message,
                                        title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                                if (reply == JOptionPane.YES_OPTION) {
                                    agreedToMorePomodoros = true;
                                } else {
                                    break; // get out of the loop
                                }
                            }
                        }
                    }
                    panel.move(selectedActivity);
                    // removing a row requires decreasing  the row index number
                    panel.removeRow(row);
                    increment++;
                }
            }
            // Refresh panel border
            panel.setPanelBorder();
        }
    }

    private boolean isMaxNbTotalEstimatedPomReached(Activity activity) {
        int nbTotalEstimatedPom = ToDoList.getList().getNbTotalEstimatedPom() + activity.getEstimatedPoms() + activity.getOverestimatedPoms();
        return nbTotalEstimatedPom > PreferencesPanel.preferences.getMaxNbPomPerDay();
    }
}
