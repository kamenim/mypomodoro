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
package org.mypomodoro.util;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * HTML editor
 *
 */
public class HtmlEditor extends JTextPane {
    
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    public HtmlEditor() {
        setEditorKit(new HTMLEditorKit()); // content type = text/html       
        getDocument().putProperty("i18n", Boolean.TRUE);
        // set default HTML body settings        
        String bodyRule = "body {"
                + "color: #000;"
                + "font-family: " + getFont().getFamily() + ";"
                + "font-size: " + getFont().getSize() + "pt;"
                + "margin: 1px;"
                + "}";
        ((HTMLDocument) getDocument()).getStyleSheet().addRule(bodyRule);
        // set default p tag behaviour
        /*String pRule = "p {"
         + "display: inline;"
         + "margin-top: 0px;"
         + "}";
         ((HTMLDocument) getDocument()).getStyleSheet().addRule(pRule);*/

        // Remove some formatting when typing before or after a formatted text
        // we do it the same way MICROSOFT Word Office does:
        // - Formatting is preserved after: bold, italic, underline, foreground style --> nothing to do here
        // - Formatting is removed after: background style
        // - Formating is removed before and after: links
        addCaretListener(new CaretListener() {

            @Override
            public void caretUpdate(CaretEvent event) {
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        int start = HtmlEditor.this.getSelectionStart();
                        int end = HtmlEditor.this.getSelectionEnd();
                        if (start > end) { // backward selection
                            start = end;
                        }
                        AttributeSet selectionAttributes = HtmlEditor.this.getStyledDocument().getCharacterElement(start).getAttributes();
                        MutableAttributeSet inputAttr = HtmlEditor.this.getInputAttributes();
                        /*MutableAttributeSet BOLD = new SimpleAttributeSet();
                         StyleConstants.setBold(BOLD, true);
                         if (!selectionAttributes.containsAttributes(BOLD)) {
                         inputAttr.removeAttribute(StyleConstants.Bold);
                         }
                         MutableAttributeSet ITALIC = new SimpleAttributeSet();
                         StyleConstants.setItalic(ITALIC, true);
                         if (!selectionAttributes.containsAttributes(ITALIC)) {
                         inputAttr.removeAttribute(StyleConstants.Italic);
                         }
                         MutableAttributeSet UNDERLINE = new SimpleAttributeSet();
                         StyleConstants.setUnderline(UNDERLINE, true);
                         if (!selectionAttributes.containsAttributes(UNDERLINE)) {
                         inputAttr.removeAttribute(StyleConstants.Underline);
                         }*/
                        MutableAttributeSet BACKGROUND = new SimpleAttributeSet();
                        StyleConstants.setBackground(BACKGROUND, StyleConstants.getBackground(selectionAttributes));
                        if (!selectionAttributes.containsAttributes(BACKGROUND)) {
                            inputAttr.removeAttribute(StyleConstants.Background);
                        }
                        /*MutableAttributeSet FOREGROUND = new SimpleAttributeSet();
                         StyleConstants.setBackground(FOREGROUND, StyleConstants.getForeground(selectionAttributes));
                         if (!selectionAttributes.containsAttributes(FOREGROUND)) {
                         inputAttr.removeAttribute(StyleConstants.Foreground);
                         }*/
                        Object tag = selectionAttributes.getAttribute(HTML.Tag.A);
                        if (tag == null) {
                            inputAttr.removeAttribute(HTML.Tag.A);
                        }
                    }
                });
            }
        });

        addHyperlinkListener(new MyHyperlinkListener());

        
        //getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "<br/>\n");

        /*InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
         ActionMap am = getActionMap();
         // Replace carriage return with BR tag (rather than p tag)
         im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
         class enterAction extends AbstractAction {

         @Override
         public void actionPerformed(ActionEvent e) {
         try {
         System.err.println("test");
         insertText(getCaretPosition(), "<br>");
         } catch (BadLocationException ignored) {
         } catch (IOException ignored) {
         }
         }
         }        
         am.put("Enter", new enterAction());*/
    }

    // Make URLs clickable in (Pre)View mode
    class MyHyperlinkListener implements HyperlinkListener {

        @Override
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED
                    && Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(e.getURL().toURI());
                } catch (IOException ignored) {
                } catch (URISyntaxException ignored) {
                }
            }
        }
    }

    // Insert text at the cursor position
    public void insertText(int start, String text, Tag insertTag) throws BadLocationException, IOException {
        ((HTMLEditorKit) getEditorKit()).insertHTML((HTMLDocument) getDocument(), start, text, 0, 0, insertTag);
    }
    
    // Get raw text out of html content
    public String getRawText() {
        String text = new String();
        try {
            text = getDocument().getText(0, getDocument().getLength());
        } catch (BadLocationException ex) {
            logger.error("Problem extracting raw content out of html content", ex);
        }
        return text;
    }
}