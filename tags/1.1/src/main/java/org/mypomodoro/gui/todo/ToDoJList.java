package org.mypomodoro.gui.todo;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.mypomodoro.model.ToDoList;

public class ToDoJList extends JList {

    private final ToDoList toDoList;
    private static final Dimension PREFERED_SIZE = new Dimension(250, 100);

    public ToDoJList(ToDoList toDoList) {
        super(toDoList.toArray());
        this.toDoList = toDoList;
        setPreferredSize(PREFERED_SIZE);
        setFont(new Font(this.getFont().getName(), Font.PLAIN, this.getFont().getSize()));
        init();
    }

    public void update() {
        setListData(toDoList.toArray());
        int toDoListSize = toDoList.getListSize();
        if (toDoListSize > 0) {
            this.setSelectedIndex(0);
        }
        init();
    }

    public void init() {
        setBorder(new TitledBorder(new EtchedBorder(), "ToDo List (" + toDoList.getListSize() + ")"));
    }
}