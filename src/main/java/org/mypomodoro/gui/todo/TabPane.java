package org.mypomodoro.gui.todo;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

class TabPane extends JTabbedPane {
	public TabPane(InformationPanel informationPanel,
            CommentPanel commentPanel,
            OverestimationPanel overestimationPanel,
			InterruptPanel interruptPanel) {
		add("Details", informationPanel);
        add("Comment", commentPanel);
        add("Overestimation", overestimationPanel);
		add("Unplanned", new JScrollPane(interruptPanel));
	}
}