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
        exportFormPanel.add(fileNamelabel, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weighty = 0.5;
        JPanel fileChooserPanel = new JPanel();
        GridBagConstraints gbcf = new GridBagConstraints();
        gbcf.gridx = 0;
        gbcf.gridy = 0;
        gbcf.fill = GridBagConstraints.NONE;
        fileName.setMinimumSize(LABEL_DIMENSION);
        fileName.setPreferredSize(LABEL_DIMENSION);
        fileName.setEditable(false);
        fileName.setBackground(ColorUtil.WHITE);
        fileName.setFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize()));
        fileChooserPanel.add(fileName, gbcf);
        gbcf.gridx = 1;
        gbcf.gridy = 0;
        gbcf.fill = GridBagConstraints.NONE;
        fileChooserPanel.add(new JLabel(" "), gbcf);
        gbcf.gridx = 2;
        gbcf.gridy = 0;
        gbcf.fill = GridBagConstraints.NONE;
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
        fileChooserPanel.add(browseButton, gbcf);
        fileChooserPanel.setLayout(new GridBagLayout());
        exportFormPanel.add(fileChooserPanel, c);
    }

    @Override
    public Object[] getFileFormats() {
        return new Object[]{CSVFormat, ExcelFormat, ExcelOpenXMLFormat};
    }
}
