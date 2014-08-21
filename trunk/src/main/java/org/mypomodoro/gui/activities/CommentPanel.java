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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;

import org.mypomodoro.buttons.AbstractButton;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays comment on the current Activity and allows editing it
 *
 */
// TODO re-activate addToDoIconPanel() for ToDoPanel only + make only one jpanel for textarea and icon pnel
public class CommentPanel extends ActivityInformationPanel {

    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final IListPanel panel;
    private String informationTmp = new String();
    private int activityIdTmp = -1;
    private final JButton saveButton = new AbstractButton(Labels.getString("Common.Save"));
    private final JButton cancelButton = new AbstractButton(Labels.getString("Common.Cancel"));
    private final JButton previewButton = new AbstractButton(Labels.getString("Common.Preview"));
    private final JButton editButton = new AbstractButton(Labels.getString("Common.Edit"));
    private final JButton htmlButton = new AbstractButton("HTML");
    private final JButton boldButton = new AbstractButton("B");
    private final JButton italicButton = new AbstractButton("I");
    private final JButton underlineButton = new AbstractButton("U");
    private final JButton backgroundColorButton = new AbstractButton("ab");
    private final JButton foregroundColorButton = new AbstractButton("A");
    private final JTextField linkTextField = new JTextField();
    private final JButton linkButton = new AbstractButton(">>");
    private final ArrayList<String> versions = new ArrayList<String>();
    private final JScrollPane scrollPaneInformationArea = new JScrollPane(informationArea);
    
    private boolean showIconLabel = false;
    
    public CommentPanel(IListPanel iListPanel) {
        this(iListPanel, false);
    }

