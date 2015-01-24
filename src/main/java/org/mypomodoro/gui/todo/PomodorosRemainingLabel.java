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

import javax.swing.JLabel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.TimeConverter;

/**
 * Remaining label
 *
 */
public class PomodorosRemainingLabel {

    static public void showRemainPomodoros(JLabel remainPomodorosLabel) {
        String label = "  "; // margin (spaces)
        if (ToDoList.getListSize() > 0) {
            int remainingPomodoros = 0;
            for (Activity toDo : ToDoList.getList()) {
                remainingPomodoros += toDo.getEstimatedPoms() + toDo.getOverestimatedPoms() - toDo.getActualPoms();
            }
            label += Labels.getString("ToDoListPanel.{0} pomodoros remaining ({1})",
                    remainingPomodoros, TimeConverter.getLength(remainingPomodoros));
        }
        remainPomodorosLabel.setText(label);
    }
}
