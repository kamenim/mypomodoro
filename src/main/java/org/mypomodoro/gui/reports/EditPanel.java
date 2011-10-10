package org.mypomodoro.gui.reports;

import java.awt.GridBagConstraints;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.util.Labels;

/**
 * GUI for editing an existing report and store to data layer.
 * 
 * @author Phil Karoo
 */
public class EditPanel extends CreatePanel {

    private static final long serialVersionUID = 20110814L;
    protected ReportInputForm reportInputForm;

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
        reportInputForm = new ReportInputForm();
        add(new JScrollPane(reportInputForm), gbc);
    }

    @Override
    protected void addSaveButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        // gbc.fill = GridBagConstraints.NONE;
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
        currentReport.databaseUpdate();
        ReportList.getList().update();
        JFrame window = new JFrame();
        String title = Labels.getString("ReportListPanel.Edit report");
        String message = Labels.getString("ReportListPanel.Report updated");
        JOptionPane.showConfirmDialog(window, message, title,
                JOptionPane.DEFAULT_OPTION);
    }

    @Override
    public void saveActivity(Activity report) {
        if (ReportList.getList().size() > 0) {
            // no check for existing reports with same name and date
            if (!report.isValid()) {
                invalidActivityAction();
            } else {
                validActivityAction(report);
            }
        }
    }

    @Override
    protected void invalidActivityAction() {
        JFrame window = new JFrame();
        String title = Labels.getString("Common.Error");
        String message = Labels.getString("Common.Title is mandatory");
        JOptionPane.showConfirmDialog(window, message, title,
                JOptionPane.DEFAULT_OPTION);
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return reportInputForm;
    }

    @Override
    public void fillOutInputForm(Activity report) {
        reportInputForm.setNameField(report.getName());
        reportInputForm.setDescriptionField(report.getDescription());
        reportInputForm.setTypeField(report.getType());
        reportInputForm.setAuthorField(report.getAuthor());
        reportInputForm.setPlaceField(report.getPlace());
        reportInputForm.setActivityId(report.getId());
    }

    @Override
    public void clearForm() {
        reportInputForm.setNameField("");
        reportInputForm.setDescriptionField("");
        //reportInputForm.setTypeField("");
        reportInputForm.setAuthorField("");
        reportInputForm.setPlaceField("");
    }
}