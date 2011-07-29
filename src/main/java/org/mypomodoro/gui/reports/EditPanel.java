package org.mypomodoro.gui.reports;

import java.awt.GridBagConstraints;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.util.Labels;

public class EditPanel extends CreatePanel {

    protected ReportInputForm reportInputFormPanel;

    public EditPanel() {
    }

    @Override
    protected void addInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        reportInputFormPanel = new ReportInputForm();
        add(reportInputFormPanel, gbc);
    }

    @Override
    protected void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        //gbc.fill = GridBagConstraints.NONE;
        add(saveButton, gbc);
    }

    @Override
    protected void addClearButton() {
    }

    @Override
    protected void addValidation() {
    }

    @Override
    protected void validActivityAction(Activity currentReport) {
        if (ReportList.getList().size() > 0) {
            currentReport.databaseUpdate();
            ReportList.getList().update();
            JFrame window = new JFrame();
            String title = Labels.getString("ReportListPanel.Edit report");
            String message = Labels.getString("ReportListPanel.Report updated");
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
    }

    @Override
    public void saveActivity(Activity newReport) {
        // no check for existing reports with same name and date
        if (!newReport.isValid()) {
            invalidActivityAction();
        } else {
            validActivityAction(newReport);
        }
    }

    @Override
    protected void invalidActivityAction() {
        if (ReportList.getList().size() > 0) {
            JFrame window = new JFrame();
            String title = Labels.getString("Common.Error");
            String message = Labels.getString("Common.Title is mandatory");
            JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
        }
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return reportInputFormPanel;
    }

    @Override
    public void fillOutInputForm(Activity report) {
        reportInputFormPanel.setPlaceField(report.getPlace());
        reportInputFormPanel.setAuthorField(report.getAuthor());
        reportInputFormPanel.setNameField(report.getName());
        reportInputFormPanel.setDescriptionField(report.getDescription());
        reportInputFormPanel.setTypeField(report.getType());
        reportInputFormPanel.setEstimatedPomodoros(report.getEstimatedPoms());
        reportInputFormPanel.setActivityId(report.getId());
        reportInputFormPanel.setDate(report.getDate());
    }

    @Override
    public void clearForm() {
        reportInputFormPanel.setNameField("");
        reportInputFormPanel.setDescriptionField("");
        reportInputFormPanel.setTypeField("");
        reportInputFormPanel.setAuthorField("");
        reportInputFormPanel.setPlaceField("");
    }
}