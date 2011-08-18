package org.mypomodoro.gui.todo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;

public class ToDoJList extends JList {

    private static final long serialVersionUID = 20110814L;
    private final ToDoList toDoList;
    private Pomodoro pomodoro;
    private static final Dimension PREFERED_SIZE = new Dimension(250, 100);
    private int selectedToDoId = 0;
    private int selectedRowIndex = 0;

    public ToDoJList(ToDoList toDoList, Pomodoro pomodoro) {
        super(toDoList.toArray());
        this.toDoList = toDoList;
        this.pomodoro = pomodoro;
        setCellRenderer(new cellRenderer());
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
        setBorder(new TitledBorder(new EtchedBorder(),
                Labels.getString("ToDoListPanel.ToDo List") + " ("
                + ToDoList.getListSize() + ")"));
    }

    public void setSelectedRowIndex(int toDoId) {
        selectedToDoId = toDoId;
        selectedRowIndex = this.getSelectedIndex();
    }

    private void selectToDo() {
        int index = 0;
        if (!toDoList.isEmpty()) {
            // ToDo completed (removed from the list)
            if (toDoList.getById(selectedToDoId) == null) {
                index = selectedRowIndex;
                // ToDo completed (end of the list)
                if (ToDoList.getListSize() < selectedRowIndex + 1) {
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

    private class cellRenderer implements ListCellRenderer {

        private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            Activity toDo = (Activity) value;
            renderer.setText(toDo.getName());

            Activity currentToDo = pomodoro.getCurrentToDo();
            if (pomodoro.inPomodoro()) {
                if (toDo.getId() == currentToDo.getId()) {
                    renderer.setForeground(Color.RED);
                    renderer.setFont(new Font(renderer.getFont().getName(), Font.BOLD, renderer.getFont().getSize()));
                } else {
                    renderer.setForeground(Color.BLACK);
                }
            } else {
                if (currentToDo != null && toDo.getId() == currentToDo.getId()) {
                    renderer.setFont(new Font(renderer.getFont().getName(), Font.BOLD, renderer.getFont().getSize()));
                }
                renderer.setForeground(Color.BLACK);
            }

            return renderer;
        }
    }
}