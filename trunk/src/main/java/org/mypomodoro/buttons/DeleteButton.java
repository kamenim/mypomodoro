package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTable;

import org.mypomodoro.gui.activities.ActivitiesPanel;
import org.mypomodoro.model.ActivityList;

public class DeleteButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeleteButton(final JTable table) {
		super("Delete");
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectedRow();
				// make sure this is pointing to the id value of the table.
				int id = (Integer) table.getModel().getValueAt(index,
						ActivitiesPanel.ID_KEY);
				ActivityList.getList().removeById(id);
			}

		});
	}
}