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
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * HTML editor
 *
 */
public class HtmlEditor extends JTextPane {

    boolean isInProgress = false;

    public HtmlEditor() {
        setEditorKit(new HTMLEditorKit()); // content type = text/html        
        getDocument().putProperty("i18n", Boolean.TRUE);
        
        // Remove all formatting when typing after a formatted text (except for URLs so the text of the URLS may be changed)
        addCaretListener(new CaretListener() {
            
            @Override
            public void caretUpdate(CaretEvent event) {
                EventQueue.invokeLater(new Runnable() {
                    
                    @Override
                    public void run() {
                        MutableAttributeSet inputAttr = HtmlEditor.this.getInputAttributes();                       
                        inputAttr.removeAttribute(StyleConstants.Bold);
                        inputAttr.removeAttribute(StyleConstants.Italic);
                        inputAttr.removeAttribute(StyleConstants.Underline); 
                        inputAttr.removeAttribute(StyleConstants.Background);
                        inputAttr.removeAttribute(StyleConstants.Foreground);
                    }
                });
            }
        });
        // Make URLs clickable (preview mode)
        addHyperlinkListener(new MyHyperlinkListener()); 
    }

    class MyHyperlinkListener implements HyperlinkListener {

        @Override
        public void hyperlinkUpdate(HyperlinkEvent e) {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException e1) {
                        // 
                    } catch (URISyntaxException e1) {
                        // 
                    }
                }
            }
        }
    }
    
    public void insertText(int start, String text) throws BadLocationException, IOException {
        ((HTMLEditorKit)getEditorKit()).insertHTML((HTMLDocument)getDocument(), start, text, 0, 0, null);
    }
}