    public CommentPanel(IListPanel iListPanel, boolean showIconLabel) {
        this.panel = iListPanel;
        this.showIconLabel = showIconLabel;

        setLayout(new GridBagLayout());
        setBorder(null);

        addEditButton();
        if (showIconLabel) {
            addToDoIconPanel();
        }    
        addCommentArea();
        addSaveButton();
        addCancelButton();
        
        // Display the edit buttons when clicking with the mouse
        informationArea.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                informationArea.setEditable(true);
                informationArea.getCaret().setVisible(true); // show cursor
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

        // Display the save and cancel buttons when typing
        informationArea.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {                
                // The area must be editable and the typing must not be a key Control mask
                if (informationArea.isEditable() 
                        && e.getModifiers() != KeyEvent.CTRL_MASK) {                    
                    informationTmp = informationArea.getText(); // record temp text
                    versions.add(informationArea.getText()); // record version                    
                }
            }

            // Key pressed method is the only suitable method to test CTRL mask key event
            @Override            
            public void keyPressed(KeyEvent e) {
                // The area must be editable and the typing must not be a key Control mask
                if (informationArea.isEditable() 
                        && e.getModifiers() != KeyEvent.CTRL_MASK) {
                    saveButton.setVisible(true);
                    cancelButton.setVisible(true);                    
                    int row = panel.getTable().getSelectedRow(); // record activity Id
                    activityIdTmp = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                }                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Do nothing here
            }
        });
        
        // set the keystroke
        // CTRL Z: Undo
        Action undoAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (versions.size() > 0) {
                    String versionText = versions.get(versions.size() - 1);
                    informationArea.setText(versionText);
                    informationTmp = versionText;
                    versions.remove(versions.size() - 1);
                    if (versions.isEmpty()) {
                        saveButton.setVisible(false);
                        cancelButton.setVisible(false);
                    }
                }                
            }
        };
        informationArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK), "Undo");
        informationArea.getActionMap().put("Undo", undoAction);        
    }

    private void addEditButton() {
        
        boldButton.setFont(getFont().deriveFont(Font.BOLD));
        boldButton.setMargin(new Insets(0, 0, 0, 0));
        
        italicButton.setFont(getFont().deriveFont(Font.ITALIC));
        italicButton.setMargin(new Insets(0, 0, 0, 0));
        
        Map attributes = getFont().getAttributes();
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        underlineButton.setFont(getFont().deriveFont(attributes));
        underlineButton.setMargin(new Insets(0, 0, 0, 0));
        
        backgroundColorButton.setForeground(Color.BLUE);
        backgroundColorButton.setFont(getFont().deriveFont(attributes).deriveFont(Font.BOLD));
        backgroundColorButton.setMargin(new Insets(0, 0, 0, 0));
        
        foregroundColorButton.setForeground(Color.BLUE);
        foregroundColorButton.setFont(getFont().deriveFont(Font.BOLD));
        foregroundColorButton.setMargin(new Insets(0, 0, 0, 0));
        linkTextField.setText("http://");        
        linkButton.setMargin(new Insets(0, 0, 0, 0));
        // Edit button
        /*gbc.gridx = 0;
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
         add(editButton, gbc);*/
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
                    //informationArea.setCaretPosition(0); // only for textArea
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
        // set the keystroke on the button (won't work on the text pane)
        // CTRL B: Bold
        boldButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK), "Bold");
        boldButton.getActionMap().put("Bold", new StyledEditorKit.BoldAction());
        boldButton.setToolTipText("CTRL + B");
        boldButton.setVisible(false);
        add(boldButton, gbc);
        // italic button
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        italicButton.addActionListener(new StyledEditorKit.ItalicAction());
        // set the keystroke on the button (won't work on the text pane)
        // CTRL I: Italic
        italicButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK), "Italic");
        italicButton.getActionMap().put("Italic", new StyledEditorKit.ItalicAction());
        italicButton.setToolTipText("CTRL + I");
        italicButton.setVisible(false);
        add(italicButton, gbc);
        // underline button
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        underlineButton.addActionListener(new StyledEditorKit.UnderlineAction());
        // set the keystroke on the button (won't work on the text pane)
        // CTRL U: Underline
        underlineButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK), "Underline");
        underlineButton.getActionMap().put("Underline", new StyledEditorKit.UnderlineAction());
        underlineButton.setToolTipText("CTRL + U");
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
                        String link = "<a href=\"" + (linkTextField.getText().startsWith("www") ? ("http://" + linkTextField.getText()) : linkTextField.getText()) + "\">" + linkTextField.getText() + "</a>";
                        informationArea.insertText(start, link, HTML.Tag.A);
                        // Set caret position right after the link                        
                        //informationArea.setCaretPosition(start + link.length());
                        //informationArea.setCaretPosition(informationArea.getCaretPosition());                        
                        linkTextField.setText("http://"); // reset field                        
                    }
                } catch (BadLocationException ignored) {
                } catch (IOException ignored) {
                }
            }
        });
        linkButton.setVisible(false);
        add(linkButton, gbc);
    }
    
    private void addToDoIconPanel() {
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;        
        gbc.insets = new Insets(0, 3, 0, 0); // margin left
        add(iconLabel, gbc);
        gbc.insets = new Insets(0, 0, 0, 0); // reset insets
    }

    private void addCommentArea() {
        // add the comment area
        gbc.gridx = 5;        
        gbc.gridy = showIconLabel ? 1 : 0;
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        gbc.gridheight = showIconLabel ? 3 : 4;
        informationArea.setEditable(false);
        add(scrollPaneInformationArea, gbc);
    }

    private void addSaveButton() {
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 0.8;
        gbc.gridheight = 3;
        // set the keystrokes on the button (won't work on the text pane)
        // CTRL S: Save
        Action saveAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveComment(informationArea.getText());
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
                informationTmp = new String();
                activityIdTmp = -1;
                versions.clear();
            }
        };
        saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK), "Save");
        saveButton.getActionMap().put("Save", saveAction);
        saveButton.setToolTipText("CTRL + S");         
        saveButton.setVisible(false);
        saveButton.addActionListener(saveAction);
        add(saveButton, gbc);
    }

    private void addCancelButton() {
        gbc.gridx = 6;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        gbc.weighty = 0.2;
        gbc.gridheight = 1;
        cancelButton.setVisible(false);
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                previewButton.getActionListeners()[0].actionPerformed(e);
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
                informationTmp = new String();
                activityIdTmp = -1;
                versions.clear();
                int row = panel.getTable().getSelectedRow();
                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                selectInfo(panel.getActivityById(id));
                showInfo();
            }
        });
        add(cancelButton, gbc);
    }

    @Override
    public void selectInfo(final Activity activity) {
        String comment = activity.getNotes().trim();
        if (activityIdTmp == activity.getId() && !informationTmp.isEmpty()) {
            comment = informationTmp;            
            saveButton.setVisible(true);
            cancelButton.setVisible(true);
        } else {
            saveButton.setVisible(false);
            cancelButton.setVisible(false);
            // make sure CTRL + z of previous selected activity doesn't work on new selected activity
            int row = panel.getTable().getSelectedRow();
            int selectedActivityId = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
            if (selectedActivityId != activityIdTmp) {
                versions.clear();
            }
        }
        if (comment.isEmpty()) { // no comment yet
            String text = "";
            if (activity.isStory()) {
                // default template for User Story type
                // use of <ul><li>...</li></ul> is not an option : ugly, hard to use and somehow appear on others tasks' empty comment
                text += "<p style=\"margin-top: 0\"><b><u>Story line</u></b></p>";
                text += "<p style=\"margin-top: 0\">As a {user role}, I want to {action} in order to {goal}.</p>";
                text += "<p><b><u>User acceptance criteria</u></b></p>";
                text += "<p style=\"margin-top: 0\">";
                text += "+ ...";
                text += "</p>";
                text += "<p style=\"margin-top: 0\">";
                text += "+ ...";
                text += "</p>";
                text += "<p><b><u>Test cases</u></b></p>";
                text += "<p style=\"margin-top: 0\">";
                text += "+ ...";
                text += "</p>";
                text += "<p style=\"margin-top: 0\">";
                text += "+ ...";
                text += "</p>";
            }
            textMap.put("comment", text);
        } else {
            // Backward compatility 3.0.X
            // Check if there is a body tag; if not replace trailing return carriage with P tag
            if (!comment.contains("</body>")) {
                comment = "<p style=\"margin-top: 0\">" + comment;
                comment = comment.replaceAll("\n", "</p><p style=\"margin-top: 0\">");
                comment = comment + "</p>";                
            }
            textMap.put("comment", comment);
        }
    }

    @Override
    public boolean isMultipleSelectionAllowed() {
        return false;
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }
}
