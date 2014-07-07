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
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;

import org.mypomodoro.buttons.AbstractButton;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays comment on the current Activity and allows editing it
 *
 */
// TODO make exit on Jtable only rows not the entire table !!!
// TODO do not set foreground of informationArea to red when in pomodoro
// TODO use this CommentPanel everywhere
// TODO do not update comment when passing on selected task
// TODO problem with backward compatibility 3.0.x
// TODO Add cancel button
public class CommentPanel extends ActivityInformationPanel {

    private final GridBagConstraints gbc = new GridBagConstraints();
    final ActivitiesPanel panel;
    protected String informationTmp = new String();
    private final JButton saveButton = new AbstractButton(Labels.getString("Common.Save"));
    

    public CommentPanel(ActivitiesPanel activitiesPanel) {
        this.panel = activitiesPanel;

        setLayout(new GridBagLayout());
        setBorder(null);

        informationArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent de) {
                
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                //saveButton.setText(saveButton.getText() + " *");
            }
        });

        addEditButton();
        addCommentArea();
        addSaveButton();
        addCancelButton();
    }

    private void addEditButton() {
        final JButton editButton = new AbstractButton(
                Labels.getString("Common.Edit"));
        final JButton previewButton = new AbstractButton(
                Labels.getString("Common.Preview"));
        final JButton htmlButton = new AbstractButton(
                "HTML");
        final JButton boldButton = new AbstractButton(
                "B");
        boldButton.setFont(getFont().deriveFont(Font.BOLD));
        boldButton.setMargin(new Insets(0, 0, 0, 0));
        final JButton italicButton = new AbstractButton(
                "I");
        italicButton.setFont(getFont().deriveFont(Font.ITALIC));
        italicButton.setMargin(new Insets(0, 0, 0, 0));
        final JButton underlineButton = new AbstractButton(
                "U");
        Map attributes = getFont().getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        underlineButton.setFont(getFont().deriveFont(attributes));
        underlineButton.setMargin(new Insets(0, 0, 0, 0));
        final JButton backgroundColorButton = new AbstractButton(
                "ab");
        backgroundColorButton.setForeground(Color.BLUE);
        backgroundColorButton.setFont(getFont().deriveFont(attributes).deriveFont(Font.BOLD));
        backgroundColorButton.setMargin(new Insets(0, 0, 0, 0));
        final JButton foregroundColorButton = new AbstractButton(
                "A");
        foregroundColorButton.setForeground(Color.BLUE);
        foregroundColorButton.setFont(getFont().deriveFont(Font.BOLD));
        foregroundColorButton.setMargin(new Insets(0, 0, 0, 0));
        final JTextField linkTextField = new JTextField();
        final JButton linkButton = new AbstractButton(
                ">>");
        linkButton.setMargin(new Insets(0, 0, 0, 0));
        // Edit button
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1; // 10 %
        gbc.weighty = 0.5;
        gbc.gridwidth = 5;
        gbc.gridheight = 4;
        gbc.fill = GridBagConstraints.BOTH;
        editButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                informationArea.setEditable(true);
                editButton.setVisible(false);
                previewButton.setVisible(true);
                htmlButton.setVisible(true);
                boldButton.setVisible(true);
                italicButton.setVisible(true);
                underlineButton.setVisible(true);
                backgroundColorButton.setVisible(true);
                foregroundColorButton.setVisible(true);
                linkTextField.setVisible(true);
                linkButton.setVisible(true);
            }
        });
        add(editButton, gbc);
        // Preview button
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1; // 10 %
        gbc.weighty = 0.5;
        gbc.gridwidth = 5;
        gbc.gridheight = 1; // this is the only setting different with the edit button
        gbc.fill = GridBagConstraints.BOTH;
        previewButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // init html button in case it was left in HTML mode
                if (informationArea.getContentType().equals("text/plain")) {
                    String text = informationArea.getText();
                    informationArea.setContentType("text/html");
                    informationArea.setText(text);
                    htmlButton.setText("HTML");
                    informationArea.setCaretPosition(0);
                }
                informationArea.setEditable(false);
                editButton.setVisible(true);
                previewButton.setVisible(false);
                htmlButton.setVisible(false);
                boldButton.setVisible(false);
                italicButton.setVisible(false);
                underlineButton.setVisible(false);
                backgroundColorButton.setVisible(false);
                foregroundColorButton.setVisible(false);
                linkTextField.setVisible(false);
                linkButton.setVisible(false);
            }
        });
        previewButton.setVisible(false);
        add(previewButton, gbc);
        // html/plain button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.weighty = 0.3;
        gbc.gridwidth = 5;
        gbc.gridheight = 1;
        htmlButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (informationArea.getContentType().equals("text/html")) {
                    String text = informationArea.getText();
                    informationArea.setContentType("text/plain");
                    informationArea.setText(text);
                    htmlButton.setText(Labels.getString("Common.Text Plain"));
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
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        boldButton.addActionListener(new StyledEditorKit.BoldAction());
        boldButton.setVisible(false);
        add(boldButton, gbc);
        // italic button
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        italicButton.addActionListener(new StyledEditorKit.ItalicAction());
        italicButton.setVisible(false);
        add(italicButton, gbc);
        // underline button
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        underlineButton.addActionListener(new StyledEditorKit.UnderlineAction());
        underlineButton.setVisible(false);
        add(underlineButton, gbc);
        // background color button
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
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
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
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
        gbc.weightx = 0.08;
        gbc.weighty = 0.1;
        gbc.gridwidth = 4;
        linkTextField.setVisible(false);
        add(linkTextField, gbc);
        // add link button
        gbc.gridx = 4;
        gbc.gridy = 3;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        linkButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int start = informationArea.getSelectionStart();
                    int end = informationArea.getSelectionEnd();
                    if (start > end) { // Backwards selection
                        start = end;
                    }
                    if (!linkTextField.getText().isEmpty()) {
                        String link = "<a href=\"" + linkTextField.getText() + "\">" + linkTextField.getText() + "</a>";
                        informationArea.insertText(start, link);
                        linkTextField.setText(null);
                        // Set caret position right after the link                        
                        informationArea.setCaretPosition(start + linkTextField.getText().length());
                    }
                } catch (BadLocationException ignored) {
                } catch (IOException ignored) {
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
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        gbc.gridheight = 4;
        informationArea.setEditable(false);
        add(new JScrollPane(informationArea), gbc);
    }

    private void addSaveButton() {
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0.8;
        gbc.gridheight = 3;
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveComment(informationArea.getText());
            }
        });
        add(saveButton, gbc);
    }

    private void addCancelButton() {
        gbc.gridx = 6;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        gbc.weighty = 0.2;
        gbc.gridheight = 1;
        JButton cancelButton = new AbstractButton(
                Labels.getString("Common.Cancel"));
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showInfo(); // ???
            }
        });
        add(cancelButton, gbc);
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

        // Templates
        /*String path = "./" + (activity.getType().isEmpty() ? "" : (activity.getType() + ".")) + "template.html";
         System.err.println("path=" + path);
         // Check only ones for templates (optimization)
         FileInputStream file;
         try {
         file = new FileInputStream(path);
         int content;
         while ((content = file.read()) != -1) {
         text += (char) content;
         }
         file.close();
         } catch (IOException ex) {
         path = "./template.html";
         try {
         file = new FileInputStream(path);
         int content;
         while ((content = file.read()) != -1) {
         text += (char) content;
         }
         file.close();
         } catch (IOException ignored) {
         }
         }*/
        if (activity.getNotes().trim().length() == 0) {
            String text = "";
            if (activity.isStory()) {
                // default template for User Story type
                text += "<div><b><u>Story line</u></b></div><br />";
                text += "<p>As a {user role}, I want to {action} in order to {goal}.</p>";
                text += "<br />";
                text += "<div><b><u>User acceptance criteria</u></b></div>";
                text += "<ul>";
                text += "<li>...</li>";
                text += "<li>...</li>";
                text += "</ul>";
                text += "<br />";
                text += "<div><b><u>Test cases</u></b></div>";
                text += "<ul>";
                text += "<li>...</li>";
                text += "<li>...</li>";
                text += "</ul>";
            } else {
                text += "<p></p>";
            }
            textMap.put("comment", text);
        } else {
            // Backward compatility 3.0.X
            String comment = activity.getNotes();//.replaceAll("\n", "<br />");
            /*if (selectedActivity) {
             System.err.println("test");
             System.err.println(informationTmp);
             textMap.put("comment", informationTmp);
             } else {*/
            textMap.put("comment", comment);
        }

        /*if (activity.isFinished()) {
         informationArea.setForeground(ColorUtil.GREEN);
         } else {
         informationArea.setForeground(ColorUtil.BLACK);
         }*/
    }

    @Override
    public boolean isMultipleSelectionAllowed() {
        return false;
    }
}
