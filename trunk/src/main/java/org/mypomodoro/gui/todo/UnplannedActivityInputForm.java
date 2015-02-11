/* 
 * Copyright (C) 
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
package org.mypomodoro.gui.todo;

import javax.swing.JComboBox;
import org.mypomodoro.Main;
import org.mypomodoro.gui.activities.AbstractComboBoxRenderer;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

public class UnplannedActivityInputForm extends ActivityInputForm {

    protected JComboBox interruptionsComboBox;
    protected final String unplanned = Labels.getString("ToDoListPanel.Unplanned task");
    protected final String internal = Labels.getString("ToDoListPanel.Internal interruption");
    protected final String external = Labels.getString("ToDoListPanel.External interruption");

    public UnplannedActivityInputForm() {
        super(1);
        addInterruptions();
    }

    protected final void addInterruptions() {
        // Internal and External
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.5;
        add(new FormLabel(""), c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.5;
        interruptionsComboBox = new JComboBox();
        interruptionsComboBox.addItem(unplanned);
        interruptionsComboBox.setRenderer(new AbstractComboBoxRenderer());        
        // Setting the background color is required here for the Cross Platform Look And Feel (see Main)
        interruptionsComboBox.setBackground(ColorUtil.WHITE);
        add(interruptionsComboBox, c);
    }

    public boolean isSelectedInternalInterruption() {
        return ((String) interruptionsComboBox.getSelectedItem()).equals(internal);
    }

    public boolean isSelectedExternalInterruption() {
        return ((String) interruptionsComboBox.getSelectedItem()).equals(external);
    }

    public void setInterruption(int index) {
        interruptionsComboBox.setSelectedIndex(index);
    }

    public void refreshInterruptionComboBox(boolean inPomodoro) {
        interruptionsComboBox.removeAllItems();
        if (Main.toDoPanel.getPomodoro().inPomodoro()) {
            interruptionsComboBox.addItem(unplanned);
            interruptionsComboBox.addItem(internal);
            interruptionsComboBox.addItem(external);
            interruptionsComboBox.setSelectedItem(internal);
        } else {
            interruptionsComboBox.addItem(unplanned);
        }
        interruptionsComboBox.repaint();
    }
}
