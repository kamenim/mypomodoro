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
package org.mypomodoro.gui.preferences.plaf;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;
import java.awt.Color;
import java.util.Properties;
import org.mypomodoro.Main;
import org.mypomodoro.util.ColorUtil;

/**
 * MAP look and Feel RGB codes
 *
 * Based on JTattoo's Acryl Look And Feel http://www.jtattoo.net/ThemeProps.html
 *
 */
public class MAPLookAndFeel extends AcrylLookAndFeel {

    private final Color DARK_RED = new Color(200, 42, 42);
    private final Color RED = new Color(216, 54, 54);
    private final Color YELLOW_HIGHLIGHT = new Color(255, 255, 102);
    /*private final Color GREEN_DARK = new Color(102, 204, 0);
    private final Color ORANGE_DARK = new Color(255, 223, 0);
    private final Color ORANGE_YELLOW = new Color(255, 255, 102);
    private final Color PURPLE = new Color(255, 0, 255);*/
    private final Color GREEN_LIGHT = new Color(216, 255, 216);
    private final Color GREEN_DARK = new Color(145, 221, 145);
    

    public MAPLookAndFeel() {
        // Table colors
        // Shades of orange and yellow: http://www.workwithcolor.com/yellow-color-hue-range-01.htm
        /*Main.selectedRowColor = ORANGE_DARK;
        Main.oddRowColor = ColorUtil.WHITE;
        Main.evenRowColor = ORANGE_YELLOW;
        Main.hoverRowColor = ColorUtil.YELLOW_ROW;
        Main.rowBorderColor = RED;
        Main.taskFinishedColor = GREEN_DARK;
        Main.taskRunningColor = PURPLE;*/
        Main.myIconBackgroundColor = RED;

        // JTatoo theme settings
        Properties props = new Properties();
        props.put("logoString", "");       
        
        // Main window background and foreground colors
        props.put("windowTitleForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));
        props.put("windowTitleBackgroundColor", ColorUtil.toProperty(RED));
        props.put("windowTitleColorLight", ColorUtil.toProperty(RED));
        props.put("windowTitleColorDark", ColorUtil.toProperty(RED));
        props.put("windowBorderColor", ColorUtil.toProperty(RED));
        
        // (Inactive) Main window background and foreground colors (the window is inactive when opening a dialog message)
        props.put("windowInactiveTitleForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));
        props.put("windowInactiveTitleBackgroundColor", ColorUtil.toProperty(RED));
        props.put("windowInactiveTitleColorLight", ColorUtil.toProperty(RED));
        props.put("windowInactiveTitleColorDark", ColorUtil.toProperty(RED));
        props.put("windowInactiveBorderColor", ColorUtil.toProperty(RED));

        // Background and foreground colors
        props.put("backgroundColor", ColorUtil.toProperty(DARK_RED));
        props.put("foregroundColor", ColorUtil.toProperty(ColorUtil.WHITE));

        // Menu background colors
        props.put("menuColorLight", ColorUtil.toProperty(RED));
        props.put("menuColorDark", ColorUtil.toProperty(DARK_RED));
        
        // Menu foreground colors
        props.put("menuForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));
        
        // Menu items background colors
        props.put("menuBackgroundColor", ColorUtil.toProperty(DARK_RED));
        props.put("menuSelectionBackgroundColor", ColorUtil.toProperty(RED));
        props.put("menuSelectionBackgroundColorLight", ColorUtil.toProperty(RED));
        props.put("menuSelectionBackgroundColorDark", ColorUtil.toProperty(RED));
        
        // Menu items foreground colors
        props.put("menuSelectionForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));

        // Input/fields background and foreground colors
        props.put("inputBackgroundColor", ColorUtil.toProperty(RED));
        props.put("inputForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));

        // Selection background and foreground colors
        props.put("selectionBackgroundColor", ColorUtil.toProperty(GREEN_LIGHT));
        props.put("selectionBackgroundColorLight", ColorUtil.toProperty(GREEN_LIGHT));
        props.put("selectionBackgroundColorDark", ColorUtil.toProperty(GREEN_LIGHT));
        props.put("selectionForegroundColor", ColorUtil.toProperty(ColorUtil.BLACK));

        // Buttons
        props.put("buttonBackgroundColor", ColorUtil.toProperty(DARK_RED));
        props.put("buttonForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));
        props.put("buttonColorLight", ColorUtil.toProperty(RED));
        props.put("buttonColorDark", ColorUtil.toProperty(RED));

        // Tooltip
        props.put("tooltipBackgroundColor", ColorUtil.toProperty(DARK_RED));
        props.put("tooltipForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));
        props.put("tooltipBorderSize", "0");

        // Tabbed panel
        props.put("controlBackgroundColor", ColorUtil.toProperty(DARK_RED)); // tabbed pane background
        props.put("controlForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE)); // tabs
        props.put("controlColorLight", ColorUtil.toProperty(DARK_RED)); // this must be set for rollover prop to work
        props.put("controlColorDark", ColorUtil.toProperty(RED)); // this must be set for rollover prop to work
        
        // Roll over buttons, table headers, tabs, checkboxes
        //props.put("rolloverColorLight", ColorUtil.toProperty(ORANGE_YELLOW));
        //props.put("rolloverColorDark", ColorUtil.toProperty(ORANGE_DARK));
        props.put("rolloverColorLight", ColorUtil.toProperty(GREEN_LIGHT));
        props.put("rolloverColorDark", ColorUtil.toProperty(GREEN_DARK));
        
        setCurrentTheme(props);
    }
}
