package org.mypomodoro.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.util.Labels;

public class MyPomodoroIconBar extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private final ArrayList<MyIcon> myIcons = new ArrayList<MyIcon>();
    private MyIcon highlightedIcon;

    public MyPomodoroIconBar(MyPomodoroView view) {
        myIcons.add(MyIcon.getInstance(view,
                Labels.getString("IconBar.Create"), "createButton",
                view.getCreatePanel()));
        myIcons.add(MyIcon.getInstance(view,
                Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "IconBar.Activity"), "activityButton",
                view.getActivityListPanel()));
        myIcons.add(MyIcon.getInstance(view,
                Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "IconBar.ToDo"),
                "todoButton", view.getToDoPanel()));
        myIcons.add(MyIcon.getInstance(view,
                Labels.getString((ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "IconBar.Report"), "reportButton",
                view.getReportListPanel()));
        if (ControlPanel.preferences.getAgileMode()) {
            myIcons.add(MyIcon.getInstance(view,
                    Labels.getString("IconBar.Burndown Chart"), "burndownButton",
                    view.getBurndownPanel()));
        }

        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
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

    public void unHighlightIcon(MyIcon icon) {
        if (highlightedIcon != null) {
            highlightedIcon.unhighlight();
        }
    }

    public MyIcon getSelectedIcon() {
        return highlightedIcon;
    }

    public MyIcon getIcon(int i) {
        return myIcons.get(i);
    }
}
