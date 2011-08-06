package org.mypomodoro.gui.reports.export;

import java.awt.event.ActionEvent;
import org.mypomodoro.gui.create.*;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.mypomodoro.util.Labels;

/**
 *
 * @author Phil Karoo
 */
public class ExportInputForm extends JPanel {

    private static final Dimension PANEL_DIMENSION = new Dimension(400, 50);
    private static final Dimension LABEL_DIMENSION = new Dimension(170, 25);
    private static final Dimension COMBO_BOX_DIMENSION = new Dimension(300, 25);
    private JCheckBox headerCheckBox = new JCheckBox();
    private JTextField fileName = new JTextField();
    private JComboBox fileFormatComboBox = new JComboBox();
    private FormLabel separatorLabel = new FormLabel("");
    private JComboBox separatorComboBox = new JComboBox();
    private String defaultFileName = "myPomodoro";
    private FileFormat CSVFormat = new FileFormat("CSV", FileFormat.CSVExtention);
    private FileFormat ExcelFormat = new FileFormat("XLS (Excel 2003)", FileFormat.ExcelExtention);
    private Separator commaSeparator = new Separator(0, Labels.getString("ReportListPanel.Comma"), ',');
    private Separator tabSeparator = new Separator(1, Labels.getString("ReportListPanel.Tab"), '\t');
    private Separator semicolonSeparator = new Separator(2, Labels.getString("ReportListPanel.Semicolon"), ';');
    private Separator editableSeparator = new Separator(3, "", ',');
    private Patterns patterns = new Patterns();
    private JPanel patternsPanel = new JPanel();
    private String excelPatterns = "m/d/yy";
    private JPanel excelPatternsPanel = new JPanel();

