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
import java.awt.event.InputEvent;
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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import org.apache.commons.lang3.StringEscapeUtils;

import org.mypomodoro.buttons.AbstractButton;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.HtmlEditor;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays comment on the current Activity and allows editing it
 *
 */
public class CommentPanel extends JPanel {

    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final IListPanel panel;
    private int activityIdTmp = -1;
    private final JButton saveButton = new AbstractButton(Labels.getString("Common.Save"));
    private final JButton cancelButton = new AbstractButton(Labels.getString("Common.Cancel"));
    private final JButton previewButton = new AbstractButton(Labels.getString("Common.Preview"));
    private final JButton htmlButton = new AbstractButton("HTML 3.2");
    private final JButton boldButton = new AbstractButton("B");
    private final JButton italicButton = new AbstractButton("I");
    private final JButton underlineButton = new AbstractButton("U");
    private final JButton backgroundColorButton = new AbstractButton("ab");
    private final JButton foregroundColorButton = new AbstractButton("A");
    private final JTextField linkTextField = new JTextField();
    private final JButton linkButton = new AbstractButton(">>");
    private final ArrayList<Version> versions = new ArrayList<Version>();
    protected final HtmlEditor informationArea = new HtmlEditor();
    protected String currentInformation = new String();
    protected int currentPlainCaretPosition = 0;
    protected int currentHTMLCaretPosition = 0;
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

        addEditorButtons();
        addCommentArea();
        addSaveButton();
        addCancelButton();

