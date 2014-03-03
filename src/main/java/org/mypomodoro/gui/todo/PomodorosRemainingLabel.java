package org.mypomodoro.gui.todo;

import javax.swing.JLabel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;
import static org.mypomodoro.util.TimeConverter.getLength;

/**
 * Remaining label
 *
 */
public class PomodorosRemainingLabel {

    static public void showRemainPomodoros(JLabel remainPomodorosLabel) {
        String label = "";
        int remainingPomodoros = 0;
        for (Activity toDo : ToDoList.getList()) {
            int Pomodoros = toDo.getEstimatedPoms() + toDo.getOverestimatedPoms() - toDo.getActualPoms();
            if (Pomodoros > 0) {
                remainingPomodoros += Pomodoros;
            }
        }
        if (ToDoList.getListSize() != 0) {
            label = Labels.getString("ToDoListPanel.{0} pomodoros remaining ({1})",
                    remainingPomodoros, getLength(remainingPomodoros));
        }
        remainPomodorosLabel.setText("  " + label);
    }
}
