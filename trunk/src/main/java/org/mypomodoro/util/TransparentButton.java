/*
 * Copyright (C)
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

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JButton;

/**
 * Transparent button (including text)
 *
 */
public class TransparentButton extends JButton {

    private float alpha = 0.0f; // completely transparent

    private boolean fireRepaint = true;

    public TransparentButton() {
        setOpaque(false);
        setEnabled(false);
    }

    public TransparentButton(String text) {
        super(text);
        setOpaque(false);
        setEnabled(false);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        super.paint(g2);
        g2.dispose();
    }

    // this calls paint(...)
    @Override
    public void repaint() {
        if (fireRepaint) {
            super.repaint();
        }
    }

    // fireRepaint to prevent the button to flicker on Win7 aero theme (repaint() called multiple time because of setEnabled and other fire actions)
    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag) {
            alpha = 1.0f;
            // must be setOpaque(true) to make the button opaque with Win XP classic             
            if (CheckWindowsClassicTheme.isWindowsClassicLAF()) {
                setOpaque(true);
            } else {
                setOpaque(false);
            }
            fireRepaint = true;
            setEnabled(true);
            fireRepaint = false;
        } else {
            alpha = 0.0f;
            setOpaque(false);
            fireRepaint = true;
            setEnabled(false);
            fireRepaint = false;
        }
    }

    public void setFireRepaint(boolean repaint) {
        fireRepaint = repaint;
    }
}
