package org.mypomodoro.gui.todo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Iterator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mypomodoro.gui.ControlPanel;

import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

public class ToDoJList extends JList {

    private static final long serialVersionUID = 20110814L;
    final private ToDoList toDoList;
    final private Pomodoro pomodoro;
    private static final Dimension PREFERED_SIZE = new Dimension(250, 100);
    private int selectedToDoId = 0;
    private int selectedRowIndex = 0;
    final private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

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

    public void refresh() {
        toDoList.refresh();
    }

    public ToDoList getToDoList() {
        return toDoList;
    }

    final public void init() {
        selectToDo();
        String title = Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List") + " ("
                + ToDoList.getListSize() + ")";
        if (ControlPanel.preferences.getAgileMode()
                && toDoList.getListSize() > 0) {
            title += " - " + Labels.getString("Agile.Common.Story Points") + ": " + toDoList.getStoryPoints();
        }
        setBorder(new TitledBorder(new EtchedBorder(), title));
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

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (isSelected) {
                renderer.setBackground(ColorUtil.BLUE_ROW);
            } else {
                renderer.setBackground(index % 2 == 0 ? Color.white : ColorUtil.YELLOW_ROW); // rows with even/odd number
            }

            Activity toDo = (Activity) value;
            renderer.setText((toDo.isUnplanned() ? "(" + "U" + ") " : "") + toDo.getName() + " (" + toDo.getActualPoms() + "/" + toDo.getEstimatedPoms() + (toDo.getOverestimatedPoms() > 0 ? " + " + toDo.getOverestimatedPoms() : "") + ")");

            Activity currentToDo = pomodoro.getCurrentToDo();
            if (isSelected) {
                renderer.setFont(new Font(renderer.getFont().getName(), Font.BOLD, renderer.getFont().getSize()));
            }
            if (pomodoro.inPomodoro()) {
                if (toDo.getId() == currentToDo.getId()) {
                    renderer.setForeground(ColorUtil.RED);
                } else {
                    if (toDo.isFinished()) {
                        renderer.setForeground(ColorUtil.GREEN);
                    } else {
                        renderer.setForeground(ColorUtil.BLACK);
                    }
                }
            } else {
                if (toDo.isFinished()) {
                    renderer.setForeground(ColorUtil.GREEN);
                } else {
                    renderer.setForeground(ColorUtil.BLACK);
                }
            }
            return renderer;
        }
    }
}
