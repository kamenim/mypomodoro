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
package org.mypomodoro.gui.todo;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DefaultButton;
import org.mypomodoro.model.Activity;

/**
 * Icon/Button Panel
 *
 */
public class ToDoIconPanel {

    private final static ImageIcon squareCrossIcon = new ImageIcon(Main.class.getResource(Main.iconsSetPath + "squareCross.png"));
    private final static ImageIcon squareIcon = new ImageIcon(Main.class.getResource(Main.iconsSetPath + "square.png"));
    private final static ImageIcon plusIcon = new ImageIcon(Main.class.getResource(Main.iconsSetPath + "plus.png"));
    private final static ImageIcon quoteIcon = new ImageIcon(Main.class.getResource(Main.iconsSetPath + "quote.png"));
    private final static ImageIcon dashIcon = new ImageIcon(Main.class.getResource(Main.iconsSetPath + "dash.png"));

    static public void showIconPanel(JPanel iconPanel, Activity activity, Color color) {
        showIconPanel(iconPanel, activity, color, true);
    }

    static public void showIconPanel(JPanel iconPanel, Activity activity, Color color, boolean showName) {
        // Remove all components
        iconPanel.removeAll();

        // Set panel
        iconPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        iconPanel.setFont(new JLabel().getFont().deriveFont(Font.BOLD));

        // Add label component
        JLabel iconLabel = new JLabel();
        iconLabel.setToolTipText(activity.getName());
        if (showName) {
            iconLabel.setText(activity.getName().length() > 25 ? activity.getName().substring(0, 25) + "..." + " " : activity.getName() + " ");
            iconLabel.setForeground(color);
            iconLabel.setFont(iconPanel.getFont().deriveFont(Font.BOLD));
        }
        iconPanel.add(iconLabel);

        // Add icon/buttons
        int estimatedPoms = activity.getEstimatedPoms();
        int realPoms = activity.getActualPoms();
        int overestimatedPoms = activity.getOverestimatedPoms();
        int numInternalInterruptions = activity.getNumInternalInterruptions();
        int numExternalInterruptions = activity.getNumInterruptions();

        // Estimated pomodoros
        for (int i = 0; i < estimatedPoms; i++) {
            if (realPoms >= i + 1) {
                // We can disable the button but it won't look nice on JTattoo Noire themes
                //squareCrossButton.setEnabled(false);
                //squareCrossButton.setDisabledIcon(squareCrossIcon); // icon used when button is disable                                
                iconPanel.add(new DefaultButton(squareCrossIcon, true));
            } else {
                iconPanel.add(new DefaultButton(squareIcon, true));
            }
        }
        // Overestimated pomodoros
        if (overestimatedPoms > 0) {
            // Plus sign
            iconPanel.add(new DefaultButton(plusIcon, true));
            // Overestimated pomodoros
            for (int i = 0; i < overestimatedPoms; i++) {
                if (realPoms >= estimatedPoms + i + 1) {
                    iconPanel.add(new DefaultButton(squareCrossIcon, true));
                } else {
                    iconPanel.add(new DefaultButton(squareIcon, true));
                }
            }
        }
        // Internal interruption
        for (int i = 0; i < numInternalInterruptions; i++) {
            DefaultButton quoteButton = new DefaultButton(quoteIcon, true);
            iconPanel.add(quoteButton);
        }
        // External interruption        
        for (int i = 0; i < numExternalInterruptions; i++) {
            DefaultButton dashButton = new DefaultButton(dashIcon, true);
            iconPanel.add(dashButton);
        }
    }

    static public void clearIconPanel(JPanel iconPanel) {
        iconPanel.removeAll();
    }
}
