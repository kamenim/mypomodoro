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
package org.mypomodoro.gui.todo;

import java.awt.Color;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.CompoundIcon;

/**
 * Icon label
 *
 */
public class ToDoIconLabel {

    static public void showIconLabel(JLabel iconLabel, Activity activity) {
        showIconLabel(iconLabel, activity, ColorUtil.BLACK);
    }

    static public void showIconLabel(JLabel iconLabel, Activity activity, Color color) {
        showIconLabel(iconLabel, activity, color, true);
    }

    static public void showIconLabel(JLabel iconLabel, Activity activity, Color color, boolean showName) {
        iconLabel.setToolTipText(activity.getName());
        if (showName) {
            iconLabel.setText(activity.getName().length() > 25 ? activity.getName().substring(0, 25) + "..." : activity.getName());
        }
        iconLabel.setForeground(color);
        int estimatedPoms = activity.getEstimatedPoms();
        int realPoms = activity.getActualPoms();
        int overestimatedPoms = activity.getOverestimatedPoms();
        int numInternalInterruptions = activity.getNumInternalInterruptions();
        int numExternalInterruptions = activity.getNumInterruptions();
        int plusSign = 1;

        // Trim (add three points at the end of the row) when too many pomodoros
        boolean trimIconLabel = false;
        int nbMaxPoms = 10; // max number of pomodoro icon to display 
        if (estimatedPoms + overestimatedPoms > nbMaxPoms) {
            if (estimatedPoms > nbMaxPoms) {
                estimatedPoms = nbMaxPoms;
            }
            overestimatedPoms = (overestimatedPoms > nbMaxPoms - estimatedPoms) ? nbMaxPoms - estimatedPoms : overestimatedPoms;
            trimIconLabel = true;
        }

        int arraySize = estimatedPoms;
        if (overestimatedPoms > 0) {
            arraySize += overestimatedPoms + plusSign;
        }
        if (numInternalInterruptions > 0) {
            arraySize += numInternalInterruptions;
        }
        if (numExternalInterruptions > 0) {
            arraySize += numExternalInterruptions;
        }
        if (trimIconLabel) {
            arraySize += 3; // 3 trailing points
        }
        Icon[] icons = new Icon[arraySize];

        // Estimated pomodoros
        for (int i = 0; i < estimatedPoms; i++) {
            if (i < realPoms) {
                icons[i] = new ImageIcon(Main.class.getResource("/images/squareCross.png"));
            } else {
                icons[i] = new ImageIcon(Main.class.getResource("/images/square.png"));
            }
        }

        // Overestimated pomodoros
        if (overestimatedPoms > 0) {
            // Plus sign
            icons[estimatedPoms] = new ImageIcon(Main.class.getResource("/images/plus.png"));
            // Overestimated pomodoros
            for (int i = estimatedPoms + plusSign; i < arraySize; i++) {
                if (i < realPoms + 1) {
                    icons[i] = new ImageIcon(Main.class.getResource("/images/squareCross.png"));
                } else {
                    icons[i] = new ImageIcon(Main.class.getResource("/images/square.png"));
                }
            }
        }

        // Add three points at the end of the row when necessary
        if (trimIconLabel) {
            for (int i = estimatedPoms + (overestimatedPoms > 0 ? overestimatedPoms + plusSign : 0); i < arraySize; i++) {
                icons[i] = new ImageIcon(Main.class.getResource("/images/point.png"));
            }
        }

        // Internal interruption
        if (numInternalInterruptions > 0) {
            for (int i = estimatedPoms + (overestimatedPoms > 0 ? overestimatedPoms + plusSign : 0) + (trimIconLabel ? 3 : 0); i < arraySize; i++) {
                icons[i] = new ImageIcon(Main.class.getResource("/images/quote.png"));
            }
        }
        // External interruption
        if (numExternalInterruptions > 0) {
            for (int i = estimatedPoms + (overestimatedPoms > 0 ? overestimatedPoms + plusSign : 0) + (trimIconLabel ? 3 : 0) + numInternalInterruptions; i < arraySize; i++) {
                icons[i] = new ImageIcon(Main.class.getResource("/images/dash.png"));
            }
        }
        CompoundIcon icon = new CompoundIcon(2, icons);
        iconLabel.setIcon(icon);
        iconLabel.setVerticalTextPosition(JLabel.CENTER);
        iconLabel.setHorizontalTextPosition(JLabel.LEFT);
    }

    static public void clearIconLabel(JLabel iconLabel) {        
        iconLabel.setText("");
        iconLabel.setIcon(null);
    }
}
