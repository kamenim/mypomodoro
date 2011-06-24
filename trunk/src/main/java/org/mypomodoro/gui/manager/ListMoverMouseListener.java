package org.mypomodoro.gui.manager;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.mypomodoro.model.Activity;

public class ListMoverMouseListener extends MouseAdapter {
	private final ListPane from;
	private final ListPane to;
	public ListMoverMouseListener(ListPane from, ListPane to) {
		this.from = from;
		this.to = to;
	}
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() >= 2) {
			Activity selectedActivity = from.getSelectedActivity();
			if (selectedActivity != null) {
                if (selectedActivity.isActivity()) {
                    if (to.isMaxNbTotalEstimatedPomReached(selectedActivity)) {
                        JFrame window = new JFrame();
                        String message = "Max nb of pomodoros per day (" + org.mypomodoro.gui.ControlPanel.preferences.getMaxNbPomPerDay() + ") reached!";
                        JOptionPane.showMessageDialog(window,message);
                    } else if (!selectedActivity.isDateToday()) {
                        JFrame window = new JFrame();
                        String title = "Add activity to ToDo list";
                        String message = "The date of activity \"" + selectedActivity.getName() + "\" is not today. Proceed anyway?";
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
}