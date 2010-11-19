package org.mypomodoro.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class MyPomodoroIconBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ArrayList<MyIcon> myIcons = new ArrayList<MyIcon>();

	private MyIcon highlightedIcon;

	public MyPomodoroIconBar(MyPomodoroView view) {
		myIcons.add(MyIcon.getInstance(view, "Create", "createButton", view
				.getCreatePanel()));
		myIcons.add(MyIcon.getInstance(view, "Activity", "activityButton", view
				.getActivityListPanel()));
		myIcons.add(MyIcon.getInstance(view, "Manager", "managerButton", view
				.getGeneratePanel()));
		myIcons.add(MyIcon.getInstance(view, "Todo", "todoButton", view
				.getTodoListPanel()));
		myIcons.add(MyIcon.getInstance(view, "Report", "reportButton", view
				.getReportListPanel()));

		setBorder(new BevelBorder(BevelBorder.RAISED));
		setPreferredSize(new Dimension(getWidth(), 80));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;
		for (MyIcon i : myIcons) {
			add(i, c);
			c.gridx++;
		}
	}

	public void highlightIcon(MyIcon icon) {
		if (highlightedIcon != null) {
			highlightedIcon.unhighlight();
		}
		icon.highlight();
		highlightedIcon = icon;
	}

	public MyIcon getSelectedIcon() {
		return highlightedIcon;
	}

}