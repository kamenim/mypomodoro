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
package org.mypomodoro.gui.export;

import au.com.bytecode.opencsv.CSVWriter;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.AbstractButton;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.export.ExportInputForm.activityToArray;
import org.mypomodoro.gui.export.google.GoogleConfigLoader;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel to export reports
 *
 */
public class ExportPanel extends JPanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    protected final ExportInputForm exportInputForm = new ExportInputForm();
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final IListPanel panel;
    private JButton cancelButton;
    private final String[] headerEntries = new String[]{"U",
        Labels.getString(PreferencesPanel.preferences.getAgileMode() ? "Common.Date created" : "Common.Date scheduled"),
        Labels.getString("Common.Date completed"),
        Labels.getString("Common.Title"),
        Labels.getString("Common.Estimated"),
        Labels.getString("Common.Overestimated"),
        Labels.getString("Common.Real"),
        Labels.getString("ReportListPanel.Diff I"),
        Labels.getString("ReportListPanel.Diff II"),
        Labels.getString("ToDoListPanel.Internal"),
        Labels.getString("ToDoListPanel.External"),
        Labels.getString("Common.Type"),
        Labels.getString("Common.Author"),
        Labels.getString("Common.Place"),
        Labels.getString("Common.Description"),
        Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "Common.Comment"),
        Labels.getString("Agile.Common.Story Points"),
        Labels.getString("Agile.Common.Iteration"),
        Labels.getString("Common.Priority")};

    public ExportPanel(IListPanel panel) {
        this.panel = panel;

        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addCancelButton();
        addExportInputForm();
        addExportButton();
    }

    private void addExportButton() {
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        // gbc.fill = GridBagConstraints.NONE;
        JButton exportButton = new AbstractButton(
                Labels.getString("ReportListPanel.Export"));
        exportButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Make sure the file name is set
                if (exportInputForm.getFileName().length() == 0) {
                    exportInputForm.initFileName();
                }
                export();
            }
        });
        add(exportButton, gbc);
    }

    private void addCancelButton() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        cancelButton = new AbstractButton(
                Labels.getString("Common.Cancel"));
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                exportInputForm.showExportForm();
                cancelButton.setVisible(false);
            }
        });
        add(cancelButton, gbc);
        cancelButton.setVisible(false);
    }

    private void addExportInputForm() {
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        exportInputForm.setPreferredSize(null);
        add(new JScrollPane(exportInputForm), gbc);
    }

    private void export() {
        if (panel.getTable().getSelectedRowCount() > 0) {
            ArrayList<Activity> activities = new ArrayList<Activity>();
            int[] rows = panel.getTable().getSelectedRows();
            for (int row : rows) {
                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                Activity selectedActivity = panel.getActivityById(id);
                activities.add(selectedActivity);
            }
            try {
                String fileName = exportInputForm.getFileName() + "."
                        + exportInputForm.getFileExtention();
                Iterator<Activity> act = activities.iterator();
                boolean exportOK = false;
                if (exportInputForm.isFileCSVFormat()) {
                    exportOK = exportCSV(fileName, act);
                } else if (exportInputForm.isFileExcelFormat()) {
                    exportOK = exportExcel(fileName, act);
                } else if (exportInputForm.isFileExcelOpenXMLFormat()) {
                    exportOK = exportExcelx(fileName, act);
                } else if (exportInputForm.isFileGoogleDriveFormat()) {
                    exportOK = exportToGoogleDrive(fileName, act);
                }
                if (exportOK) {
                    String title = Labels.getString("ReportListPanel.Export");
                    String message = Labels.getString(
                            "ReportListPanel.Data exported to file {0}",
                            fileName);
                    if (exportInputForm.isFileGoogleDriveFormat()) {
                        message = Labels.getString(
                                "ReportListPanel.Data exported to Google Drive");
                    }
                    JOptionPane.showConfirmDialog(Main.gui, message, title,
                            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (IOException ex) {
                logger.error("Export failed", ex);
                String title = Labels.getString("Common.Error");
                String message = Labels.getString("ReportListPanel.Export failed");
                JOptionPane.showConfirmDialog(Main.gui, message, title,
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private boolean exportCSV(String fileName, Iterator<Activity> act)
            throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(fileName),
                exportInputForm.getSeparator());
        // Header
        if (exportInputForm.isHeaderSelected()) {
            writer.writeNext(headerEntries);
        }
        // Data
        while (act.hasNext()) {
            String[] entries = activityToArray.toArray(act.next(),
                    exportInputForm.getDatePattern());
            writer.writeNext(entries);
        }
        writer.close();
        return true;
    }

    private boolean exportExcel(String fileName, Iterator<Activity> act)
            throws IOException {
        FileOutputStream fileOut = new FileOutputStream(fileName);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet worksheet = workbook.createSheet();

        int rowNb = 0;
        // Header
        if (exportInputForm.isHeaderSelected()) {
            HSSFRow row = worksheet.createRow(rowNb);
            for (int i = 0; i < headerEntries.length; i++) {
                row.createCell(i).setCellValue(headerEntries[i]);
            }
            rowNb++;
        }
        // Data
        while (act.hasNext()) {
            Object[] entries = activityToArray.toRowArray(act.next());
            HSSFRow row = worksheet.createRow(rowNb);
            for (int i = 0; i < entries.length; i++) {
                HSSFCell cell = row.createCell(i);
                if (entries[i] instanceof Integer) {
                    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    cell.setCellValue((Integer) entries[i]);
                } else if (entries[i] instanceof Float) {
                    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    HSSFCellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.0"));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((Float) entries[i]);
                } else if (entries[i] instanceof Boolean) {
                    cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
                    cell.setCellValue((Boolean) entries[i]);
                } else if (entries[i] instanceof Date) {
                    HSSFCellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(exportInputForm.getDatePattern()));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((Date) entries[i]);
                } else { // text
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue((String) entries[i]);
                }
            }
            rowNb++;
        }
        workbook.write(fileOut);
        fileOut.flush();
        fileOut.close();
        return true;
    }

    private boolean exportExcelx(String fileName, Iterator<Activity> act)
            throws IOException {
        FileOutputStream fileOut = new FileOutputStream(fileName);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet();

        int rowNb = 0;
        // Header
        if (exportInputForm.isHeaderSelected()) {
            XSSFRow row = worksheet.createRow(rowNb);
            for (int i = 0; i < headerEntries.length; i++) {
                row.createCell(i).setCellValue(headerEntries[i]);
            }
            rowNb++;
        }
        // Data
        while (act.hasNext()) {
            Object[] entries = activityToArray.toRowArray(act.next());
            XSSFRow row = worksheet.createRow(rowNb);
            for (int i = 0; i < entries.length; i++) {
                XSSFCell cell = row.createCell(i);
                if (entries[i] instanceof Integer) {
                    cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
                    cell.setCellValue((Integer) entries[i]);
                } else if (entries[i] instanceof Float) {
                    cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
                    XSSFCellStyle cellStyle = workbook.createCellStyle();
                    XSSFDataFormat dataFormat = workbook.createDataFormat();
                    cellStyle.setDataFormat(dataFormat.getFormat("0.0"));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((Float) entries[i]);
                } else if (entries[i] instanceof Boolean) {
                    cell.setCellType(XSSFCell.CELL_TYPE_BOOLEAN);
                    cell.setCellValue((Boolean) entries[i]);
                } else if (entries[i] instanceof Date) {
                    XSSFCellStyle cellStyle = workbook.createCellStyle();
                    XSSFDataFormat dataFormat = workbook.createDataFormat();
                    cellStyle.setDataFormat(dataFormat.getFormat(exportInputForm.getDatePattern()));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((Date) entries[i]);
                } else { // text
                    cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue((String) entries[i]);
                }
            }
            rowNb++;
        }
        workbook.write(fileOut);
        fileOut.flush();
        fileOut.close();
        return true;
    }

    private boolean exportToGoogleDrive(String fileName, Iterator<Activity> act)
            throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new com.google.api.client.json.jackson2.JacksonFactory();
        String clientId = GoogleConfigLoader.getClientId();
        String clientSecret = GoogleConfigLoader.getClientSecret();
        String redirectURI = GoogleConfigLoader.getRedirectURI();
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientId, clientSecret, Arrays.asList(DriveScopes.DRIVE))
                .setAccessType("online")
                .setApprovalPrompt("auto").build();
        String authorisationCode = exportInputForm.getAuthorisationCode();
        if (authorisationCode.length() == 0) {
            // Retrieve authorisation code URL
            String authorisationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI).build();
            exportInputForm.setAuthorisationCodeUrl(authorisationUrl);
            exportInputForm.showAuthorisationForm();
            cancelButton.setVisible(true);
            return false;
        } else {
            // Set Google Drive service
            GoogleTokenResponse response = flow.newTokenRequest(authorisationCode).setRedirectUri(redirectURI).execute();
            GoogleCredential credential = new GoogleCredential().setFromTokenResponse(response);
            Drive service = new Drive.Builder(httpTransport, jsonFactory, credential).setApplicationName("myAgilePomodoro").build();
            // Set file's metadata.
            com.google.api.services.drive.model.File googleFile = new com.google.api.services.drive.model.File();
            googleFile.setTitle(fileName);
            googleFile.setDescription("myAgilePomodoro file");
            googleFile.setMimeType("text/csv");
            // Send file
            exportCSV(fileName, act); // first, export the data to a csv file
            //String path = "./" + fileName;
            java.io.File csvFile = new java.io.File(fileName);
            FileContent mediaContent = new FileContent("text/csv", csvFile);
            // convert and send the file to Google Drive
            service.files().insert(googleFile, mediaContent).setConvert(true).execute();
            // reset the form
            exportInputForm.showExportForm();
            cancelButton.setVisible(false);
            return true;
        }
    }
}
