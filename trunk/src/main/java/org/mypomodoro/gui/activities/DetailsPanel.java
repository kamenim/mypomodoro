/* 
 * Copyright (C) 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.gui.activities;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Date;
import javax.swing.JScrollPane;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.buttons.MoveButton;
import org.mypomodoro.gui.IActivityInformation;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays information on the current Pomodoro
 *
 */
public class DetailsPanel extends ActivityInformationPanel implements IActivityInformation {

    private final GridBagConstraints gbc = new GridBagConstraints();

    public DetailsPanel(ActivitiesPanel activitiesPanel) {
        setLayout(new GridBagLayout());
        setBorder(null);

        addDeleteButton(activitiesPanel);
        addInformationArea();
        addMoveButton(activitiesPanel);
    }

    private void addDeleteButton(ActivitiesPanel activitiesPanel) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        add(new DeleteButton(Labels.getString("Common.Delete activity"), Labels.getString("Common.Are you sure to delete those activities?"), activitiesPanel), gbc);
    }

    private void addInformationArea() {
        // add the information area
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        informationArea.setEditable(false);
        add(new JScrollPane(informationArea), gbc);
    }

    private void addMoveButton(ActivitiesPanel activitiesPanel) {
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        String rightArrow = getFont().canDisplay('\u226b') ? "\u226b" : ">>>";
        MoveButton moveButton = new MoveButton(rightArrow, activitiesPanel);
        moveButton.setFont(getFont().deriveFont(Font.PLAIN, getFont().getSize() + (getFont().canDisplay('\u226b') ? 30 : 6)));
        add(moveButton, gbc);
    }

    @Override
    public void selectInfo(Activity activity) {
        super.selectInfo(activity);
        if (DateUtil.isSameDay(activity.getDateCompleted(), new Date(0))) {
            textMap.remove("date_reopened");
        }
        if (Main.preferences.getAgileMode()) {
            textMap.remove("storypoints");
            textMap.remove("iteration");
        }
        textMap.remove("date_completed");
    }
}
