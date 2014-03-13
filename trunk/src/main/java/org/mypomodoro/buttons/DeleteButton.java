/* 
 * Copyright (C) 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import org.mypomodoro.Main;
import org.mypomodoro.gui.AbstractActivitiesPanel;

import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Delete button
 *
 */
public class DeleteButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;

    public DeleteButton(final String title, final String message, final AbstractActivitiesPanel panel) {
        super(Labels.getString("Common.Delete"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel.getTable().getSelectedRowCount() > 0) {
                    int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (reply == JOptionPane.YES_OPTION) {
                        if (panel.getTable().getSelectedRowCount() == panel.getTable().getRowCount()) { // delete all at once                        
                            panel.deleteAll();
                            panel.refresh();
                        } else {
                            int increment = 0;
                            int[] rows = panel.getTable().getSelectedRows();
                            for (int row : rows) {
                                row = row - increment;
                                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                                Activity selectedActivity = panel.getActivityById(id);
                                panel.delete(selectedActivity);
                                // removing a row requires decreasing  the row index number
                                panel.removeRow(row);
                                increment++;
                            }
                        }
                        // Refresh panel border
                        panel.setPanelBorder();
                    }
                }
            }
        });
    }
}
