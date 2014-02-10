package org.mypomodoro.gui.reports.export;

import java.awt.Button;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

/**
 * Import form
 *
 * @author Phil Karoo
 */
public class ImportInputForm extends ExportInputForm {

    private FileDialog fileDialog;

    public ImportInputForm() {
        defaultFileName = "";
    }

    @Override
    protected void addFileField(GridBagConstraints c) {
        c.gridx = 0;
        c.gridy = 1;
        c.weighty = 0.5;
        FormLabel fileNamelabel = new FormLabel(
                Labels.getString("ReportListPanel.File") + "*: ");
        fileNamelabel.setMinimumSize(LABEL_DIMENSION);
        fileNamelabel.setPreferredSize(LABEL_DIMENSION);
        add(fileNamelabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0.5;
        JPanel fileChooserPanel = new JPanel();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        fileName.setMinimumSize(LABEL_DIMENSION);
        fileName.setPreferredSize(LABEL_DIMENSION);
        fileName.setEditable(false);
        fileName.setBackground(ColorUtil.WHITE);
        fileName.setFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize()));
        fileChooserPanel.add(fileName, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        fileChooserPanel.add(new JLabel(" "), gbc);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        JDialog d = new JDialog();
        fileDialog = new FileDialog(d, Labels.getString("ReportListPanel.Choose a file"), FileDialog.LOAD);
        Button browseButton = new Button(Labels.getString("ReportListPanel.Browse"));
        browseButton.setFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize()));
        browseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fileDialog.setVisible(true);
                String directory = fileDialog.getDirectory();
                String file = fileDialog.getFile();
                if (directory != null && file != null) {
                    fileName.setText(directory + file);
                }
            }
        });
        fileChooserPanel.add(browseButton, gbc);
        fileChooserPanel.setLayout(new GridBagLayout());
        add(fileChooserPanel, c);
    }
}
