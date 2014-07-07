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

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Enumeration;
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
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * HTML editor
 *
 */
public class HtmlEditor extends JTextPane {

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
         ((HTMLDocument)getDocument()).getStyleSheet().addRule(bodyRule);
         // set default p tag behaviour
         String pRule = "p {"
         + "display: inline;" 
         + "margin-top: 0px;"
         + "}";
         ((HTMLDocument)getDocument()).getStyleSheet().addRule(pRule);
        // Remove all formatting when typing after a formatted text (except for URLs so the text of the URLS may be changed)
         
         
         addCaretListener(new CaretListener() {

            @Override
            public void caretUpdate(CaretEvent event) {
                EventQueue.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        /*AttributeSet selectionAttributes = HtmlEditor.this.getStyledDocument().getCharacterElement(HtmlEditor.this.getSelectionStart()).getAttributes();
                        Enumeration names = selectionAttributes.getAttributeNames();
                        while (names.hasMoreElements()) {
                            System.err.println("name=" + names.nextElement());
                        }*/
                        
                        MutableAttributeSet inputAttr = HtmlEditor.this.getInputAttributes();
                        MutableAttributeSet BOLD = new SimpleAttributeSet();
                        StyleConstants.setBold(BOLD, true);
                        if (!HtmlEditor.this.getStyledDocument().getCharacterElement(HtmlEditor.this.getSelectionStart()).getAttributes().containsAttributes(BOLD)) {
                            inputAttr.removeAttribute(StyleConstants.Bold);
                        }
                        MutableAttributeSet ITALIC = new SimpleAttributeSet();
                        StyleConstants.setItalic(ITALIC, true);
                        if (!HtmlEditor.this.getStyledDocument().getCharacterElement(HtmlEditor.this.getSelectionStart()).getAttributes().containsAttributes(ITALIC)) {
                            inputAttr.removeAttribute(StyleConstants.Italic);                            
                        }
                        MutableAttributeSet UNDERLINE = new SimpleAttributeSet();
                        StyleConstants.setUnderline(UNDERLINE, true);
                        if (!HtmlEditor.this.getStyledDocument().getCharacterElement(HtmlEditor.this.getSelectionStart()).getAttributes().containsAttributes(UNDERLINE)) {                            
                            inputAttr.removeAttribute(StyleConstants.Underline);
                        }
                        MutableAttributeSet BACKGROUND = new SimpleAttributeSet();
                        StyleConstants.setBackground(BACKGROUND, Color.BLACK);
                        if (!HtmlEditor.this.getStyledDocument().getCharacterElement(HtmlEditor.this.getSelectionStart()).getAttributes().containsAttributes(BACKGROUND)) {
                            inputAttr.removeAttribute(StyleConstants.Background);
                        }
                        MutableAttributeSet FOREGROUND = new SimpleAttributeSet();
                        StyleConstants.setBackground(FOREGROUND, Color.BLACK);
                        if (!HtmlEditor.this.getStyledDocument().getCharacterElement(HtmlEditor.this.getSelectionStart()).getAttributes().containsAttributes(FOREGROUND)) {
                            inputAttr.removeAttribute(StyleConstants.Foreground);
                        }
                        MutableAttributeSet HTMLATAG = new SimpleAttributeSet();
                        //StyleConstants.setComponent(HTMLATAG, );
                        if (!HtmlEditor.this.getStyledDocument().getCharacterElement(HtmlEditor.this.getSelectionStart()).getAttributes().containsAttributes(HTMLATAG)) {
                            inputAttr.removeAttribute(HTML.Tag.A);                            
                        }
                    }
                });
            }
        });

        addHyperlinkListener(new MyHyperlinkListener());
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
    public void insertText(int start, String text) throws BadLocationException, IOException {
        ((HTMLEditorKit) getEditorKit()).insertHTML((HTMLDocument) getDocument(), start, text, 0, 0, null);
    }
}
