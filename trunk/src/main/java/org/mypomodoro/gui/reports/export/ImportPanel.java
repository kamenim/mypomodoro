package org.mypomodoro.gui.reports.export;

import au.com.bytecode.opencsv.CSVReader;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileReader;

import java.io.InputStream;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.mypomodoro.buttons.AbstractPomodoroButton;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.util.Labels;

/**
 * Panel to export reports
 * 
 * @author Phil Karoo
 */
public class ImportPanel extends JPanel implements ActivityInformation {

    private static final long serialVersionUID = 20110814L;
    protected final ImportInputForm importInputForm = new ImportInputForm();
    private final GridBagConstraints gbc = new GridBagConstraints();   

    public ImportPanel() {       

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
        add(importInputForm, gbc);
    }

    private void importData() {
            try {
                String fileName = importInputForm.getFileName(); // path
                if (importInputForm.isFileCSVFormat()) {
                    importCSV(fileName);
                } else if (importInputForm.isFileExcelFormat()) {
                    importExcel(fileName);
                }
                JFrame window = new JFrame();
                String title = Labels.getString("ReportListPanel.Import");
                String message = Labels.getString(
                        "ReportListPanel.Data imported",
                        fileName);
                JOptionPane.showConfirmDialog(window, message, title,
                        JOptionPane.DEFAULT_OPTION);
            }
            catch (Exception ex) {
                JFrame window = new JFrame();
                String title = Labels.getString("Common.Error");
                String message = Labels.getString("ReportListPanel.Import failed. See error log.");
                JOptionPane.showConfirmDialog(window, message, title,
                        JOptionPane.DEFAULT_OPTION);
            }
        
    }

    private void importCSV(String fileName) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(fileName), importInputForm.getSeparator(),'\"',importInputForm.isHeaderSelected()?1:0);
        String[] nextLine;
        while (( nextLine = reader.readNext() ) != null) {
            insertData(nextLine);
        }
    }    

    private void importExcel(String fileName) throws Exception {
        InputStream myxls = new FileInputStream(fileName);
        HSSFWorkbook book = new HSSFWorkbook(myxls);
        FormulaEvaluator eval =
                book.getCreationHelper().createFormulaEvaluator();
        HSSFSheet sheet = book.getSheetAt(0);
        for (Row row : sheet) {
            for (Cell cell : row) {
                //printCell(cell, eval);
                //System.out.print("; ");
            }
            //System.out.println();
        }
        myxls.close();
    }

    /*private static void printCell(Cell cell, FormulaEvaluator eval) {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                System.out.print("EMPTY");
                break;
            case Cell.CELL_TYPE_STRING:
                System.out.print(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    System.out.print(cell.getDateCellValue());
                } else {
                    System.out.print(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                System.out.print(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                System.out.print(cell.getCellFormula());
                CellValue cellValue = eval.evaluate(cell);
                switch (cellValue.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        double v = cellValue.getNumberValue();
                        if (DateUtil.isCellDateFormatted(cell)) {
                            System.out.print(" = "
                                    + DateUtil.getJavaDate(v, true));
                        } else {
                            System.out.print(" = " + v);
                        }
                        break;
                }
                break;
            default:
                System.out.print("DEFAULT");
        }
    }*/
    
    protected void insertData(String[] line) throws Exception {
        Activity newReport = new Activity(line[13], line[12], line[3], line[14], line[11], Integer.parseInt(line[4]), 
                org.mypomodoro.util.DateUtil.getDate(line[1] + " " + line[2], importInputForm.getDatePattern()), Integer.parseInt(line[5]), Integer.parseInt(line[6]),
                Integer.parseInt(line[9]), Integer.parseInt(line[10]), line[15], 
                line[0].equals("0")?false:true, true);
        newReport.databaseInsert();
    }

    @Override
    public void showInfo(Activity activity) {
    }

    @Override
    public void clearInfo() {
    }
}