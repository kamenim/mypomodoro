/* 
 * Copyright (C) 2014
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
package org.mypomodoro.gui.reports;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JScrollPane;

import org.mypomodoro.buttons.DeleteButton;
import org.mypomodoro.buttons.MoveButton;
import org.mypomodoro.gui.IActivityInformation;
import org.mypomodoro.gui.activities.ActivityInformationPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays information on the current Report
 *
 */
public class DetailsPanel extends ActivityInformationPanel implements IActivityInformation {

    private final GridBagConstraints gbc = new GridBagConstraints();

    public DetailsPanel(ReportsPanel reportsPanel) {
        setLayout(new GridBagLayout());
        setBorder(null);

        addReopenButton(reportsPanel);
        addInformationArea();
        addDeleteButton(reportsPanel);
    }

    private void addReopenButton(ReportsPanel reportsPanel) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        add(new MoveButton(Labels.getString("ReportListPanel.Reopen"), reportsPanel), gbc);
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

    private void addDeleteButton(ReportsPanel reportsPanel) {
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.1;
        add(new DeleteButton(Labels.getString("ReportListPanel.Delete report"), Labels.getString("ReportListPanel.Are you sure to delete those reports?"), reportsPanel), gbc);
    }

    @Override
    public void selectInfo(Activity activity) {
        super.selectInfo(activity);
        textMap.remove("date_reopened");
        // add additional info
        textMap.put("diffi", "<b>" + Labels.getString("ReportListPanel.Diff I") + ":</b> " + (activity.getActualPoms() - activity.getEstimatedPoms()) + "<br />");
        textMap.put("diffii", "<b>" + Labels.getString("ReportListPanel.Diff II") + ":</b> " + (activity.getOverestimatedPoms() > 0 ? activity.getActualPoms() - activity.getEstimatedPoms() - activity.getOverestimatedPoms() : "") + "<br />");
        textMap.put("internal", "<b>" + Labels.getString("ReportListPanel.Internal Interruptions") + ":</b> " + activity.getNumInternalInterruptions() + "<br />");
        textMap.put("external", "<b>" + Labels.getString("ReportListPanel.External Interruptions") + ":</b> " + activity.getNumInterruptions() + "<br />");
        if (activity.isFinished()) {
            informationArea.setForeground(ColorUtil.GREEN);
        } else {
            informationArea.setForeground(ColorUtil.BLACK);
        }
    }
}
