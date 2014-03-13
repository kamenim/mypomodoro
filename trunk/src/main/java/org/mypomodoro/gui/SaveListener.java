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
package org.mypomodoro.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;

public class SaveListener implements ActionListener {

    final private CreatePanel panel;

    public SaveListener(CreatePanel panel) {
        this.panel = panel;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Activity newActivity = panel.getFormPanel().getActivityFromFields();
        if (newActivity != null) {
            panel.saveActivity(newActivity);
        }
    }
}
