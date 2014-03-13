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
package org.mypomodoro.gui.reports.export;

import au.com.bytecode.opencsv.CSVReader;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;

import java.io.FileWriter;
import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.AbstractPomodoroButton;
import org.mypomodoro.gui.AbstractActivitiesPanel;
import org.mypomodoro.gui.reports.ReportsPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel to import reports
 *
 */
public class ImportPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    protected final ImportInputForm importInputForm = new ImportInputForm();
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final AbstractActivitiesPanel panel;

    public ImportPanel(AbstractActivitiesPanel panel) {
        this.panel = panel;

        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addImportInputForm();
        addImportButton();
    }

    private void addImportButton() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        // gbc.fill = GridBagConstraints.NONE;
        JButton importButton = new AbstractPomodoroButton(
                Labels.getString("ReportListPanel.Import"));
        importButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (importInputForm.getFileName().length() == 0) {
                    importInputForm.initFileName();
                }
                if (importInputForm.getEditableSeparatorText().length() == 0) { // editable
                    // field
                    importInputForm.initSeparatorComboBox();
                }
                importData();
            }
        });
        add(importButton, gbc);
    }

    private void addImportInputForm() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        add(new JScrollPane(importInputForm), gbc);
    }

    private void importData() {
        String fileName = importInputForm.getFileName(); // path
        if (fileName != null && fileName.length() > 0) {
            try {
                if (importInputForm.isFileCSVFormat()) {
                    importCSV(fileName);
                } else if (importInputForm.isFileExcelFormat()) {
                    importExcel(fileName);
                } else if (importInputForm.isFileExcelOpenXMLFormat()) {
                    importExcelx(fileName);
                }
                panel.refresh();
                String title = Labels.getString("ReportListPanel.Import");
                String message = Labels.getString(
                        "ReportListPanel.Data imported",
                        fileName);
                JOptionPane.showConfirmDialog(Main.gui, message, title,
                        JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                String title = Labels.getString("Common.Error");
                String message = Labels.getString("ReportListPanel.Import failed. See error log.");
                JOptionPane.showConfirmDialog(Main.gui, message, title,
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
                writeErrorFile(ex.toString());
            }
        }
    }

    private void writeErrorFile(String error) {
        try {
            FileWriter fstream = new FileWriter("error.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(error);
            out.close();
        } catch (Exception e) {
            // Do nothing
        }
    }

    private void importCSV(String fileName) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(fileName), importInputForm.getSeparator(), '\"', importInputForm.isHeaderSelected() ? 1 : 0);
        String[] line;
        while ((line = reader.readNext()) != null) {
            insertData(line);
        }
    }

    private void importExcel(String fileName) throws Exception {
        InputStream myxls = new FileInputStream(fileName);
        HSSFWorkbook book = new HSSFWorkbook(myxls);
        FormulaEvaluator eval = book.getCreationHelper().createFormulaEvaluator();
        HSSFSheet sheet = book.getSheetAt(0);
        if (importInputForm.isHeaderSelected()) {
            sheet.removeRow(sheet.getRow(0));
        }
        for (Row row : sheet) {
            String[] line = new String[19];
            int i = 0;
            for (Cell cell : row) {
                line[i] = getCellContent(cell, eval);
                i++;
            }
            insertData(line);
        }
        myxls.close();
    }

    private void importExcelx(String fileName) throws Exception {
        InputStream myxlsx = new FileInputStream(fileName);
        XSSFWorkbook book = new XSSFWorkbook(myxlsx);
        FormulaEvaluator eval = book.getCreationHelper().createFormulaEvaluator();
        XSSFSheet sheet = book.getSheetAt(0);
        if (importInputForm.isHeaderSelected()) {
            sheet.removeRow(sheet.getRow(0));
        }
        for (Row row : sheet) {
            String[] line = new String[19];
            int i = 0;
            for (Cell cell : row) {
                line[i] = getCellContent(cell, eval);
                i++;
            }
            insertData(line);
        }
        myxlsx.close();
    }

    private String getCellContent(Cell cell, FormulaEvaluator eval) {
        String value = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getStringCellValue();
                break;
            // TODO fix float issue with xlsx format (story points)
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    CellValue cellValue = eval.evaluate(cell);
                    value = org.mypomodoro.util.DateUtil.getFormatedDate(DateUtil.getJavaDate(cellValue.getNumberValue(), true), importInputForm.getDatePattern());
                } else if (cell.getCellStyle().getDataFormat() == new XSSFWorkbook().createDataFormat().getFormat(importInputForm.getDatePattern())) {
                    CellValue cellValue = eval.evaluate(cell);
                    value = org.mypomodoro.util.DateUtil.getFormatedDate(DateUtil.getJavaDate(cellValue.getNumberValue(), true), importInputForm.getDatePattern());
                } else if (cell.getCellStyle().getDataFormat() == HSSFDataFormat.getBuiltinFormat("0.0")) {
                    value = (float) cell.getNumericCellValue() + "";
                } else if (cell.getCellStyle().getDataFormat() == new XSSFWorkbook().createDataFormat().getFormat("0.0")) {
                    value = (float) cell.getNumericCellValue() + "";
                } else {
                    value = (int) cell.getNumericCellValue() + "";
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = cell.getBooleanCellValue() ? "1" : "0";
                break;
            default:
                value = "";
        }
        return value;
    }

    private void insertData(String[] line) throws Exception {
        Activity newActivity = new Activity(line[13], line[12], line[3], line[14], line[11], Integer.parseInt(line[4]),
                org.mypomodoro.util.DateUtil.getDate(line[1] + " " + line[2], importInputForm.getDatePattern()), Integer.parseInt(line[5]), Integer.parseInt(line[6]),
                Integer.parseInt(line[9]), Integer.parseInt(line[10]), line[15],
                !line[0].equals("0"), panel instanceof ReportsPanel);
        try {
            newActivity.setStoryPoints(Float.parseFloat(line[16]));
            newActivity.setIteration(Integer.parseInt(line[17]));
            newActivity.setPriority(Integer.parseInt(line[18]));
        } catch (NumberFormatException e) {
            newActivity.setStoryPoints(0);
            newActivity.setIteration(-1);
            newActivity.setPriority(-1);
        } catch (ArrayIndexOutOfBoundsException e) {
            newActivity.setStoryPoints(0);
            newActivity.setIteration(-1);
            newActivity.setPriority(-1);
        }
        panel.addActivity(newActivity);
    }
}
