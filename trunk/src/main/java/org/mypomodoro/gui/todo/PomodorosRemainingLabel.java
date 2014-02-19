package org.mypomodoro.gui.todo;

import java.util.Iterator;
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

    static public void showRemainPomodoros(JLabel remainPomodorosLabel, ToDoList toDoList) {
        String label = "";
        int remainingPomodoros = 0;
        //int completedPomodoros = 0;
        Iterator<Activity> iToDo = toDoList.iterator();
        while (iToDo.hasNext()) {
            Activity toDo = iToDo.next();
            int Pomodoros = toDo.getEstimatedPoms() + toDo.getOverestimatedPoms() - toDo.getActualPoms();
            if (Pomodoros > 0) {
                remainingPomodoros += Pomodoros;
            }/* else {
             completedPomodoros++;
             }*/

        }
        if (!toDoList.isEmpty()) {
            label = Labels.getString("ToDoListPanel.{0} pomodoros remaining ({1})",
                    remainingPomodoros, getLength(remainingPomodoros));
        }
        remainPomodorosLabel.setText("  " + label);
    }
}
