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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.apache.commons.lang3.StringEscapeUtils;
import org.mypomodoro.Main;

import org.mypomodoro.buttons.AbstractButton;
import org.mypomodoro.gui.IListPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.HtmlEditor;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays comment on the current Activity and allows editing it
 *
 */
// TODO fix size of button and tab panels
// TODO when nothing in memory, CTRL + V shortcut insert "Control V" in the text area
// TODO after control + z, error when getting forth and back to the editor
public class CommentPanel extends JPanel {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Main.class);

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
    protected final HtmlEditor informationArea = new HtmlEditor();
    protected String currentInformation = "";
    protected int currentPlainCaretPosition = 0;
    protected int currentHTMLCaretPosition = 0;
    private final JScrollPane scrollPaneInformationArea;
    private boolean showIconLabel = false;

    // Undo / Redo (plain mode only)
    private final UndoHandler undoHandler = new UndoHandler();
    private final UndoManager undoManager = new UndoManager();
    private final UndoAction undoAction = new UndoAction();
    private final RedoAction redoAction = new RedoAction();
    private int versionCounter = 0;

    public CommentPanel(IListPanel iListPanel) {
        this(iListPanel, false);
    }

    public CommentPanel(IListPanel iListPanel, boolean showIconLabel) {
        this.panel = iListPanel;
        this.showIconLabel = showIconLabel;

        // This will disable the wrapping of JTextPane
        // http://stackoverflow.com/questions/4702891/toggling-text-wrap-in-a-jtextpane/4705323#4705323        
        JPanel noWrapPanel = new JPanel(new BorderLayout());
        noWrapPanel.add(informationArea);
        scrollPaneInformationArea = new JScrollPane(noWrapPanel);

        // Add undo / redo handler
        informationArea.getDocument().addUndoableEditListener(undoHandler);

        setLayout(new GridBagLayout());
        setBorder(null);

        addEditorButtons();
        addCommentArea();
        addSaveButton();
        addCancelButton();

        // Display the buttons in plain mode when clicking or selecting with the mouse
        informationArea.addMouseListener(new MouseAdapter() {

            // click
            @Override
            public void mouseClicked(MouseEvent e) {
                activatePlainMode();
            }

            // selection and release
            @Override
            public void mouseReleased(MouseEvent e) {
                activatePlainMode();
            }

            private void activatePlainMode() {
                if (!informationArea.isEditable()) {
                    informationArea.enterKeyBindingForPlainMode();
                    informationArea.setEditable(true);
                    informationArea.getCaret().setVisible(true); // show carriet
                    informationArea.requestFocusInWindow();
                    htmlButton.setVisible(true);
                    previewButton.setVisible(true);
                    boldButton.setVisible(true);
                    italicButton.setVisible(true);
                    underlineButton.setVisible(true);
                    backgroundColorButton.setVisible(true);
                    foregroundColorButton.setVisible(true);
                    linkTextField.setVisible(true);
                    linkButton.setVisible(true);
                    int row = panel.getTable().getSelectedRow();
                    activityIdTmp = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());                                        
                } else {
                    if (informationArea.isPlainMode()) {
                        currentPlainCaretPosition = informationArea.getCaretPosition();
                        currentHTMLCaretPosition = 0;
                    } else {
                        currentPlainCaretPosition = 0;
                        currentHTMLCaretPosition = informationArea.getCaretPosition();
                    }
                }
            }

        });

        /**
         * Displays the save and cancel buttons
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
                // Including: key Control mask + V and Shift mask + Insert (paste)
                if (informationArea.isEditable()
                        && ((e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_V)
                        || (e.getModifiers() == KeyEvent.CTRL_MASK && e.getKeyCode() == KeyEvent.VK_N)
                        || (e.getModifiers() == KeyEvent.SHIFT_MASK && e.getKeyCode() == KeyEvent.VK_INSERT)
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
                    versionCounter++;
                }
            }

            /**
             * KeyReleased is only used to record the current text AFTER
             * modification
             *
             */
            @Override
            public void keyReleased(KeyEvent e) {                                
                currentInformation = informationArea.getText();
                if (informationArea.isPlainMode()) {
                    currentPlainCaretPosition = informationArea.getCaretPosition();
                    currentHTMLCaretPosition = 0;
                } else {
                    currentPlainCaretPosition = 0;
                    currentHTMLCaretPosition = informationArea.getCaretPosition();
                }
            }
        });

        // CTRL Z: Undo
        informationArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK), "Undo");
        informationArea.getActionMap().put("Undo", undoAction);
        
        // CTRL Y: Redo
        informationArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK), "Redo");
        informationArea.getActionMap().put("Redo", redoAction);

        // Override SHIFT + '>' and SHIFT + '<' to prevent conflicts with list SHIFT + '>' and SHIFT + '<' shortcuts  
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, InputEvent.SHIFT_MASK), "donothing");
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_COMMA, InputEvent.SHIFT_MASK), "donothing");
        class doNothing extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Do nothing
            }
        }
        informationArea.getActionMap().put("donothing", new doNothing());

        // CTRL + N: insert line break
        /*informationArea.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK), "Break");
        class breakAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int start = informationArea.getSelectionStart();
                    saveButton.setVisible(true);
                    cancelButton.setVisible(true);
                    if (informationArea.isPlainMode()) {
                        informationArea.insertText(start, "<br>", HTML.Tag.BR);
                        currentPlainCaretPosition = informationArea.getCaretPosition();
                        informationArea.setCaretPosition(currentPlainCaretPosition); // show caret at the beginning of the new line
                        currentHTMLCaretPosition = 0;
                    } else {
                        informationArea.getDocument().insertString(start, "<br>", null);
                        currentPlainCaretPosition = 0;
                        currentHTMLCaretPosition = informationArea.getCaretPosition();
                    }
                    informationArea.requestFocusInWindow();
                    currentInformation = informationArea.getText();
                } catch (BadLocationException ignored) {
                } catch (IOException ignored) {
                }
            }
        }
        informationArea.getActionMap().put("Break", new breakAction());*/
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
                if (informationArea.isPlainMode()) { // plain mode --> html mode;                    
                    informationArea.revokeEnterKeyBindingForPlainMode();
                    displayButtonsForHTMLMode();
                } else { // html mode --> plain mode
                    informationArea.enterKeyBindingForPlainMode();
                    displayButtonsForPlainMode();
                }
                informationArea.requestFocusInWindow();
                if (informationArea.isPlainMode()) {
                    informationArea.setCaretPosition(currentPlainCaretPosition);
                } else {
                    informationArea.setCaretPosition(currentHTMLCaretPosition);
                }
            }
        });
        htmlButton.setVisible(false);
        add(htmlButton, gbc);
        // Formatting actions
        class boldAction extends StyledEditorKit.BoldAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                String selectedText = informationArea.getSelectedText();
                if (selectedText != null && selectedText.length() > 0) {
                    setPlainEnvForActionButton();
                }
                // show caret
                informationArea.requestFocusInWindow();
            }
        }
        class italicAction extends StyledEditorKit.ItalicAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                String selectedText = informationArea.getSelectedText();
                if (selectedText != null && selectedText.length() > 0) {
                    setPlainEnvForActionButton();
                }
                // show caret
                informationArea.requestFocusInWindow();
            }
        }
        class underlineAction extends StyledEditorKit.UnderlineAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                super.actionPerformed(e);
                String selectedText = informationArea.getSelectedText();
                if (selectedText != null && selectedText.length() > 0) {
                    setPlainEnvForActionButton();
                }
                // show caret
                informationArea.requestFocusInWindow();
            }
        }
        // bold button
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        boldButton.addActionListener(new boldAction());
        // set the keystroke on the button (so won't work in preview mode)
        // CTRL B: Bold
        boldButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_MASK), "Bold");
        boldButton.getActionMap().put("Bold", new boldAction());
        boldButton.setToolTipText("CTRL + B");
        boldButton.setVisible(false);
        add(boldButton, gbc);
        // italic button
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        italicButton.addActionListener(new italicAction());
        // set the keystroke on the button (so won't work in preview mode; however this will conflict with CTRL+I keystroke in ToDoPanel)
        // CTRL I: Italic
        italicButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK), "Italic");
        italicButton.getActionMap().put("Italic", new italicAction());
        italicButton.setToolTipText("CTRL + I");
        italicButton.setVisible(false);
        add(italicButton, gbc);
        // underline button
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        underlineButton.addActionListener(new underlineAction());
        // set the keystroke on the button (so won't work in preview mode)
        // CTRL U: Underline
        underlineButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK), "Underline");
        underlineButton.getActionMap().put("Underline", new underlineAction());
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
                    String selectedText = informationArea.getSelectedText();
                    if (selectedText != null && selectedText.length() > 0) {
                        int start = informationArea.getSelectionStart();
                        informationArea.getStyledDocument().setCharacterAttributes(start, selectedText.length(), COLOR, false);
                        setPlainEnvForActionButton();
                    }
                    // show caret
                    informationArea.requestFocusInWindow();
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
                    String selectedText = informationArea.getSelectedText();
                    if (selectedText != null && selectedText.length() > 0) {
                        int start = informationArea.getSelectionStart();
                        informationArea.getStyledDocument().setCharacterAttributes(start, selectedText.length(), COLOR, false);
                        setPlainEnvForActionButton();
                    }
                    // show caret
                    informationArea.requestFocusInWindow();
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
        // Clean text field when clicking
        linkTextField.addMouseListener(new MouseAdapter() {

            // click
            @Override
            public void mouseClicked(MouseEvent e) {
                if (linkTextField.getText().equals("http://")) {
                    linkTextField.setText("");
                }
            }
        });
        linkTextField.setText("http://");
        linkTextField.setVisible(false);
        add(linkTextField, gbc);
        // add link button
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        // ENTER: insert link (requires focus on the link text field)
        linkButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Create link");
        class linkEnterAction extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int start = informationArea.getSelectionStart();
                    if (!linkTextField.getText().isEmpty()) {
                        int row = panel.getTable().getSelectedRow();
                        activityIdTmp = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                        saveButton.setVisible(true);
                        cancelButton.setVisible(true);
                        String href = linkTextField.getText().startsWith("www") ? ("http://" + linkTextField.getText()) : linkTextField.getText();
                        String link = "<a href=\"" + href + "\">" + linkTextField.getText() + "</a>";                        
                        informationArea.insertText(start, link, HTML.Tag.A);
                        // Show caret
                        informationArea.requestFocusInWindow();
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
        linkButton.getActionMap().put("Create link", new linkEnterAction());
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
                // show caret
                informationArea.requestFocusInWindow();
            }
        };
        saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK), "Save");
        saveButton.getActionMap().put("Save", saveAction);
        saveButton.setToolTipText("CTRL + S");
        saveButton.setVisible(false);
        saveButton.addActionListener(saveAction);
        // Set the width of the button to make it shorter
        Dimension dimension = saveButton.getSize();
        dimension.width = 50;
        saveButton.setMinimumSize(dimension);
        saveButton.setPreferredSize(dimension);
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
        //if (activityIdTmp == activity.getId() && versionCounter > 0) {
        if (activityIdTmp == activity.getId()) {
            comment = currentInformation;
            //saveButton.setVisible(true);
            //cancelButton.setVisible(true);
        } else {
            saveButton.setVisible(false);
            cancelButton.setVisible(false);
            // make sure CTRL + Z of previous selected activity doesn't work on new selected activity
            int row = panel.getTable().getSelectedRow();
            int selectedActivityId = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
            /*if (selectedActivityId != activityIdTmp) {
                versionCounter = 0;
            }*/
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
                comment = comment.replaceAll(System.getProperty("line.separator"), "</p><p style=\"margin-top: 0\">");
                comment = comment + "</p>";
            }
        }
        informationArea.setText(comment);
        if (activityIdTmp == activity.getId()) {
            if (informationArea.isPlainMode()) {
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
     *
     */
    class Version {

        String text = "";
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

    private void setPlainEnvForActionButton() {
        int row = panel.getTable().getSelectedRow();
        activityIdTmp = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        currentInformation = informationArea.getText();
    }

    class UndoHandler implements UndoableEditListener {

        /**
         * Messaged when the Document has created an edit, the edit is added to
         * <code>undoManager</code>, an instance of UndoManager.
         *
         * @param e
         */
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            undoManager.addEdit(e.getEdit());
            undoAction.update();
            redoAction.update();
        }
    }

    class UndoAction extends AbstractAction {

        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                undoManager.undo();
            } catch (CannotUndoException ex) {
                logger.error(ex.toString());
            }
            update();
            redoAction.update();
        }

        protected void update() {
            if (undoManager.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undoManager.getUndoPresentationName());
                currentInformation = informationArea.getText();
                currentPlainCaretPosition = informationArea.getCaretPosition();
                currentHTMLCaretPosition = 0;
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
            }
        }
    }

    class RedoAction extends AbstractAction {

        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                undoManager.redo();                
            } catch (CannotRedoException ex) {
                logger.error(ex.toString());
            }
            update();
            undoAction.update();
        }

        protected void update() {
            if (undoManager.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undoManager.getRedoPresentationName());
                saveButton.setVisible(true);
                cancelButton.setVisible(true);
                currentInformation = informationArea.getText();
                currentPlainCaretPosition = informationArea.getCaretPosition();
                currentHTMLCaretPosition = 0;
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }
    }
}
