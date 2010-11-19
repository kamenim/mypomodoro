package org.mypomodoro.gui.todo;

import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;

public class InterruptPanel extends CreatePanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ToDoList todoList;

	public InterruptPanel(ToDoList todoList) {
		this.todoList = todoList;
	}

	@Override
	protected void validActivityAction(Activity newActivity) {
		todoList.currentActivity().incrementInter();
		newActivity.setIsUnplanned(true);
		super.validActivityAction(newActivity);
	}
}