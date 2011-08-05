package org.mypomodoro.gui.reports.export;

import au.com.bytecode.opencsv.CSVWriter;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
    private final String[] headerEntries = new String[]{"U", Labels.getString("Common.Date"), Labels.getString("ReportListPanel.Time"),
        Labels.getString("Common.Title"), Labels.getString("ReportListPanel.Estimated"), Labels.getString("ReportListPanel.Overestimated"),
        Labels.getString("ReportListPanel.Real"), Labels.getString("ReportListPanel.Diff I"), Labels.getString("ReportListPanel.Diff II"),
        Labels.getString("ToDoListPanel.Internal"), Labels.getString("ToDoListPanel.External"),
        Labels.getString("Common.Type"), Labels.getString("Common.Author"), Labels.getString("Common.Place"),
        Labels.getString("Common.Description"), Labels.getString("Common.Comment")};

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

    private void export() {
        if (activities.size() > 0) {

            try {
                String fileName = exportInputForm.getFileName() + "." + exportInputForm.getFileExtention();
                Iterator<Activity> act = activities.iterator();
                if (exportInputForm.isFileCSVFormat()) {
                    exportCSV(fileName, act);
                } else if (exportInputForm.isFileExcelFormat()) {
                    exportExcel(fileName, act);
                }
                JFrame window = new JFrame();
                String title = Labels.getString("ReportListPanel.Export reports");
                String message = Labels.getString("ReportListPanel.Reports exported to file {0}", fileName);
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

    private void exportCSV(String fileName, Iterator<Activity> act) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(fileName), exportInputForm.getSeparator());
        // Header
        if (exportInputForm.isHeaderSelected()) {
            writer.writeNext(headerEntries);
        }
        // Data
        while (act.hasNext()) {
            String[] entries = act.next().toArray(exportInputForm.getDatePattern());
            writer.writeNext(entries);
        }
        writer.close();
    }

    private void exportExcel(String fileName, Iterator<Activity> act) throws IOException {
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
            Object[] entries = act.next().toRowArray();
            HSSFRow row = worksheet.createRow(rowNb);
            for (int i = 0; i < entries.length; i++) {
                HSSFCell cell = row.createCell(i);
                if (entries[i] instanceof Integer) {
                    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    cell.setCellValue((Integer) entries[i]);
                } else if (entries[i] instanceof Boolean) {
                    cell.setCellType(HSSFCell.CELL_TYPE_BOOLEAN);
                    cell.setCellValue((Boolean) entries[i]);
                } else if (entries[i] instanceof Date) {
                    HSSFCellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat(exportInputForm.getDatePattern())); // no other pattern for dates
                    cell.setCellStyle(cellStyle);
                    //cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                    cell.setCellValue((Date) entries[i]);
                } else { // text
                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                    cell.setCellValue((String) entries[i]);
                }
            }
            rowNb++;
        }

        // index from 0,0... cell A1 is cell(0,0)

        /*cellStyle = workbook.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
        cellD1.setCellStyle(cellStyle);*/

        workbook.write(fileOut);
        fileOut.flush();
        fileOut.close();
    }

    @Override
    public void showInfo(Activity activity) {
    }

    @Override
    public void clearInfo() {
    }
}