    public ExportInputForm() {
        setBorder(new TitledBorder(new EtchedBorder(), ""));
        setMinimumSize(PANEL_DIMENSION);
        setPreferredSize(PANEL_DIMENSION);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Header
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.5;
        FormLabel headerlabel = new FormLabel(Labels.getString("ReportListPanel.Header") + ": ");
        headerlabel.setMinimumSize(LABEL_DIMENSION);
        headerlabel.setPreferredSize(LABEL_DIMENSION);
        add(headerlabel, c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.5;
        headerCheckBox = new JCheckBox();
        headerCheckBox.setSelected(true);
        headerCheckBox.setMinimumSize(COMBO_BOX_DIMENSION);
        headerCheckBox.setPreferredSize(COMBO_BOX_DIMENSION);
        add(headerCheckBox, c);
        // File name
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.5;
        FormLabel fileNamelabel = new FormLabel(Labels.getString("ReportListPanel.File name") + "*: ");
        fileNamelabel.setMinimumSize(LABEL_DIMENSION);
        fileNamelabel.setPreferredSize(LABEL_DIMENSION);
        add(fileNamelabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0.5;
        fileName = new JTextField();
        fileName.setText(defaultFileName);
        fileName.setMinimumSize(COMBO_BOX_DIMENSION);
        fileName.setPreferredSize(COMBO_BOX_DIMENSION);
        add(fileName, c);
        // File formats
        c.gridx = 0;
        c.gridy = 2;
        c.weighty = 0.5;
        FormLabel fileFormatLabel = new FormLabel(Labels.getString("ReportListPanel.File format") + "*: ");
        fileFormatLabel.setMinimumSize(LABEL_DIMENSION);
        fileFormatLabel.setPreferredSize(LABEL_DIMENSION);
        add(fileFormatLabel, c);
        c.gridx = 1;
        c.gridy = 2;
        c.weighty = 0.5;
        Object fileFormats[] = new Object[]{CSVFormat, ExcelFormat};
        fileFormatComboBox = new JComboBox(fileFormats);
        fileFormatComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FileFormat selectedFormat = (FileFormat) fileFormatComboBox.getSelectedItem();
                if (selectedFormat.equals(ExcelFormat)) {
                    patternsPanel.setVisible(false);
                    separatorLabel.setVisible(false);
                    separatorComboBox.setVisible(false);
                    excelPatternsPanel.setVisible(true);

                } else {
                    patternsPanel.setVisible(true);
                    separatorLabel.setVisible(true);
                    separatorComboBox.setVisible(true);
                    excelPatternsPanel.setVisible(false);
                }
            }
        });
        fileFormatComboBox.setMinimumSize(COMBO_BOX_DIMENSION);
        fileFormatComboBox.setPreferredSize(COMBO_BOX_DIMENSION);
        add(fileFormatComboBox, c);
        // Date patterns
        c.gridx = 0;
        c.gridy = 3;
        c.weighty = 0.5;
        FormLabel datePatternLabel = new FormLabel(Labels.getString("ReportListPanel.Date pattern") + "*: ");
        datePatternLabel.setMinimumSize(LABEL_DIMENSION);
        datePatternLabel.setPreferredSize(LABEL_DIMENSION);
        add(datePatternLabel, c);
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
        separatorLabel = new FormLabel(Labels.getString("ReportListPanel.Separator") + "*: ");
        fileFormatLabel.setMinimumSize(LABEL_DIMENSION);
        fileFormatLabel.setPreferredSize(LABEL_DIMENSION);
        add(separatorLabel, c);
        c.gridx = 1;
        c.gridy = 4;
        c.weighty = 0.5;
        Object separators[] = new Object[]{commaSeparator, tabSeparator, semicolonSeparator, editableSeparator};
        separatorComboBox = new JComboBox(separators);
        separatorComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Separator selectedSeparator = new Separator();
                if (separatorComboBox.getSelectedItem() instanceof String) { // editable field has been edited and has become a String
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
        add(separatorComboBox, c);
    }

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
        add(patternsPanel, c);

    }

    private void addExcelPaternsComboBox(GridBagConstraints c) {
        excelPatternsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        excelPatternsPanel.add(new JLabel(excelPatterns), gbc);
        add(excelPatternsPanel, c);
    }

    public String getFileExtention() {
        return ( (FileFormat) fileFormatComboBox.getSelectedItem() ).getExtention();
    }

    public boolean isFileCSVFormat() {
        return ( (FileFormat) fileFormatComboBox.getSelectedItem() ).isCSVFormat();
    }

    public boolean isFileExcelFormat() {
        return ( (FileFormat) fileFormatComboBox.getSelectedItem() ).isExcelFormat();
    }

    public String getFileName() {
        return fileName.getText().trim();
    }

    public String getDatePattern() {
        FileFormat selectedFormat = (FileFormat) fileFormatComboBox.getSelectedItem();
        if (selectedFormat.equals(ExcelFormat)) {
            return excelPatterns;
        } else {
            return patterns.getDatePattern();
        }
    }

    public char getSeparator() {
        return ( (Separator) separatorComboBox.getSelectedItem() ).getSeparator();
    }

    public String getEditableSeparatorText() {
        return editableSeparator.getSeparatorText();
    }

    public boolean isHeaderSelected() {
        return headerCheckBox.isSelected();
    }

    public void initFileName() {
        this.fileName.setText(defaultFileName);
    }

    public void initSeparatorComboBox() {
        separatorComboBox.setSelectedIndex(commaSeparator.getSeparatorIndex());
    }

    private class FileFormat {

        public static final String CSVExtention = "csv";
        public static final String ExcelExtention = "xls";
        private String formatName;
        private String extention;

        public FileFormat() {
        }

        public FileFormat(String formatName, String extention) {
            this.formatName = formatName;
            this.extention = extention;
        }

        public String getExtention() {
            return extention;
        }

        public boolean isCSVFormat() {
            return extention.equals(CSVExtention);
        }

        public boolean isExcelFormat() {
            return extention.equals(ExcelExtention);
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

        public Separator(int separatorIndex, String separatorText, char separator) {
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

        private String datePatterns[] = new String[]{"yy", "yyy", "M", "MM", "MMM", "MMMM", "d", "dd"};
        private String dateSeparators[] = new String[]{"/", "-", "."};
        private JComboBox datePatternsComboBox1 = new JComboBox(datePatterns);
        private JComboBox dateSeparatorComboBox1 = new JComboBox(dateSeparators);
        private JComboBox datePatternsComboBox2 = new JComboBox(datePatterns);
        private JComboBox dateSeparatorComboBox2 = new JComboBox(dateSeparators);
        private JComboBox datePatternsComboBox3 = new JComboBox(datePatterns);

        public Patterns() {
            datePatternsComboBox1.setSelectedIndex(0);
            datePatternsComboBox2.setSelectedIndex(2);
            datePatternsComboBox3.setSelectedIndex(6);
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
}