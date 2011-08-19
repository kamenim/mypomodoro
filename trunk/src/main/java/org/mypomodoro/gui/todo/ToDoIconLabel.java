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
 *
 * @author Phil Karoo
 */
public class ToDoIconLabel {

    static public void showIconLabel(JLabel iconLabel, Activity activity) {
        showIconLabel(iconLabel, activity, ColorUtil.BLACK);
    }

    static public void showIconLabel(JLabel iconLabel, Activity activity, Color color) {
        //iconLabel.setOpaque(true);
        //iconLabel.setBackground(Color.white);
        iconLabel.setText(activity.getName());
        iconLabel.setForeground(color);
        int estimatedPoms = activity.getEstimatedPoms();
        int realPoms = activity.getActualPoms();
        int overestimatedPoms = activity.getOverestimatedPoms();
        int numInternalInterruptions = activity.getNumInternalInterruptions();
        int numExternalInterruptions = activity.getNumInterruptions();
        int plusSign = 1;
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
        // Internal interruption
        if (numInternalInterruptions > 0) {
            for (int i = estimatedPoms + ( overestimatedPoms > 0 ? overestimatedPoms + plusSign : 0 ); i < arraySize; i++) {
                icons[i] = new ImageIcon(Main.class.getResource("/images/quote.png"));
            }
        }
        // External interruption
        if (numExternalInterruptions > 0) {
            for (int i = estimatedPoms + ( overestimatedPoms > 0 ? overestimatedPoms + plusSign : 0 ) + numInternalInterruptions; i < arraySize; i++) {
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