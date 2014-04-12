package org.mypomodoro.util;

import java.awt.Cursor;
import java.awt.event.*;
import org.mypomodoro.Main;

/**
 * Wait cursor
 *
 */
public class WaitCursor {

    /**
     * Start wait cursor
     *
     */
    public static void startWaitCursor() {
        Main.gui.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Main.gui.getGlassPane().setVisible(true);
    }

    /**
     * Stop wait cursor
     *
     */
    public static void stopWaitCursor() {
        Main.gui.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        Main.gui.getGlassPane().setVisible(false);
    }
}