        // Display the edit buttons when clicking with the mouse
        informationArea.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!informationArea.isEditable()) {
                    informationArea.setEditable(true);
                    informationArea.getCaret().setVisible(true); // show cursor
                    informationArea.requestFocusInWindow();
                    informationArea.setCaretPosition(currentPlainCaretPosition);
                    htmlButton.setVisible(true);
                    previewButton.setVisible(true);
                    boldButton.setVisible(true);
                    italicButton.setVisible(true);
                    underlineButton.setVisible(true);
                    backgroundColorButton.setVisible(true);
                    foregroundColorButton.setVisible(true);
                    linkTextField.setVisible(true);
                    linkButton.setVisible(true);
                }
            }
        });

        /**
         * Displays the save and cancel buttons Records the versions
         */
        informationArea.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // Nothing to do here
            }

            /**
             * KeyPressed makes the necessary checks and record the text BEFORE
             * modification
             *
             */
            @Override
            public void keyPressed(KeyEvent e) {
                // The area must be editable 
                // Excluding: key Control mask, arrows, home/end and page up/down
                // Including: key Control mask + V (paste)
                if (informationArea.isEditable()
                        && ((e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_V)
                        || e.getModifiers() != KeyEvent.CTRL_MASK)
                        && e.getKeyCode() != KeyEvent.VK_UP
                        && e.getKeyCode() != KeyEvent.VK_KP_UP
                        && e.getKeyCode() != KeyEvent.VK_DOWN
                        && e.getKeyCode() != KeyEvent.VK_KP_DOWN
                        && e.getKeyCode() != KeyEvent.VK_LEFT
                        && e.getKeyCode() != KeyEvent.VK_KP_LEFT
                        && e.getKeyCode() != KeyEvent.VK_RIGHT
                        && e.getKeyCode() != KeyEvent.VK_KP_RIGHT
                        && e.getKeyCode() != KeyEvent.VK_HOME
                        && e.getKeyCode() != KeyEvent.VK_END
                        && e.getKeyCode() != KeyEvent.VK_PAGE_UP
                        && e.getKeyCode() != KeyEvent.VK_PAGE_DOWN) {
                    int row = panel.getTable().getSelectedRow();
                    activityIdTmp = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                    saveButton.setVisible(true);
                    cancelButton.setVisible(true);
                    versions.add(new Version(informationArea.getText(), informationArea.getCaretPosition(), informationArea.getContentType()));
                }
            }

            /**
             * KeyReleased is only used to record the current text (text AFTER
             * modification)
             *
             */
            @Override
            public void keyReleased(KeyEvent e) {
                currentInformation = informationArea.getText();
                if (informationArea.getContentType().equals("text/html")) {
                    currentPlainCaretPosition = informationArea.getCaretPosition();
                    currentHTMLCaretPosition = 0;
                } else {
                    currentPlainCaretPosition = 0;
                    currentHTMLCaretPosition = informationArea.getCaretPosition();
                }
            }
        });

        // set the keystroke
        // CTRL Z: Undo
        informationArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK), "Undo");
        Action undoAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!versions.isEmpty()) {
                    if (versions.get(versions.size() - 1).getContentType().equals("text/html")) {
                        displayButtonsForPlainMode();
                    } else {
                        displayButtonsForHTMLMode();
                    }
                    informationArea.requestFocusInWindow();
                    informationArea.setText(versions.get(versions.size() - 1).getText());
                    informationArea.setCaretPosition(versions.get(versions.size() - 1).getCaretPosition());
                    versions.remove(versions.size() - 1);
                }
                if (versions.isEmpty()) {
                    saveButton.setVisible(false);
                    cancelButton.setVisible(false);
                }
            }
        };
        informationArea.getActionMap().put("Undo", undoAction);

        // Override SHIFT + '>' and SHIFT + '<' to prevent conflicts with list shortcuts  
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, InputEvent.SHIFT_MASK), "donothing");
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.SHIFT_MASK), "donothing");
        class doNothing extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Do nothing
            }
        }
        informationArea.getActionMap().put("donothing", new doNothing());
    }

    private void addEditorButtons() {

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
        // Preview button
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1; // 10 %
        gbc.weighty = 0.5;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.BOTH;
        previewButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                displayButtonsForPreviewMode();
                informationArea.requestFocusInWindow();
                informationArea.setCaretPosition(0);
            }
        });
        previewButton.setVisible(false);
        add(previewButton, gbc);
        // html/plain button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.1;
        gbc.weighty = 0.3;
        gbc.gridwidth = 5;
        htmlButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (informationArea.getContentType().equals("text/html")) {
                    displayButtonsForHTMLMode();
                } else {
                    displayButtonsForPlainMode();
                }
                informationArea.requestFocusInWindow();
                if (informationArea.getContentType().equals("text/html")) {
                    informationArea.setCaretPosition(currentPlainCaretPosition);
                } else {
                    informationArea.setCaretPosition(currentHTMLCaretPosition);
                }
            }
        });
        htmlButton.setVisible(false);
        add(htmlButton, gbc);                   
        // bold button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        boldButton.addActionListener(new StyledEditorKit.BoldAction());
        // set the keystroke on the button (so won't work in preview mode)
        // CTRL B: Bold
        boldButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK), "Bold");
        boldButton.getActionMap().put("Bold", new StyledEditorKit.BoldAction());
        boldButton.setToolTipText("CTRL + B");
        boldButton.setVisible(false);
        add(boldButton, gbc);
        // italic button
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        italicButton.addActionListener(new StyledEditorKit.ItalicAction());
        // set the keystroke on the button (so won't work in preview mode although this will conflict with CTRL+I keystroke in ToDoPanel)
        // CTRL I: Italic
        italicButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK), "Italic");
        italicButton.getActionMap().put("Italic", new StyledEditorKit.ItalicAction());
        italicButton.setToolTipText("CTRL + I");
        italicButton.setVisible(false);
        add(italicButton, gbc);
        // underline button
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        underlineButton.addActionListener(new StyledEditorKit.UnderlineAction());
        // set the keystroke on the button (so won't work in preview mode)
        // CTRL U: Underline
        underlineButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK), "Underline");
        underlineButton.getActionMap().put("Underline", new StyledEditorKit.UnderlineAction());
        underlineButton.setToolTipText("CTRL + U");
        underlineButton.setVisible(false);
        add(underlineButton, gbc);
        // background color button
        gbc.gridx = 3;
        gbc.gridy = 1;
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
                        if (start > end) { // Backwards selection (left to right writting)
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
        gbc.gridy = 1;
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
                        if (start > end) { // Backwards selection (left to right writting)
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
        gbc.gridy = 2;
        gbc.weightx = 0.08;
        gbc.weighty = 0.1;
        gbc.gridwidth = 4;
        linkTextField.setVisible(false);
        add(linkTextField, gbc);
        // add link button
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        // ENTER: insert link (requires focus on the link text field)
        linkButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enterlink");
        class linkEnterAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int start = informationArea.getSelectionStart();
                    int end = informationArea.getSelectionEnd();
                    if (start > end) { // Backwards selection (left to right writting)
                        start = end;
                    }
                    if (!linkTextField.getText().isEmpty()) {
                        int row = panel.getTable().getSelectedRow();
                        activityIdTmp = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                        versions.add(new Version(informationArea.getText(), informationArea.getCaretPosition(), informationArea.getContentType()));
                        saveButton.setVisible(true);
                        cancelButton.setVisible(true);
                        String link = "<a href=\"" + (linkTextField.getText().startsWith("www") ? ("http://" + linkTextField.getText()) : linkTextField.getText()) + "\">" + linkTextField.getText() + "</a>";
                        informationArea.insertText(start, link, HTML.Tag.A);
                        // Set caret position right after the link
                        informationArea.requestFocusInWindow();
                        informationArea.setCaretPosition(start + linkTextField.getText().length());
                        currentInformation = informationArea.getText();
                        currentPlainCaretPosition = informationArea.getCaretPosition();
                        currentHTMLCaretPosition = 0;
                        linkTextField.setText("http://"); // reset field                        
                    }
                } catch (BadLocationException ignored) {
                } catch (IOException ignored) {
                }
            }
        }
        linkButton.getActionMap().put("Enterlink", new linkEnterAction());
        linkButton.setToolTipText("ENTER");
        linkButton.addActionListener(new linkEnterAction());
        linkButton.setVisible(false);
        add(linkButton, gbc);
    }

    private void addCommentArea() {
        JPanel commentArea = new JPanel();
        commentArea.setLayout(new GridBagLayout());
        GridBagConstraints commentgbc = new GridBagConstraints();
        if (showIconLabel) { // icon label panel (ToDo list / Iteration panel)
            commentgbc.gridx = 0;
            commentgbc.gridy = 0;
            commentgbc.fill = GridBagConstraints.BOTH;
            commentgbc.weightx = 1.0;
            commentgbc.weighty = 0.1;
            iconLabel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
            commentArea.add(iconLabel, commentgbc);
        }
        // add the comment area
        commentgbc.gridx = 0;
        commentgbc.gridy = showIconLabel ? 1 : 0;
        commentgbc.fill = GridBagConstraints.BOTH;
        commentgbc.weightx = 1.0;
        commentgbc.weighty = 1.0;
        informationArea.setEditable(false);
        commentArea.add(scrollPaneInformationArea, commentgbc);
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        gbc.gridheight = 4;
        add(commentArea, gbc);
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
                panel.saveComment(StringEscapeUtils.unescapeHtml4(informationArea.getText()));
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
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
                activityIdTmp = -1;
                versions.clear();
                int row = panel.getTable().getSelectedRow();
                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                showInfo(panel.getActivityById(id));
            }
        });
        add(cancelButton, gbc);
    }

    public void showInfo(final Activity activity) {
        clearInfo();
        String comment = activity.getNotes().trim();
        if (activityIdTmp == activity.getId() && !versions.isEmpty()) {
            comment = currentInformation;
            saveButton.setVisible(true);
            cancelButton.setVisible(true);
        } else {
            saveButton.setVisible(false);
            cancelButton.setVisible(false);
            // make sure CTRL + Z of previous selected activity doesn't work on new selected activity
            int row = panel.getTable().getSelectedRow();
            int selectedActivityId = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
            if (selectedActivityId != activityIdTmp) {
                versions.clear();
            }
        }
        if (comment.isEmpty()) { // no comment yet
            if (activity.isStory()) {
                // default template for User Story type
                // use of <ul><li>...</li></ul> is not an option : ugly, hard to use and somehow appear on others tasks' empty comment
                comment += "<p style=\"margin-top: 0\"><b>Story line</b></p>";
                comment += "<p style=\"margin-top: 0\">As a {user role}, I want to {action} in order to {goal}.</p>";
                comment += "<p><b>User acceptance criteria</b></p>";
                comment += "<p style=\"margin-top: 0\">";
                comment += "+ ...";
                comment += "</p>";
                comment += "<p style=\"margin-top: 0\">";
                comment += "+ ...";
                comment += "</p>";
                comment += "<p><b>Test cases</b></p>";
                comment += "<p style=\"margin-top: 0\">";
                comment += "+ ...";
                comment += "</p>";
                comment += "<p style=\"margin-top: 0\">";
                comment += "+ ...";
                comment += "</p>";
            }
        } else {
            // Backward compatility 3.0.X and imports
            // Check if there is a body tag; if not replace trailing return carriage with P tag
            if (!comment.contains("</body>")) {
                comment = "<p style=\"margin-top: 0\">" + comment;
                comment = comment.replaceAll("\r\n", "</p><p style=\"margin-top: 0\">"); // \r\n also replaced in caseit appears in the import file
                comment = comment.replaceAll("\n", "</p><p style=\"margin-top: 0\">");
                comment = comment + "</p>";
            }
        }
        informationArea.setText(comment);
        if (activityIdTmp == activity.getId()) {
            if (informationArea.getContentType().equals("text/html")) {
                informationArea.setCaretPosition(currentPlainCaretPosition);
            } else {
                informationArea.setCaretPosition(currentHTMLCaretPosition);
            }
        } else {
            // disable auto scrolling
            informationArea.setCaretPosition(0);
        }
    }

    public void clearInfo() {
        informationArea.setText("");
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }

    private void displayButtonsForHTMLMode() {
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
    }

    private void displayButtonsForPlainMode() {
        String text = informationArea.getText();
        informationArea.setContentType("text/html");
        informationArea.setText(text);
        htmlButton.setText("HTML 3.2");
        boldButton.setVisible(true);
        italicButton.setVisible(true);
        underlineButton.setVisible(true);
        backgroundColorButton.setVisible(true);
        foregroundColorButton.setVisible(true);
        linkTextField.setVisible(true);
        linkButton.setVisible(true);
    }

    private void displayButtonsForPreviewMode() {
        String text = informationArea.getText();
        informationArea.setContentType("text/html");
        informationArea.setText(text);
        htmlButton.setText("HTML 3.2"); // make sure the plain/html button read "HTML..."
        informationArea.setEditable(false);
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

    /**
     * Version
     */
    class Version {

        String text = new String();
        int caretPosition = 0;
        String contentType = "text/plain";

        public Version(String text, int caretPosition, String contentType) {
            this.text = text;
            this.caretPosition = caretPosition;
            this.contentType = contentType;
        }

        public String getText() {
            return text;
        }

        public int getCaretPosition() {
            return caretPosition;
        }

        public String getContentType() {
            return contentType;
        }
    }
}
