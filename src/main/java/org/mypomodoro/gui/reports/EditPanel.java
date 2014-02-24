package org.mypomodoro.gui.reports;

import java.awt.GridBagConstraints;

import javax.swing.JOptionPane;

import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.mypomodoro.Main;
import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.util.Labels;

/**
 * GUI for editing an existing report and store to data layer.
 *
 */
public class EditPanel extends CreatePanel {

    private static final long serialVersionUID = 20110814L;
    private ReportInputForm reportInputForm;

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
        reportInputForm.getNameField().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void changedUpdate(DocumentEvent e) {
                enableSaveButton();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (reportInputForm.getNameField().getText().length() == 0) {
                    disableSaveButton();
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                enableSaveButton();
            }
        });
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
        //ReportList.getList().update();
        String title = Labels.getString("ReportListPanel.Edit report");
        String message = Labels.getString("ReportListPanel.Report updated");
        JOptionPane.showConfirmDialog(Main.gui, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
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
    }

    @Override
    public ActivityInputForm getFormPanel() {
        return reportInputForm;
    }

    @Override
    public void fillOutInputForm(Activity report) {
        reportInputForm.setAuthorField(report.getAuthor());
        reportInputForm.setPlaceField(report.getPlace());
        reportInputForm.setDescriptionField(report.getDescription());
        reportInputForm.setActivityId(report.getId());
    }

    /*@Override
     public void clearForm() {
     reportInputForm.setAuthorField("");
     reportInputForm.setPlaceField("");
     reportInputForm.setDescriptionField("");
     }*/
}
