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
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
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
// TODO fix size of button and tab panels
// TODO fix exception Exception in thread "AWT-EventQueue-0" java.lang.IllegalArgumentException: offsetLimit must be after current position
public class CommentPanel extends JPanel {

    //private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Main.class);
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final IListPanel panel;
    private int activityIdTmp = -1;
    private final JButton saveButton = new AbstractButton(Labels.getString("Common.Save"));
    private final JButton cancelButton = new AbstractButton(Labels.getString("Common.Cancel"));
    private final JButton previewButton = new AbstractButton(Labels.getString("Common.Preview"));
    private final String html32 = "HTML 3.2";
    private final JButton htmlButton = new AbstractButton(html32);
    private final JButton boldButton = new AbstractButton("B");
    private final JButton italicButton = new AbstractButton("I");
    private final JButton underlineButton = new AbstractButton("U");
    private final JButton backgroundColorButton = new AbstractButton("ab");
    private final JButton foregroundColorButton = new AbstractButton("A");
    private final JTextField linkTextField = new JTextField();
    private final JButton linkButton = new AbstractButton(">>");
    protected final HtmlEditor informationArea = new HtmlEditor();
    private final JScrollPane scrollPaneInformationArea;
    private boolean showIconLabel = false;

    // Record
    protected String currentPlainInformation = "";
    protected int currentPlainCaretPosition = 0;

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
                if (!informationArea.isEditable()) {
                    activatePlainMode();
                }
            }

            private void activatePlainMode() {
                informationArea.setEditable(true);
                // show caret
                informationArea.getCaret().setVisible(true);
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
                // Excluding: key Control and shift, arrows, home/end and page up/down 
                if (informationArea.isEditable()
                        && e.getKeyCode() != KeyEvent.VK_CONTROL
                        && e.getKeyCode() != KeyEvent.VK_SHIFT
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

                    // Add item to list when pressing ENTER within a list
                    if (e.getKeyCode() == KeyEvent.VK_ENTER
                            && isParentElement(HTML.Tag.LI)) {
                        Element element = getCurrentParentElement();
                        try {
                            e.consume(); // the event must be 'consumed' before inserting!
                            String item = "<li></li>";                            
                            ((HTMLDocument) informationArea.getDocument()).insertAfterEnd(element, item);
                            informationArea.setCaretPosition(element.getEndOffset());
                        } catch (BadLocationException ignored) {
                        } catch (IOException eignored) {
                        }
                    }                    
                    
                    /*if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                            && isParentElement(HTML.Tag.LI)) {
                        try {
                            if (getCurrentParentElement().getParentElement().getElementCount() == 1) {                                
                                getCurrentParentElement().getDocument().remove(getElement().getStartOffset(), getElement().getEndOffset());
                                System.err.println("ok");
                            }
                        } catch (BadLocationException ex) {                            
                        }                                               
                    }*/ 
                    
                    int row = panel.getTable().getSelectedRow();
                    activityIdTmp = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                    displaySaveCancelButton();
                }
            }

            /**
             * KeyReleased is only used to record the current plain text and
             * position (text AFTER modification)
             *
             */
            @Override
            public void keyReleased(KeyEvent e) {
                if (informationArea.isPlainMode()) {
                    currentPlainInformation = informationArea.getText();
                    currentPlainCaretPosition = informationArea.getCaretPosition();
                } else {
                    currentPlainCaretPosition = 0;
                }
            }
            
            private boolean isParentElement(HTML.Tag tag) {
                boolean isParentElement = false;
                Element e = getCurrentParentElement();
                if (e.getName().equalsIgnoreCase(tag.toString())) {
                    isParentElement = true;
                }
                return isParentElement;
            }
            
            private Element getElement() {
                return ((HTMLDocument) informationArea.getDocument()).getParagraphElement(informationArea.getCaretPosition());
            }
            
            private Element getCurrentParentElement() {
                return ((HTMLDocument) informationArea.getDocument()).getParagraphElement(informationArea.getCaretPosition()).getParentElement();
            }
        });

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

        // Override Control + V and SHIFT + INSERT shortcut to get rid of all formatting       
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.SHIFT_MASK), "Shift Insert");
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "Control V");
        class paste extends AbstractAction {

            @Override
            public void actionPerformed(ActionEvent e) {
                String clipboardText = informationArea.getClipboard();
                if (!clipboardText.isEmpty()) {
                    int start = informationArea.getSelectionStart();
                    int end = informationArea.getSelectionEnd();
                    try {
                        if (start != end) {
                            informationArea.getDocument().remove(start, end - start);
                        }
                        informationArea.getDocument().insertString(start, clipboardText, null);
                        // Show caret
                        informationArea.requestFocusInWindow();
                    } catch (BadLocationException ignored) {
                    }
                }
            }
        }
        informationArea.getActionMap().put("Shift Insert", new paste());
        informationArea.getActionMap().put("Control V", new paste());

        // Ordered and unordered lists
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK), "Create List");
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK), "Create Ordered List");
        class createList extends AbstractAction {

            private String type = HTML.Tag.UL.toString(); // unordered
            private HTML.Tag tag = HTML.Tag.UL; // unordered

            public createList() {
            }

            public createList(HTML.Tag tag) {
                this.type = tag.toString();
                this.tag = tag;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int start = informationArea.getSelectionStart();
                    String list = "<" + type + "><li></li></" + type + ">";                    
                    informationArea.insertText(start, list, 1, tag);
                    informationArea.setCaretPosition(start + 1);
                } catch (BadLocationException ignored) {
                } catch (IOException ignored) {
                }
                // Show caret
                informationArea.requestFocusInWindow();
            }
        }
        informationArea.getActionMap().put("Create List", new createList());
        informationArea.getActionMap().put("Create Ordered List", new createList(HTML.Tag.OL));
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
                displayPreviewMode();
                informationArea.setEditable(false);
                // disable auto scrolling
                //informationArea.setCaretPosition(0);
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
                    displayButtonsForHTMLMode();
                } else { // html mode --> plain mode
                    displayButtonsForPlainMode();
                }
                // Show caret
                informationArea.requestFocusInWindow();
                if (informationArea.isPlainMode()) {
                    informationArea.setCaretPosition(currentPlainCaretPosition);
                } else {
                    // disable auto scrolling
                    informationArea.setCaretPosition(0);
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
                    if (isInPreviewMode()) {
                        displayButtonsForPlainMode();
                    }
                    displaySaveCancelButton();
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
                    if (isInPreviewMode()) {
                        displayButtonsForPlainMode();
                    }
                    displaySaveCancelButton();
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
                    if (isInPreviewMode()) {
                        displayButtonsForPlainMode();
                    }
                    displaySaveCancelButton();
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
        boldButton.setToolTipText("CTRL + B");
        boldButton.setVisible(false);
        add(boldButton, gbc);
        // set the keystroke on the area (to work in preview mode as well)
        // CTRL B: Bold
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK), "Bold");
        informationArea.getActionMap().put("Bold", new boldAction());
        // italic button
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        italicButton.addActionListener(new italicAction());        
        italicButton.setToolTipText("CTRL + I");
        italicButton.setVisible(false);
        add(italicButton, gbc);
        // set the keystroke on the area (to work in preview mode as well)
        // CTRL I: Italic
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK), "Italic");
        informationArea.getActionMap().put("Italic", new italicAction());
        // underline button
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        underlineButton.addActionListener(new underlineAction());        
        underlineButton.setToolTipText("CTRL + U");
        underlineButton.setVisible(false);
        add(underlineButton, gbc);
        // set the keystroke on the area (to work in preview mode as well)
        // CTRL U: Underline
        informationArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_DOWN_MASK), "Underline");
        informationArea.getActionMap().put("Underline", new underlineAction());
        // background color button
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.weightx = 0.02;
        gbc.weighty = 0.1;
        gbc.gridwidth = 1;
        backgroundColorButton.addActionListener(new ActionListener() {

            /**
             * SPAN tag is to be used as the editor doesn't do the job properly
             *
             * http://stackoverflow.com/questions/13285526/jtextpane-text-background-color-does-not-work
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                        null,
                        Labels.getString("BurndownChartPanel.Choose a color"),
                        Color.BLACK);
                if (newColor != null) {
                    //Add span Tag
                    String htmlStyle = "background-color:" + getHTMLColor(newColor);
                    SimpleAttributeSet attr = new SimpleAttributeSet();
                    attr.addAttribute(HTML.Attribute.STYLE, htmlStyle);
                    MutableAttributeSet COLOR = new SimpleAttributeSet();
                    COLOR.addAttribute(HTML.Tag.SPAN, attr);
                    StyleConstants.setBackground(COLOR, newColor);
                    String selectedText = informationArea.getSelectedText();
                    if (selectedText != null && selectedText.length() > 0) {
                        int start = informationArea.getSelectionStart();
                        informationArea.getStyledDocument().setCharacterAttributes(start, selectedText.length(), COLOR, false);
                        displaySaveCancelButton();
                    }
                }
                // show caret
                informationArea.requestFocusInWindow();
            }

            /**
             * Convert a Java Color to equivalent HTML Color.
             *
             * @param color The Java Color
             * @return The String containing HTML Color.
             */
            public String getHTMLColor(Color color) {
                if (color == null) {
                    return "#000000";
                }
                return "#" + Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
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
                        displaySaveCancelButton();
                    }
                }
                // show caret
                informationArea.requestFocusInWindow();
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
        linkButton.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Create link");
        class linkEnterAction extends AbstractAction {

            /**
             * Insert HTML link
             *
             * Problem unsolved: after carriage return, the link is inserted at
             * the end of the previous line not the beginning of the new line
             *
             * @param e
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int start = informationArea.getSelectionStart();                    
                    if (!linkTextField.getText().isEmpty()) {
                        String href = linkTextField.getText().startsWith("www") ? ("http://" + linkTextField.getText()) : linkTextField.getText();
                        String link = "<a href=\"" + href + "\">" + linkTextField.getText() + "</a>";                        
                        informationArea.insertText(start, link, HTML.Tag.A);
                        informationArea.setCaretPosition(start + linkTextField.getText().length());
                        linkTextField.setText("http://"); // reset field
                        displaySaveCancelButton();
                    }
                } catch (BadLocationException ignored) {
                } catch (IOException ignored) {
                }
                // Show caret
                informationArea.requestFocusInWindow();
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
                // Discard all previous edit
                //undoManager.discardAllEdits();
                hideSaveCancelButton();
                // show caret
                informationArea.requestFocusInWindow();
            }
        };
        // WHEN_IN_FOCUSED_WINDOW makes Save shortcut work (WHEN_FOCUSED doesn't)
        saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "Save");
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
                // Discard all previous edit
                //undoManager.discardAllEdits();
                hideSaveCancelButton();
                informationArea.setEditable(false);
                int row = panel.getTable().getSelectedRow();
                Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                Activity activity = panel.getActivityById(id);
                currentPlainInformation = activity.getNotes().trim();
                currentPlainCaretPosition = 0;
                showInfo(activity);
            }
        });
        add(cancelButton, gbc);
    }

    public void showInfo(final Activity activity) {
        String comment = activity.getNotes().trim();
        if (comment.isEmpty()) { // no comment            
            if (activity.isStory()) {
                // default template for User Story type
                comment = "<p style=\"margin-top: 0\">";
                comment += "<b>Story line</b>";
                comment += "<ul>";
                comment += "<li>As a {user role}, I want to {action} in order to {goal}.</li>";
                comment += "</ul>";                
                comment += "<b>User acceptance criteria</b>";
                comment += "<ol>";                
                comment += "<li>...</li>";
                comment += "<li>...</li>";
                comment += "<li>...</li>";
                comment += "</ol>";
                comment += "<b>Test cases</b>";
                comment += "<ol>";
                comment += "<li>...</li>";
                comment += "<li>...</li>";
                comment += "<li>...</li>";
                comment += "</ol>";
                comment += "</p>";
            }
        } else if (!comment.contains("</body>")) {
            // Backward compatility 3.0.X and imports
            // Check if there is a body tag; if not replace trailing return carriage with P tag
            comment = "<p style=\"margin-top: 0\">" + comment;
            comment = comment.replaceAll(System.getProperty("line.separator"), "</p><p style=\"margin-top: 0\">");
            comment = comment + "</p>";
        }
        int row = panel.getTable().getSelectedRow();
        int selectedActivityId = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
        if (selectedActivityId == activity.getId()) {
            if (selectedActivityId != activityIdTmp) {
                // New activity selected
                hideSaveCancelButton();
                informationArea.setText(comment);
                // record temp info : only whe selecting an activity and recording edits    
                activityIdTmp = selectedActivityId;
                currentPlainInformation = comment;
                currentPlainCaretPosition = 0;
            } else {
                // Currently selected activity                
                if (informationArea.isPlainMode()) {
                    // The comment might have been modified previously
                    comment = currentPlainInformation;
                    //displaySaveCancelButton();
                }
                informationArea.setText(comment);
                // Show caret
                informationArea.requestFocusInWindow();
                if (informationArea.isPlainMode()) {
                    informationArea.setCaretPosition(currentPlainCaretPosition);
                } else {
                    // disable auto scrolling
                    informationArea.setCaretPosition(0);
                }
            }
        } else {
            hideSaveCancelButton();
            // any non-selected activity                           
            informationArea.setText(comment);
            // Show caret
            informationArea.requestFocusInWindow();
            // disable auto scrolling
            informationArea.setCaretPosition(0);
        }
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }
    
    private boolean isInPreviewMode() {
        return previewButton.isVisible();
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
        htmlButton.setText(html32);
        boldButton.setVisible(true);
        italicButton.setVisible(true);
        underlineButton.setVisible(true);
        backgroundColorButton.setVisible(true);
        foregroundColorButton.setVisible(true);
        linkTextField.setVisible(true);
        linkButton.setVisible(true);
    }

    private void displayPreviewMode() {
        String text = informationArea.getText();
        informationArea.setContentType("text/html");
        informationArea.setText(text);
        htmlButton.setText(html32);
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

    private void displaySaveCancelButton() {
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
    }

    private void hideSaveCancelButton() {
        saveButton.setVisible(false);
        cancelButton.setVisible(false);
    }
}
