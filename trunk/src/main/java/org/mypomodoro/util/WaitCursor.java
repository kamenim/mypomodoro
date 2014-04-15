package org.mypomodoro.util;

import java.awt.Cursor;
import org.mypomodoro.Main;

/**
 * Wait cursor
 *
 */
public class WaitCursor {

    private static boolean started = false;

    /**
     * Start wait cursor
     *
     * @return true if already started
     */
    public static boolean startWaitCursor() {
        boolean alreadyStarted = false;
        if (!started) {
            Main.gui.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Main.gui.getGlassPane().setVisible(true);
            started = true;
        } else {
            alreadyStarted = true;
        }
        return alreadyStarted;
    }

    /**
     * Stop wait cursor
     *
     */
    public static void stopWaitCursor() {
        Main.gui.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        Main.gui.getGlassPane().setVisible(false);
        started = false;
    }
}
