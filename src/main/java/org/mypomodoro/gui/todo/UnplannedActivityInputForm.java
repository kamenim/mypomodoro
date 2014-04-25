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
package org.mypomodoro.gui.todo;

import javax.swing.JComboBox;
import org.mypomodoro.gui.activities.AbstractComboBoxRenderer;

import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.Labels;

public class UnplannedActivityInputForm extends ActivityInputForm {

    protected JComboBox interruptions = new JComboBox();
    protected final String internal = Labels.getString("ToDoListPanel.Internal");
    protected final String external = Labels.getString("ToDoListPanel.External");

    public UnplannedActivityInputForm() {
        super(1);
        addInterruptions();
    }

    protected final void addInterruptions() {
        // Internal and External
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.5;
        add(new FormLabel(Labels.getString("ToDoListPanel.Interruption") + ": "), c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.5;
        String items[] = new String[3];
        items[0] = " ";
        items[1] = internal;
        items[2] = external;
        interruptions = new JComboBox(items);
        interruptions.setRenderer(new AbstractComboBoxRenderer());
        add(interruptions, c);
    }

    public boolean isSelectedInternalInterruption() {
        return ((String) interruptions.getSelectedItem()).equals(internal);
    }

    public boolean isSelectedExternalInterruption() {
        return ((String) interruptions.getSelectedItem()).equals(external);
    }

    public void setInterruption(int index) {
        interruptions.setSelectedIndex(index);
    }
}
