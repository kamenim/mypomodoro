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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.activities.AbstractComboBoxRenderer;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.gui.export.google.GoogleConfigLoader;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.BareBonesBrowserLaunch;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * Export form
 *
 */
public class ExportInputForm extends JPanel {

    protected static final Dimension LABEL_DIMENSION = new Dimension(170, 25);
    private static final Dimension COMBO_BOX_DIMENSION = new Dimension(300, 25);
    private JCheckBox headerCheckBox = new JCheckBox();
    protected JTextField fileName = new JTextField();
    private JComboBox fileFormatComboBox = new JComboBox();
    private FormLabel separatorLabel = new FormLabel("");
    private JComboBox separatorComboBox = new JComboBox();
    protected String defaultFileName = "myAgilePomodoro";
    public final FileFormat CSVFormat = new FileFormat(FileFormat.CSVFormatName,
            FileFormat.CSVExtention);
    public final FileFormat ExcelFormat = new FileFormat("XLS (Excel 2003)",
            FileFormat.ExcelExtention);
    public final FileFormat ExcelOpenXMLFormat = new FileFormat("XLSX (Excel 2007)",
            FileFormat.ExcelOpenXMLExtention);
    private final FileFormat GoogleDriveFormat = new FileFormat(FileFormat.GoogleDriveFormatName,
            FileFormat.CSVExtention);
    private final Separator commaSeparator = new Separator(0,
            Labels.getString("ReportListPanel.Comma"), ',');
    private final Separator tabSeparator = new Separator(1,
            Labels.getString("ReportListPanel.Tab"), '\t');
    private final Separator semicolonSeparator = new Separator(2,
            Labels.getString("ReportListPanel.Semicolon"), ';');
    private final Separator editableSeparator = new Separator(3, "", ',');
    private final Patterns patterns = new Patterns();
    private final JPanel patternsPanel = new JPanel();
    private FormLabel datePatternLabel = new FormLabel("");
    // Sole pattern supported by Apache POI for non-CSV formats
    private final String excelPatterns = "m/d/yy";
    private final JPanel excelPatternsPanel = new JPanel();
    //private final JPanel columnsPanel = new JPanel();
    private final GridBagConstraints gbc = new GridBagConstraints();
    protected final JPanel exportFormPanel = new JPanel();
    private final JPanel authorisationFormPanel = new JPanel();
    // Google Drive
    private JTextField authorisationCodeTextField = new JTextField();
    private JTextField authorisationUrlTextField = new JTextField("");

    public ExportInputForm() {
        setLayout(new GridBagLayout());
        // The following three lines are necessary to make the additional jpanels to fill up the parent jpanel
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        addExportForm();
        addAuthorisationForm();
        authorisationFormPanel.setVisible(false);
    }

