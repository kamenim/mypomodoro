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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.mypomodoro.gui.burndownchart.CheckPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;

/**
 * Remove button for Chart list
 *
 */
public class RemoveButton extends AbstractPomodoroButton {

    private static final long serialVersionUID = 20110814L;
    private static final Dimension BUTTON_SIZE = new Dimension(100, 30);

    public RemoveButton(String label, final CheckPanel panel) {
        super(label);
        setMinimumSize(BUTTON_SIZE);
        setPreferredSize(BUTTON_SIZE);
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                remove(panel);
            }
        });
    }

    public void remove(final CheckPanel panel) {
        if (panel.getTable().getSelectedRowCount() > 0) {
            int increment = 0;
            int[] rows = panel.getTable().getSelectedRows();
            for (int row : rows) {
                row = row - increment;
                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                Activity selectedActivity = panel.getActivityById(id);
                ChartList.getList().remove(selectedActivity);
                // removing a row requires decreasing the row index number
                panel.removeRow(row);
                increment++;
            }
        }
    }
}
