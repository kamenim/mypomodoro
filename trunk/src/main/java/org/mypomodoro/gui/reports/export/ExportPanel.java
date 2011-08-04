package org.mypomodoro.gui.reports.export;

import au.com.bytecode.opencsv.CSVWriter;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.buttons.MyButton;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel to export reports
 * 
 * @author Phil Karoo
 */
public class ExportPanel extends JPanel implements ActivityInformation {

    protected final ExportInputForm exportInputForm = new ExportInputForm();
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final AbstractActivities activities;

    public ExportPanel(AbstractActivities activities) {
        this.activities = activities;

        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addExportInputForm();
        addExportButton();
    }

    private void addExportButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        //gbc.fill = GridBagConstraints.NONE;
        JButton exportButton = new MyButton(Labels.getString("ReportListPanel.Export"));
        exportButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (exportInputForm.getFileName().length() == 0) {
                    exportInputForm.initFileName();
                }
                if (exportInputForm.getEditableSeparatorText().length() == 0) { // editable field
                    exportInputForm.initSeparatorComboBox();
                }
                export();
            }
        });
        add(exportButton, gbc);
    }

    private void addExportInputForm() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(exportInputForm, gbc);
    }

    public void export() {
        if (activities.size() > 0) {
            String fileName = exportInputForm.getFileName() + "." + exportInputForm.getFileFormat();
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(fileName), exportInputForm.getSeparator());                
                Iterator<Activity> act = activities.iterator();
                String[] entries = new String[]{};
                // Header
                if (exportInputForm.isHeaderSelected()) {
                
                    entries = new String[] {"U",Labels.getString("Common.Date"),Labels.getString("ReportListPanel.Time"),
                                                    Labels.getString("Common.Title"),Labels.getString("ReportListPanel.Estimated"),Labels.getString("ReportListPanel.Overestimated"),
                                                    Labels.getString("ReportListPanel.Real"),Labels.getString("ReportListPanel.Diff I"),Labels.getString("ReportListPanel.Diff II"),
                                                    Labels.getString("ToDoListPanel.Internal"),Labels.getString("ToDoListPanel.External"),
                                                    Labels.getString("Common.Type"),Labels.getString("Common.Author"),Labels.getString("Common.Place"),
                                                    Labels.getString("Common.Description"),Labels.getString("Common.Comment")};
                    writer.writeNext(entries);
                }
                // Data
                while (act.hasNext()) {
                    entries = act.next().toArray(exportInputForm.getDatePattern());
                    writer.writeNext(entries);
                }
                writer.close();
                JFrame window = new JFrame();
                String title = Labels.getString("ReportListPanel.Export reports");
                String message = Labels.getString("ReportListPanel.Reports exported to file {0}",fileName);
                JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
            }
            catch (IOException ex) {
                JFrame window = new JFrame();
                String title = Labels.getString("Common.Error");
                String message = Labels.getString("ReportListPanel.Export failed");
                JOptionPane.showConfirmDialog(window, message, title, JOptionPane.DEFAULT_OPTION);
            }
        }
    }

    @Override
    public void showInfo(Activity activity) {
    }

    @Override
    public void clearInfo() {
    }
}