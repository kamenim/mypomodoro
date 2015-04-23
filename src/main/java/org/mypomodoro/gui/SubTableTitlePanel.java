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
package org.mypomodoro.gui;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DefaultButton;
import org.mypomodoro.gui.preferences.PreferencesInputForm;
import org.mypomodoro.util.ColorUtil;

/**
 *
 *
 */
public class SubTableTitlePanel extends TableTitlePanel {

    private final IListPanel panel;
    private int viewCount = 0;

    public SubTableTitlePanel(IListPanel panel, AbstractTable table) {
        super(panel, table);

        this.panel = panel;

        // Manage mouse hovering
        addMouseMotionListener(new HoverMouseMotionAdapter());
        // This is to address the case/event when the mouse exit the title
        addMouseListener(new ExitMouseAdapter());

        // On click action
        addMouseListener(new OneClickMouseAdapter(this));
    }

    // Add listeners to possible components
    // Component (button) are added dynamically to this panel    
    @Override
    public Component add(Component comp) {
        boolean isHoverMouseMotionAdapter = false;
        boolean isExitMouseAdapter = false;
        boolean isOneClickMouseAdapter = false;
        // make sure the listeners aren't added each the component is added
        for (MouseListener listener : comp.getMouseListeners()) {
            if (listener instanceof HoverMouseMotionAdapter) {
                isHoverMouseMotionAdapter = true;
            }
            if (listener instanceof ExitMouseAdapter) {
                isExitMouseAdapter = true;
            }
            if (listener instanceof OneClickMouseAdapter) {
                isOneClickMouseAdapter = true;
            }
        }
        if (!isHoverMouseMotionAdapter) {
            comp.addMouseMotionListener(new HoverMouseMotionAdapter());
        }
        if (!isExitMouseAdapter) {
            comp.addMouseListener(new ExitMouseAdapter());
        }
        if (!isOneClickMouseAdapter) {
            comp.addMouseListener(new OneClickMouseAdapter(comp));
        }
        return super.add(comp);
    }

    // Hover
    class HoverMouseMotionAdapter extends MouseMotionAdapter {

        @Override
        public void mouseMoved(MouseEvent e) {
            setBorder(new EtchedBorder(EtchedBorder.LOWERED, Main.selectedRowColor, Main.rowBorderColor));
            setBackground(Main.hoverRowColor);
            titleLabel.setForeground(ColorUtil.BLACK); // this is necessary for themes such as JTatoo Noire
        }
    }

    // Exit
    class ExitMouseAdapter extends MouseAdapter {

        @Override
        public void mouseExited(MouseEvent e) {
            setBorder(new EtchedBorder(EtchedBorder.LOWERED));
            JPanel p = new JPanel();
            setBackground(p.getBackground()); // reset default/theme background color
            titleLabel.setForeground(p.getForeground()); // this is necessary for themes such as JTatoo Noire
        }
    }

    // One click: expand / fold
    class OneClickMouseAdapter extends MouseAdapter {

        private final Component comp;
        private Robot robot = null; // used to move the cursor

        public OneClickMouseAdapter(Component comp) {
            this.comp = comp;
            try {
                robot = new Robot();
            } catch (AWTException ignored) {
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) { // single click
                if (viewCount == 2 && !(comp instanceof DefaultButton)) { // fold: excluding buttons
                    panel.getListPane().remove(panel.getSubTableScrollPane());
                    panel.addTableTitlePanel();
                    panel.addTable();
                    panel.addSubTableTitlePanel(); // put the sub title back at the bottom
                    viewCount = 0;
                } else if (viewCount == 0 && panel.getTable().getSelectedRowCount() == 1) { // expand half way: including buttons                        
                    panel.getListPane().add(panel.getSubTableScrollPane());
                    viewCount = 1;
                } else if (viewCount == 1 && !(comp instanceof DefaultButton)) { // maximize: excluding buttons                        
                    panel.getListPane().remove(panel.getTableScrollPane());
                    panel.getListPane().remove(panel.getTableTitlePanel());
                    viewCount = 2;
                }
                // The next two lines adress an issue found on NimRod theme with the resizing of titles                    
                if (Main.preferences.getTheme().equalsIgnoreCase(PreferencesInputForm.NIMROD_LAF)) {
                    setMaximumSize(new Dimension(Main.gui.getSize().width, 30));
                    panel.getTableTitlePanel().setMaximumSize(new Dimension(Main.gui.getSize().width, 30));
                }
                // The two following lines are required to
                // repaint after resizing and move the cursor correctly
                panel.getListPane().validate();
                panel.getListPane().repaint();
                // Center cursor on panel
                if (robot != null && !(comp instanceof DefaultButton)) {
                    Point p = getLocationOnScreen(); // location on screen
                    // Center cursor in the middle of the component
                    robot.mouseMove((int) p.getX() + getWidth() / 2, (int) p.getY() + getHeight() / 2);
                }
                if (panel.getTable().getSelectedRowCount() == 1) {
                    //panel.getTable().showCurrentSelectedRow(); TODO replace JXTable in IListPanel with AbstractActivitiesTable
                }
            }
        }
    }
}
