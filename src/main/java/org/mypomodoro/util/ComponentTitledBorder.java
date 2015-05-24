/**
 * MySwing: Advanced Swing Utilites Copyright (C) 2005 Santhosh Kumar T
 * <p/>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.mypomodoro.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.Main;

public class ComponentTitledBorder implements Border, MouseListener, MouseMotionListener, SwingConstants {

    private final int offset = 5;
    private final Component comp;
    private final JComponent container;
    private Rectangle rect;
    private Border border;
    private boolean mouseEntered = false;

    public ComponentTitledBorder(Component comp, JComponent container, Border border) {
        this(comp, container, border, comp.getFont());
    }

    public ComponentTitledBorder(Component comp, JComponent container, Border border, Font f) {
        this.comp = comp;
        this.container = container;
        this.border = border;
        container.addMouseListener(this);
        container.addMouseMotionListener(this);
        this.comp.setFont(f);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Insets borderInsets = border.getBorderInsets(c);
        Insets insets = getBorderInsets(c);
        int temp = (insets.top - borderInsets.top) / 2;
        /*if (comp instanceof JCheckBox
         && ((JCheckBox)comp).isSelected()) {
         comp.setBackground(Main.selectedRowColor);
         border = new LineBorder(Main.selectedRowColor);
         } else {
         comp.setBackground(container.getBackground());
         border = new LineBorder(container.getBackground());
         }*/
        border.paintBorder(c, g, x, y + temp, width, height - temp);
        Dimension size = comp.getPreferredSize();
        rect = new Rectangle(offset, 0, size.width, size.height);
        SwingUtilities.paintComponent(g, comp, (Container) c, rect);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        Dimension size = comp.getPreferredSize();
        Insets insets = border.getBorderInsets(c);
        insets.top = Math.max(insets.top, size.height);
        return insets;
    }

    private void dispatchEvent(MouseEvent me) {
        if (rect != null && rect.contains(me.getX(), me.getY())) {
            dispatchEvent(me, me.getID());
        }
    }

    private void dispatchEvent(MouseEvent me, int id) {
        Point pt = me.getPoint();
        pt.translate(-offset, 0);
        comp.setSize(rect.width, rect.height);
        comp.dispatchEvent(new MouseEvent(comp, id, me.getWhen(),
                me.getModifiers(), pt.x, pt.y, me.getClickCount(),
                me.isPopupTrigger(), me.getButton()));
        if (!comp.isValid()) {
            container.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        dispatchEvent(me);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
        if (mouseEntered) {
            mouseEntered = false;
            border = new EtchedBorder(); // change border on exit 
            dispatchEvent(me, MouseEvent.MOUSE_EXITED);
        }
    }

    @Override
    public void mousePressed(MouseEvent me) {
        dispatchEvent(me);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        dispatchEvent(me);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        if (rect == null) {
            return;
        }
        if (mouseEntered == false && rect.contains(me.getX(), me.getY())) {
            mouseEntered = true;
            border = new EtchedBorder(Main.selectedRowColor, Main.selectedRowColor); // change border on exit
            dispatchEvent(me, MouseEvent.MOUSE_ENTERED);
        } else if (mouseEntered == true) {
            if (rect.contains(me.getX(), me.getY()) == false) {
                mouseEntered = false;
                border = new EtchedBorder(); // change border on entering
                dispatchEvent(me, MouseEvent.MOUSE_EXITED);
            } else {
                dispatchEvent(me, MouseEvent.MOUSE_MOVED);
            }
        }
    }

    public void repaint() {
        container.repaint();
    }

    public void setTitleFont(Font f) {
        comp.setFont(f);
    }

    public void setBackground(Color color) {
        comp.setBackground(color);
        container.setBackground(color);
    }

    public void setToolTipText(String text) {
        container.setToolTipText(text);
    }
}