    private void addExportForm() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        exportFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        // Header
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.5;
        FormLabel headerlabel = new FormLabel(Labels.getString("ReportListPanel.Header") + ": ");
        headerlabel.setMinimumSize(LABEL_DIMENSION);
        headerlabel.setPreferredSize(LABEL_DIMENSION);
        exportFormPanel.add(headerlabel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.WEST;
        headerCheckBox = new JCheckBox();
        headerCheckBox.setSelected(true);
        exportFormPanel.add(headerCheckBox, c);
        // File name
        addFileField(c);
        // File formats
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.5;
        FormLabel fileFormatLabel = new FormLabel(Labels.getString("ReportListPanel.File format") + "*: ");
        fileFormatLabel.setMinimumSize(LABEL_DIMENSION);
        fileFormatLabel.setPreferredSize(LABEL_DIMENSION);
        exportFormPanel.add(fileFormatLabel, c);
        c.gridx = 1;
        c.gridy = 2;
        c.weighty = 0.5;
        Object fileFormats[] = getFileFormats();
        fileFormatComboBox = new JComboBox(fileFormats);
        fileFormatComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FileFormat selectedFormat = (FileFormat) fileFormatComboBox.getSelectedItem();
                if (selectedFormat.equals(ExcelFormat) || selectedFormat.equals(ExcelOpenXMLFormat)) {
                    patternsPanel.setVisible(false);
                    separatorLabel.setVisible(false);
                    separatorComboBox.setVisible(false);
                    datePatternLabel.setVisible(false);
                    excelPatternsPanel.setVisible(false);
                } else {
                    if (selectedFormat.equals(GoogleDriveFormat)) {
                        separatorComboBox.removeItem(tabSeparator);
                        separatorComboBox.removeItem(semicolonSeparator);
                        separatorComboBox.removeItem(editableSeparator);
                    } else {
                        separatorComboBox.addItem(tabSeparator);
                        separatorComboBox.addItem(semicolonSeparator);
                        separatorComboBox.addItem(editableSeparator);
                    }
                    patternsPanel.setVisible(true);
                    separatorLabel.setVisible(true);
                    separatorComboBox.setVisible(true);
                    datePatternLabel.setVisible(true);
                    excelPatternsPanel.setVisible(false);
                }
            }
        });
        fileFormatComboBox.setMinimumSize(COMBO_BOX_DIMENSION);
        fileFormatComboBox.setPreferredSize(COMBO_BOX_DIMENSION);
        fileFormatComboBox.setBackground(ColorUtil.WHITE);
        fileFormatComboBox.setRenderer(new AbstractComboBoxRenderer());
        exportFormPanel.add(fileFormatComboBox, c);
        // Date patterns
        c.gridx = 0;
        c.gridy = 3;
        c.weighty = 0.5;
        datePatternLabel = new FormLabel(
                Labels.getString("ReportListPanel.Date pattern") + "*: ");
        datePatternLabel.setMinimumSize(LABEL_DIMENSION);
        datePatternLabel.setPreferredSize(LABEL_DIMENSION);
        exportFormPanel.add(datePatternLabel, c);
        c.gridx = 1;
        c.gridy = 3;
        c.weighty = 0.5;
        addPaternsComboBox(c);
        addExcelPaternsComboBox(c);
        excelPatternsPanel.setVisible(false);
        // Separator
        c.gridx = 0;
        c.gridy = 4;
        c.weighty = 0.5;
        separatorLabel = new FormLabel(
                Labels.getString("ReportListPanel.Separator") + "*: ");
        fileFormatLabel.setMinimumSize(LABEL_DIMENSION);
        fileFormatLabel.setPreferredSize(LABEL_DIMENSION);
        exportFormPanel.add(separatorLabel, c);
        c.gridx = 1;
        c.gridy = 4;
        c.weighty = 0.5;
        Object separators[] = new Object[]{commaSeparator, tabSeparator,
            semicolonSeparator, editableSeparator};
        separatorComboBox = new JComboBox(separators);
        separatorComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Separator selectedSeparator = new Separator();
                // editable field has been edited and has become a String
                if (separatorComboBox.getSelectedItem() instanceof String) {
                    String separatorText = (String) separatorComboBox.getSelectedItem();
                    if (separatorText.length() > 0) {
                        char[] sepArray = separatorText.toCharArray();
                        if (sepArray.length == 1) { // Character
                            editableSeparator.setSeparatorText(separatorText);
                            editableSeparator.setSeparator(sepArray[0]);
                        } else {
                            editableSeparator.setSeparatorText("");
                            editableSeparator.setSeparator(',');
                        }
                    }
                    separatorComboBox.setSelectedItem(editableSeparator);
                } else {
                    selectedSeparator = (Separator) separatorComboBox.getSelectedItem();
                }
                if (selectedSeparator.getSeparatorIndex() == editableSeparator.getSeparatorIndex()) { // editable field
                    separatorComboBox.setEditable(true);
                } else {
                    separatorComboBox.setEditable(false);
                }
            }
        });
        separatorComboBox.setMinimumSize(COMBO_BOX_DIMENSION);
        separatorComboBox.setPreferredSize(COMBO_BOX_DIMENSION);
        separatorComboBox.setBackground(ColorUtil.WHITE);
        separatorComboBox.setRenderer(new AbstractComboBoxRenderer());
        exportFormPanel.add(separatorComboBox, c);
        // Columns
        /*c.gridx = 0;
         c.gridy = 5;
         c.weighty = 0.5;
         c.gridwidth = 2;
         addColumnsComboBoxes(c);*/
        add(exportFormPanel, gbc);
    }

    public Object[] getFileFormats() {
        Object[] fileFormat;
        if (GoogleConfigLoader.isValid()) {
            fileFormat = new Object[]{CSVFormat, ExcelFormat, ExcelOpenXMLFormat, GoogleDriveFormat};
        } else {
            fileFormat = new Object[]{CSVFormat, ExcelFormat, ExcelOpenXMLFormat};
        }
        return fileFormat;
    }

    private void addAuthorisationForm() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        authorisationFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        FormLabel authorisationLabel = new FormLabel(
                Labels.getString("ReportListPanel.Please open the following URL in your browser then type the authorization code") + ": ");
        authorisationLabel.setMinimumSize(new Dimension(500, 25));
        authorisationLabel.setPreferredSize(new Dimension(500, 25));
        authorisationFormPanel.add(authorisationLabel, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        authorisationUrlTextField.setSelectionColor(ColorUtil.BLUE_ROW);
        authorisationUrlTextField.setEnabled(true); // make it copiable
        authorisationUrlTextField.setEditable(false);
        authorisationUrlTextField.setMargin(new Insets(10, 10, 10, 10));
        authorisationUrlTextField.setCaretPosition(0);
        // underline url
        Map<TextAttribute, Object> map = new HashMap<TextAttribute, Object>();
        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        authorisationUrlTextField.setFont(getFont().deriveFont(map));
        authorisationUrlTextField.setForeground(Color.BLUE);
        authorisationUrlTextField.setMinimumSize(new Dimension(500, 50));
        authorisationUrlTextField.setPreferredSize(new Dimension(500, 50));
        class customerMouseListener extends MouseAdapter {

            JTextField urlTextField;

            public customerMouseListener(JTextField urlTextField) {
                this.urlTextField = urlTextField;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                BareBonesBrowserLaunch.openURL(urlTextField.getText().trim());
            }
        }
        authorisationUrlTextField.addMouseListener(new customerMouseListener(authorisationUrlTextField));
        authorisationFormPanel.add(authorisationUrlTextField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.5;
        c.gridwidth = 1;
        FormLabel authorisationCodeLabel = new FormLabel(
                Labels.getString("ReportListPanel.Authorisation code") + "*: ");
        authorisationCodeLabel.setMinimumSize(LABEL_DIMENSION);
        authorisationCodeLabel.setPreferredSize(LABEL_DIMENSION);
        authorisationFormPanel.add(authorisationCodeLabel, c);
        c.gridx = 1;
        c.gridy = 2;
        c.weighty = 0.5;
        c.gridwidth = 1;
        authorisationCodeTextField = new JTextField();
        authorisationCodeTextField.setMinimumSize(COMBO_BOX_DIMENSION);
        authorisationCodeTextField.setPreferredSize(COMBO_BOX_DIMENSION);
        authorisationFormPanel.add(authorisationCodeTextField, c);
        add(authorisationFormPanel, gbc);
    }

    /*private void addColumnsComboBoxes(GridBagConstraints c) {
     int numberColumns = new Columns().getLength() - 1;
     int numberComboBoxPerLine = 6;
     columnsPanel.setLayout(new GridLayout(Math.round((float) numberColumns / (float) numberComboBoxPerLine), numberComboBoxPerLine));
     GridBagConstraints gbc = new GridBagConstraints();
     gbc.fill = GridBagConstraints.HORIZONTAL;
     gbc.anchor = GridBagConstraints.NORTH;

     for (int i = 0; i < numberColumns; i++) {
     gbc.gridx = i;
     gbc.gridy = 0;
     JComboBox cb = new Columns().getColumnsComboBox();
     cb.setSelectedIndex(i);
     columnsPanel.add(cb, gbc);
     }

     final Component[] comboBoxes = columnsPanel.getComponents();
     for (int i = 0; i < comboBoxes.length; i++) {
     final int index = i;
     final JComboBox cb = (JComboBox) comboBoxes[i];
     cb.addActionListener(new ActionListener() {

     @Override
     public void actionPerformed(ActionEvent e) {
     if (cb.getSelectedItem().toString().length() == 0) { // empty field
     for (int j = index + 1; j < comboBoxes.length; j++) {
     JComboBox cb1 = (JComboBox) comboBoxes[j];
     cb1.setSelectedIndex(comboBoxes.length); // empty field selected
     cb1.setEnabled(false);
     }
     } else {
     if (index + 1 < comboBoxes.length) { // Enable field next to the right
     JComboBox cb1 = (JComboBox) comboBoxes[index + 1];
     cb1.setEnabled(true);
     }
     }
     }
     });
     }
     exportFormPanel.add(columnsPanel, c);
     }*/
    private void addPaternsComboBox(GridBagConstraints c) {
        patternsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        patternsPanel.add(patterns.getDatePatternsComboBox1(), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        patternsPanel.add(new JLabel("  "), gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        patternsPanel.add(patterns.getDateSeparatorComboBox1(), gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        patternsPanel.add(new JLabel("  "), gbc);
        gbc.gridx = 4;
        gbc.gridy = 0;
        patternsPanel.add(patterns.getDatePatternsComboBox2(), gbc);
        gbc.gridx = 5;
        gbc.gridy = 0;
        patternsPanel.add(new JLabel("  "), gbc);
        gbc.gridx = 6;
        gbc.gridy = 0;
        patternsPanel.add(patterns.getDateSeparatorComboBox2(), gbc);
        gbc.gridx = 7;
        gbc.gridy = 0;
        patternsPanel.add(new JLabel("  "), gbc);
        gbc.gridx = 8;
        gbc.gridy = 0;
        patternsPanel.add(patterns.getDatePatternsComboBox3(), gbc);
        exportFormPanel.add(patternsPanel, c);
    }

    private void addExcelPaternsComboBox(GridBagConstraints c) {
        excelPatternsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        excelPatternsPanel.add(new JLabel(excelPatterns), gbc);
        excelPatternsPanel.setBackground(ColorUtil.WHITE);
        exportFormPanel.add(excelPatternsPanel, c);
    }

    public String getFileExtention() {
        return ((FileFormat) fileFormatComboBox.getSelectedItem()).getExtention();
    }

    public boolean isFileCSVFormat() {
        return ((FileFormat) fileFormatComboBox.getSelectedItem()).isCSVFormat();
    }

    public boolean isFileExcelFormat() {
        return ((FileFormat) fileFormatComboBox.getSelectedItem()).isExcelFormat();
    }

    public boolean isFileExcelOpenXMLFormat() {
        return ((FileFormat) fileFormatComboBox.getSelectedItem()).isExcelOpenXMLFormat();
    }

    public boolean isFileGoogleDriveFormat() {
        return ((FileFormat) fileFormatComboBox.getSelectedItem()).isGoogleDriveFormat();
    }

    public String getFileName() {
        return fileName.getText().trim();
    }

    public String getDatePattern() {
        FileFormat selectedFormat = (FileFormat) fileFormatComboBox.getSelectedItem();
        if (selectedFormat.equals(ExcelFormat) || selectedFormat.equals(ExcelOpenXMLFormat)) {
            return excelPatterns;
        } else {
            return patterns.getDatePattern();
        }
    }

    public char getSeparator() {
        return ((Separator) separatorComboBox.getSelectedItem()).getSeparator();
    }

    public boolean isHeaderSelected() {
        return headerCheckBox.isSelected();
    }

    public void initFileName() {
        fileName.setText(defaultFileName);
    }

    public void initSeparatorComboBox() {
        separatorComboBox.setSelectedIndex(commaSeparator.getSeparatorIndex());
    }

    public class FileFormat {

        public static final String CSVExtention = "csv";
        public static final String CSVFormatName = "CSV";
        public static final String ExcelExtention = "xls";
        public static final String ExcelOpenXMLExtention = "xlsx";
        public static final String GoogleDriveFormatName = "Google Drive";

        private final String formatName;
        private final String extention;

        public FileFormat(String formatName, String extention) {
            this.formatName = formatName;
            this.extention = extention;
        }

        public String getExtention() {
            return extention;
        }

        public boolean isCSVFormat() {
            return extention.equals(CSVExtention) && formatName.equals(CSVFormatName);
        }

        public boolean isExcelFormat() {
            return extention.equals(ExcelExtention);
        }

        public boolean isExcelOpenXMLFormat() {
            return extention.equals(ExcelOpenXMLExtention);
        }

        public boolean isGoogleDriveFormat() {
            return extention.equals(CSVExtention) && formatName.equals(GoogleDriveFormatName);
        }

        @Override
        public String toString() {
            return formatName;
        }
    }

    private class Separator {

        private int separatorIndex;
        private String separatorText;
        private char separator;

        public Separator() {
        }

        public Separator(int separatorIndex, String separatorText,
                char separator) {
            this.separatorIndex = separatorIndex;
            this.separatorText = separatorText;
            this.separator = separator;
        }

        public int getSeparatorIndex() {
            return separatorIndex;
        }

        public String getSeparatorText() {
            return separatorText;
        }

        public char getSeparator() {
            return separator;
        }

        public void setSeparator(char separator) {
            this.separator = separator;
        }

        public void setSeparatorText(String separatorText) {
            this.separatorText = separatorText;
        }

        @Override
        public String toString() {
            return separatorText;
        }
    }

    private class Patterns {

        private final String datePatterns[] = new String[]{"d", "dd", "M",
            "MM", "MMM", "MMMM", "yy", "yyyy"};
        private final String dateSeparators[] = new String[]{" ", "/", "-",
            "."};
        private final JComboBox datePatternsComboBox1 = new JComboBox(
                datePatterns);
        private final JComboBox dateSeparatorComboBox1 = new JComboBox(
                dateSeparators);
        private final JComboBox datePatternsComboBox2 = new JComboBox(
                datePatterns);
        private final JComboBox dateSeparatorComboBox2 = new JComboBox(
                dateSeparators);
        private final JComboBox datePatternsComboBox3 = new JComboBox(
                datePatterns);

        public Patterns() {
            if (DateUtil.isUSLocale()) {
                datePatternsComboBox1.setSelectedIndex(3);
                datePatternsComboBox2.setSelectedIndex(0);
                datePatternsComboBox3.setSelectedIndex(7);
            } else {
                datePatternsComboBox1.setSelectedIndex(0);
                datePatternsComboBox2.setSelectedIndex(3);
                datePatternsComboBox3.setSelectedIndex(7);
            }
            datePatternsComboBox1.setRenderer(new AbstractComboBoxRenderer());
            dateSeparatorComboBox1.setRenderer(new AbstractComboBoxRenderer());
            datePatternsComboBox2.setRenderer(new AbstractComboBoxRenderer());
            dateSeparatorComboBox2.setRenderer(new AbstractComboBoxRenderer());
            datePatternsComboBox3.setRenderer(new AbstractComboBoxRenderer());
        }

        public JComboBox getDatePatternsComboBox1() {
            return datePatternsComboBox1;
        }

        public JComboBox getDateSeparatorComboBox1() {
            return dateSeparatorComboBox1;
        }

        public JComboBox getDatePatternsComboBox2() {
            return datePatternsComboBox2;
        }

        public JComboBox getDateSeparatorComboBox2() {
            return dateSeparatorComboBox2;
        }

        public JComboBox getDatePatternsComboBox3() {
            return datePatternsComboBox3;
        }

        public String getDatePattern() {
            return datePatternsComboBox1.getSelectedItem().toString()
                    + dateSeparatorComboBox1.getSelectedItem().toString()
                    + datePatternsComboBox2.getSelectedItem().toString()
                    + dateSeparatorComboBox2.getSelectedItem().toString()
                    + datePatternsComboBox3.getSelectedItem().toString();
        }
    }

    protected void addFileField(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.5;
        FormLabel fileNamelabel = new FormLabel(
                Labels.getString("ReportListPanel.File name") + "*: ");
        fileNamelabel.setMinimumSize(LABEL_DIMENSION);
        fileNamelabel.setPreferredSize(LABEL_DIMENSION);
        exportFormPanel.add(fileNamelabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0.5;
        fileName = new JTextField();
        fileName.setText(defaultFileName);
        fileName.setMinimumSize(COMBO_BOX_DIMENSION);
        fileName.setPreferredSize(COMBO_BOX_DIMENSION);
        exportFormPanel.add(fileName, c);
    }

    private class Column {

        private int columnIndex;
        private String columnText;

        public Column() {
        }

        public Column(int columnIndex, String columnText) {
            this.columnIndex = columnIndex;
            this.columnText = columnText;
        }

        public int getcolumnIndex() {
            return columnIndex;
        }

        public String getcolumnText() {
            return columnText;
        }

        @Override
        public String toString() {
            return columnText;
        }
    }

    private class Columns {

        private final String[] headerEntries = new String[]{"U",
            Labels.getString("Common.Date"),
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
            Labels.getString("Common.Priority"),
            ""};
        private JComboBox columnsComboBox = new JComboBox();

        public Columns() {
            Object[] columns = new Object[headerEntries.length];
            for (int i = 0; i < headerEntries.length; i++) {
                columns[i] = new Column(i, headerEntries[i]);
            }
            columnsComboBox = new JComboBox(columns);
            columnsComboBox.setBackground(ColorUtil.WHITE);
            columnsComboBox.setFont(getFont().deriveFont(Font.PLAIN, getFont().getSize() - 2));
        }

        public JComboBox getColumnsComboBox() {
            return columnsComboBox;
        }

        public int getLength() {
            return headerEntries.length;
        }
    }

    public static class activityToArray {

        public static String[] toArray(Activity activity) {
            return toArray(activity, "dd MM yyyy");
        }

        public static String[] toArray(Activity activity, String datePattern) {
            String[] attributes = new String[19];
            attributes[0] = activity.isUnplanned() ? "1" : "0";
            attributes[1] = DateUtil.getFormatedDate(activity.getDate(), datePattern);
            attributes[2] = DateUtil.getFormatedDate(activity.getDateCompleted(), datePattern);
            attributes[3] = activity.getName();
            attributes[4] = activity.getEstimatedPoms() + "";
            attributes[5] = activity.getOverestimatedPoms() + "";
            attributes[6] = activity.getActualPoms() + "";
            attributes[7] = (activity.getActualPoms() - activity.getEstimatedPoms()) + "";
            attributes[8] = activity.getOverestimatedPoms() > 0 ? (activity.getActualPoms() - activity.getEstimatedPoms() - activity.getOverestimatedPoms()) + "" : "";
            attributes[9] = activity.getNumInternalInterruptions() + "";
            attributes[10] = activity.getNumInterruptions() + "";
            attributes[11] = activity.getType();
            attributes[12] = activity.getAuthor();
            attributes[13] = activity.getPlace();
            attributes[14] = activity.getDescription();
            attributes[15] = activity.getNotes();
            attributes[16] = activity.getStoryPoints() + "";
            attributes[17] = activity.getIteration() + "";
            attributes[18] = activity.getPriority() + "";
            return attributes;
        }

        public static Object[] toRowArray(Activity activity) {
            Object[] attributes = new Object[19];
            attributes[0] = activity.isUnplanned();
            attributes[1] = activity.getDate();
            attributes[2] = activity.getDateCompleted();
            attributes[3] = activity.getName();
            attributes[4] = activity.getEstimatedPoms();
            attributes[5] = activity.getOverestimatedPoms();
            attributes[6] = activity.getActualPoms();
            attributes[7] = activity.getActualPoms() - activity.getEstimatedPoms();
            attributes[8] = activity.getOverestimatedPoms() > 0 ? (activity.getActualPoms() - activity.getEstimatedPoms() - activity.getOverestimatedPoms()) : "";
            attributes[9] = activity.getNumInternalInterruptions();
            attributes[10] = activity.getNumInterruptions();
            attributes[11] = activity.getType();
            attributes[12] = activity.getAuthor();
            attributes[13] = activity.getPlace();
            attributes[14] = activity.getDescription();
            attributes[15] = activity.getNotes();
            attributes[16] = activity.getStoryPoints();
            attributes[17] = activity.getIteration();
            attributes[18] = activity.getPriority();
            return attributes;
        }
    }

    public void showAuthorisationForm() {
        exportFormPanel.setVisible(false);
        authorisationFormPanel.setVisible(true);
    }

    public void showExportForm() {
        exportFormPanel.setVisible(true);
        authorisationFormPanel.setVisible(false);
        setAuthorisationCode("");
        setAuthorisationCodeUrl("");
    }

    public void setAuthorisationCodeUrl(String authorisationUrl) {
        this.authorisationUrlTextField.setText(authorisationUrl);
        this.authorisationUrlTextField.setCaretPosition(0);
    }

    private void setAuthorisationCode(String authorisationCode) {
        this.authorisationCodeTextField.setText(authorisationCode);
    }

    public String getAuthorisationCode() {
        return authorisationCodeTextField.getText().trim();
    }
}