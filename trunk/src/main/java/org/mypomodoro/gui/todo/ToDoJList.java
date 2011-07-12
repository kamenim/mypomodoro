package org.mypomodoro.gui.todo;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JList;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mypomodoro.gui.ControlPanel;

import org.mypomodoro.model.ToDoList;

public class ToDoJList extends JList {

    private final ToDoList toDoList;
    private static final Dimension PREFERED_SIZE = new Dimension(250, 100);
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
        int toDoListSize = toDoList.getListSize();
        if (toDoListSize > 0) {
            if (toDoListSize < selectedRowIndex + 1) {
                selectedRowIndex = selectedRowIndex - 1;
            }
            this.setSelectedIndex(selectedRowIndex);
        }
        setBorder(new TitledBorder(new EtchedBorder(), ControlPanel.labels.getString("ToDoListPanel.ToDo List") + " (" + toDoList.getListSize() + ")"));
    }

    public void setSelectedRowIndex() {
        int row = this.getSelectedIndex();
        if (row > -1) {
            selectedRowIndex = row;
        }
    }
}