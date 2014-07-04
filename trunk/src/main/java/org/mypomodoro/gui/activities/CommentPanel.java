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
package org.mypomodoro.gui.activities;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import org.mypomodoro.buttons.AbstractButton;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays comment on the current Activity and allows editing it
 *
 */
// TODO edit mode: does not save foreground/background (only preview mode works)
// TODO make exit on Jtable only rows not the entire table
// TODO size of the buttons
public class CommentPanel extends ActivityInformationPanel {

    private final GridBagConstraints gbc = new GridBagConstraints();
    final ActivitiesPanel panel;
    protected String informationTmp = new String();

    public CommentPanel(ActivitiesPanel activitiesPanel) {
        setLayout(new GridBagLayout());
        setBorder(null);

        addEditButton();
        addCommentArea();
        this.panel = activitiesPanel;
        addSaveButton();
    }

    private void addEditButton() {
        final JButton editButton = new AbstractButton(
                Labels.getString("Edit"));
        final JButton htmlButton = new AbstractButton(
                "HTML");
        final JButton boldButton = new AbstractButton(
                "B");
        boldButton.setFont(getFont().deriveFont(Font.BOLD));
        final JButton italicButton = new AbstractButton(
                "I");
        italicButton.setFont(getFont().deriveFont(Font.ITALIC));
        final JButton underlineButton = new AbstractButton(
                "U");
        Map attributes = getFont().getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        underlineButton.setFont(getFont().deriveFont(attributes));
        final JButton backgroundColorButton = new AbstractButton(
                "ab");
        backgroundColorButton.setForeground(Color.BLUE);
        backgroundColorButton.setFont(getFont().deriveFont(attributes).deriveFont(Font.BOLD));
        final JButton foregroundColorButton = new AbstractButton(
                "A");
        foregroundColorButton.setForeground(Color.BLUE);
        foregroundColorButton.setFont(getFont().deriveFont(Font.BOLD));
        final JTextField linkTextField = new JTextField();
        final JButton linkButton = new AbstractButton(
                "Add");
        // Edit/Preview button
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0.5;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.BOTH;
        editButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!informationArea.isEditable()) {
                    informationArea.setEditable(true);
                    editButton.setText(Labels.getString("Preview"));
                    htmlButton.setVisible(true);
                    boldButton.setVisible(true);
                    italicButton.setVisible(true);
                    underlineButton.setVisible(true);
                    backgroundColorButton.setVisible(true);
                    foregroundColorButton.setVisible(true);
                    linkTextField.setVisible(true);
                    linkButton.setVisible(true);
                } else {
                    // init html button in case it was left in HTML mode
                    if (htmlButton.getText().equals("Plain")) {
                        String text = informationArea.getText();
                        informationArea.setContentType("text/html");
                        informationArea.setText(text);
                        htmlButton.setText("HTML");
                        informationArea.setCaretPosition(0);
                    }
                    informationArea.setEditable(false);
                    editButton.setText(Labels.getString("Edit"));
                    htmlButton.setVisible(false);
                    boldButton.setVisible(false);
                    italicButton.setVisible(false);
                    underlineButton.setVisible(false);
                    backgroundColorButton.setVisible(false);
                    foregroundColorButton.setVisible(false);
                    linkTextField.setVisible(false);
                    linkButton.setVisible(false);
                }
            }
        });
        add(editButton, gbc);
        // html/plain button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.weighty = 0.4;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.BOTH;
        htmlButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (htmlButton.getText().equals("HTML")) {
                    String text = informationArea.getText();
                    informationArea.setContentType("text/plain");
                    informationArea.setText(text);
                    htmlButton.setText("Plain");
                    boldButton.setVisible(false);
                    italicButton.setVisible(false);
                    underlineButton.setVisible(false);
                    backgroundColorButton.setVisible(false);
                    foregroundColorButton.setVisible(false);
                    linkTextField.setVisible(false);
                    linkButton.setVisible(false);
                } else {
                    String text = informationArea.getText();
                    informationArea.setContentType("text/html");
                    informationArea.setText(text);
                    htmlButton.setText("HTML");
                    boldButton.setVisible(true);
                    italicButton.setVisible(true);
                    underlineButton.setVisible(true);
                    backgroundColorButton.setVisible(true);
                    foregroundColorButton.setVisible(true);
                    linkTextField.setVisible(true);
                    linkButton.setVisible(true);
                }
                informationArea.setCaretPosition(0);
            }
        });
        htmlButton.setVisible(false);
        add(htmlButton, gbc);
        // bold button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        boldButton.addActionListener(new StyledEditorKit.BoldAction());
        boldButton.setVisible(false);
        add(boldButton, gbc);
        // italic button
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        italicButton.addActionListener(new StyledEditorKit.ItalicAction());
        italicButton.setVisible(false);
        add(italicButton, gbc);
        // underline button
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        underlineButton.addActionListener(new StyledEditorKit.UnderlineAction());
        underlineButton.setVisible(false);
        add(underlineButton, gbc);
        // background color button
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        backgroundColorButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        null,
                        Labels.getString("BurndownChartPanel.Choose a color"),
                        Color.BLACK);
                if (newColor != null) {
                    MutableAttributeSet COLOR = new SimpleAttributeSet();
                    StyleConstants.setBackground(COLOR, newColor);
                    int start = informationArea.getSelectionStart();
                    int end = informationArea.getSelectionEnd();
                    if (start != end) {
                        if (start > end) { // Backwards selection
                            start = end;
                        }
                        int length = informationArea.getSelectedText().length();
                        informationArea.getStyledDocument().setCharacterAttributes(start, length, COLOR, false);
                    }
                }
            }
        });
        backgroundColorButton.setVisible(false);
        add(backgroundColorButton, gbc);
        // foreground color button
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        foregroundColorButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        null,
                        Labels.getString("BurndownChartPanel.Choose a color"),
                        Color.BLACK);
                if (newColor != null) {
                    MutableAttributeSet COLOR = new SimpleAttributeSet();
                    StyleConstants.setForeground(COLOR, newColor);
                    int start = informationArea.getSelectionStart();
                    int end = informationArea.getSelectionEnd();
                    if (start != end) {
                        if (start > end) { // Backwards selection
                            start = end;
                        }
                        int length = informationArea.getSelectedText().length();
                        informationArea.getStyledDocument().setCharacterAttributes(start, length, COLOR, false);
                    }
                }
            }
        });
        foregroundColorButton.setVisible(false);
        add(foregroundColorButton, gbc);
        // add link field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        linkTextField.setVisible(false);
        add(linkTextField, gbc);
        // add link button
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        linkButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int start = informationArea.getSelectionStart();
                    int end = informationArea.getSelectionEnd();
                    if (start > end) { // Backwards selection
                        start = end;
                    }
                    informationArea.insertText(start, "<a href=\"" + linkTextField.getText() + "\">" + linkTextField.getText() + "</a>");
                    linkTextField.setText(null);
                } catch (BadLocationException ex) {
                    //
                } catch (IOException ex) {
                    //
                }

            }
        });
        linkButton.setVisible(false);
        add(linkButton, gbc);
    }

    private void addCommentArea() {
        // add the comment area
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = 4;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        informationArea.setEditable(false);

        add(new JScrollPane(informationArea), gbc);
    }

    private void addSaveButton() {
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        // gbc.fill = GridBagConstraints.NONE;
        JButton changeButton = new AbstractButton(
                Labels.getString("Common.Save"));
        changeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveComment(informationArea.getText());
            }
        });
        add(changeButton, gbc);
    }

    @Override
    public void selectInfo(final Activity activity) {
        // (editable) comment area
        // record temp comment in memory
        /*informationArea.getDocument().addDocumentListener(new DocumentListener() {

         @Override
         public void insertUpdate(DocumentEvent de) {
         int row = panel.getTable().getSelectedRow();
         Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
         final boolean selectedActivity = activity.getId() == id;
         System.err.println("selectedActivity = " + selectedActivity);
         if (selectedActivity) {
         informationTmp = informationArea.getText().trim();
         System.err.println("informationTmp=" + informationTmp);
         }
         }

         @Override
         public void removeUpdate(DocumentEvent de) {
         int row = panel.getTable().getSelectedRow();
         Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
         final boolean selectedActivity = activity.getId() == id;
         System.err.println("selectedActivity = " + selectedActivity);
         if (selectedActivity) {
         informationTmp = informationArea.getText().trim();
         }
         }

         @Override
         public void changedUpdate(DocumentEvent de) {
         int row = panel.getTable().getSelectedRow();
         Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
         final boolean selectedActivity = activity.getId() == id;
         System.err.println("selectedActivity = " + selectedActivity);
         if (selectedActivity) {
         informationTmp = informationArea.getText().trim();
         }
         }

         });*/

        // template for user stories
        if (activity.getNotes().trim().length() == 0
                && activity.isStory()) {

            String text = "";
            String path = "./User Story.template.html";
            FileInputStream file;
            try {
                file = new FileInputStream(path);
                int content;
                while ((content = file.read()) != -1) {
                    text += (char) content;
                }
                file.close();
            } catch (FileNotFoundException ex) {
                //
            } catch (IOException ex) {
                //
            }

            /*StringBuilder text = new StringBuilder();
             text.append("Story line" + "\n");
             text.append("-------------" + "\n");
             text.append("As a <user role>, I want to <action> in order to <purpose>" + "\n\n");
             text.append("User acceptance criteria" + "\n");
             text.append("----------------------------------" + "\n");
             text.append("* " + "\n");
             text.append("* " + "\n\n");
             text.append("Test cases" + "\n");
             text.append("----------------" + "\n");
             text.append("* " + "\n");
             text.append("* ");
             textMap.put("comment", text.toString());*/
            textMap.put("comment", text);
        } else {
            String text = "";
            String path = "./template.html";
            FileInputStream file;
            try {
                file = new FileInputStream(path);
                int content;
                while ((content = file.read()) != -1) {
                    text += (char) content;
                }
                file.close();
            } catch (FileNotFoundException ex) {
                //
            } catch (IOException ex) {
                //
            }
            /*if (selectedActivity) {
             System.err.println("test");
             System.err.println(informationTmp);
             textMap.put("comment", informationTmp);
             } else {*/
            //textMap.put("comment", activity.getNotes());
            textMap.put("comment", activity.getNotes());
            //}
        }
        if (activity.isFinished()) {
            informationArea.setForeground(ColorUtil.GREEN);
        } else {
            informationArea.setForeground(ColorUtil.BLACK);
        }
    }

    @Override
    public boolean isMultipleSelectionAllowed() {
        return false;
    }
}
