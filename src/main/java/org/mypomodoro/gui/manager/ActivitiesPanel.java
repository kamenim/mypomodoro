package org.mypomodoro.gui.manager;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

/**
 * Activities Panel
 *
 */
public class ActivitiesPanel extends ListPanel {

    protected static final Dimension COMBO_BOX_DIMENSION = new Dimension(60, 20);

    public ActivitiesPanel(AbstractActivities list) {
        super(list);
    }

    @Override
    public void setPanelBorder() {
        String titleActivitiesList = Labels.getString((org.mypomodoro.gui.ControlPanel.preferences.getAgileMode() ? "Agile." : "") + "ActivityListPanel.Activity List")
                + " (" + ActivityList.getListSize() + ")";
        if (org.mypomodoro.gui.ControlPanel.preferences.getAgileMode()
                && ActivityList.getListSize() > 0) {
            titleActivitiesList += " - " + Labels.getString("Agile.Common.Story Points") + ": " + ActivityList.getList().getStoryPoints();
        }
        setBorder(new TitledBorder(new EtchedBorder(), titleActivitiesList));
    }

    @Override
    protected void addActivitiesList() {
        super.addActivitiesList();
        if (org.mypomodoro.gui.ControlPanel.preferences.getAgileMode()) {
            addIterationFilterPanel(); // panel to filter task by iteration            
        }
    }

    protected void addIterationFilterPanel() {
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        //c.weighty = 0.1;
        JPanel iterationPanel = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        //IterationComboBox iterationComboBox = new IterationComboBox();
        JComboBox iterationComboBox = new JComboBox() {
            //
        };
        for (int i = 0; i <= 101; i++) {
            iterationComboBox.addItem(i); //starting at iteration 0 (not -1)
        }
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.5;
        iterationPanel.add(new FormLabel(Labels.getString("Agile.Common.Iteration") + ": "), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weighty = 0.5;
        iterationComboBox.setBackground(ColorUtil.WHITE);
        iterationComboBox.setMinimumSize(COMBO_BOX_DIMENSION);
        iterationComboBox.setPreferredSize(COMBO_BOX_DIMENSION);
        iterationComboBox.setFont(new Font(iterationComboBox.getFont().getName(), Font.PLAIN, iterationComboBox.getFont().getSize()));
        iterationPanel.add(iterationComboBox, gbc);
        add(iterationPanel, c);
    }

    @Override
    protected void addInformationArea() {
        if (org.mypomodoro.gui.ControlPanel.preferences.getAgileMode()) {
            addInformationArea(2);
        } else {
            super.addInformationArea();
        }
    }

    public void update() {
        if (org.mypomodoro.gui.ControlPanel.preferences.getAgileMode()) {
            internalActivitiesList.setListData(ActivityList.getList().getListIteration(0).toArray()); // subset of activities : by default, activities with iteration = 0
        } else {
            super.update();
        }
        init();
    }
}
