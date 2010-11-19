package org.mypomodoro.gui.todo;

import javax.swing.JList;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.mypomodoro.model.ToDoList;

public class ToDoJList extends JList {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ToDoList toDoList;
	/**
	 * Width of list cells
	 * 
	 */
	public static final int CELL_WIDTH = 200;

	public ToDoJList(ToDoList toDoList) {
		super(toDoList.toArray());
		this.toDoList = toDoList;
		setFixedCellWidth(CELL_WIDTH);
		setBorder(new TitledBorder(new EtchedBorder(), "ToDo List"));
	}

	public void update() {
		setListData(toDoList.toArray());
	}
}