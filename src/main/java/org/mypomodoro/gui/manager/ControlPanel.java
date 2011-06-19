package org.mypomodoro.gui.manager;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.mypomodoro.model.Activity;

public class ControlPanel extends JPanel {
	private static final Dimension BUTTON_SIZE = new Dimension(100, 30);

	public ControlPanel(ListPane activitiesPane, ListPane todoPane) {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		// Adding the add button from activities to todo lists
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;
		add(new MoveButton(">>>", activitiesPane, todoPane), gbc);
        // Adding the remove button from todo to activities list
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 0.0;		
		add(new MoveButton("<<<", todoPane, activitiesPane), gbc);
	}    

	class MoveButton extends JButton {
		public MoveButton(String title, final ListPane from, final ListPane to) {
			super(title);
			setMinimumSize(BUTTON_SIZE);
			setPreferredSize(BUTTON_SIZE);
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Activity activity = from.getSelectedActivity();
					if (activity != null) {
                        if (!to.isMaxNbEstimatedPomReached(activity)) {
                            from.removeActivity(activity);
                            to.addActivity(activity);
                        } else {
                            JFrame window = new JFrame();
                            String message = "Max nb of pomodoros per day (" + org.mypomodoro.gui.ControlPanel.preferences.getMaxNbPomPerDay() + ") reached!";
                            JOptionPane.showMessageDialog(window,message);
                        }
					}
				}
			});
		}

	}
}