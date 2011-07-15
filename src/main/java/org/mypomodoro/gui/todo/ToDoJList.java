package org.mypomodoro.gui.todo;

import java.awt.Dimension;
import java.awt.Font;
import java.util.Iterator;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.model.Activity;

import org.mypomodoro.model.ToDoList;

public class ToDoJList extends JList {

    private final ToDoList toDoList;
    private static final Dimension PREFERED_SIZE = new Dimension(250, 100);
    private int selectedToDoId = 0;
    private int selectedRowIndex = 0;

    public ToDoJList(ToDoList toDoList) {
        super(toDoList.toArray());
        this.toDoList = toDoList;
        setPreferredSize(PREFERED_SIZE);
        setFont(new Font(this.getFont().getName(), Font.PLAIN, this.getFont().getSize()));
        init();
    }

    public void update() {
        setListData(toDoList.toArray());
        init();
    }

    public void init() {
        selectToDo();
        setBorder(new TitledBorder(new EtchedBorder(), ControlPanel.labels.getString("ToDoListPanel.ToDo List") + " (" + toDoList.getListSize() + ")"));
    }

    public void setSelectedRowIndex(int toDoId) {
        selectedToDoId = toDoId;
        selectedRowIndex = this.getSelectedIndex();
    }

    private void selectToDo() {
        int index = 0;
        if (!toDoList.isEmpty()) {
            if (toDoList.getById(selectedToDoId) == null) { // ToDo completed (removed from the list)
                index = selectedRowIndex;
                if (toDoList.getListSize() < selectedRowIndex + 1) { // ToDo completed (end of the list)
                    --index;
                }
            } else if (selectedToDoId != 0) {
                Iterator<Activity> iToDo = toDoList.iterator();
                while (iToDo.hasNext()) {
                    if (iToDo.next().getId() == selectedToDoId) {
                        break;
                    }
                    index++;
                }
            }
        }
        this.setSelectedIndex(index);
    }
}