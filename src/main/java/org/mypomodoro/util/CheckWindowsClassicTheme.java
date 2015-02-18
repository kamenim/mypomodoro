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

import java.awt.Toolkit;

/**
 * Check if the Windows current theme is XP classic
 *
 */
public class CheckWindowsClassicTheme {

    public static boolean isWindowsClassicLAF() {
        return !(Boolean) Toolkit.getDefaultToolkit().getDesktopProperty(
                "win.xpstyle.themeActive");
    }

    /*private static boolean isWindowsLAF() {
     return UIManager.getLookAndFeel().getID().equals("Windows");
     }*/
}
