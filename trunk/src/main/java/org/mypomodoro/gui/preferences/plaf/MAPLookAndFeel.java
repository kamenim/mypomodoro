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
    private final Color GREEN_DARK = new Color(102, 204, 0);
    private final Color ORANGE_DARK = new Color(255, 204, 0);
    private final Color ORANGE_YELLOW = new Color(255, 255, 102);
    private final Color PURPLE = new Color(153, 0, 153);

    public MAPLookAndFeel() {
        // Table colors
        Main.selectedRowColor = ORANGE_DARK;
        Main.oddRowColor = ColorUtil.WHITE;
        Main.evenRowColor = ORANGE_YELLOW;
        Main.hoverRowColor = ColorUtil.YELLOW_ROW;
        Main.rowBorderColor = DARK_RED;
        Main.taskFinishedColor = GREEN_DARK;
        Main.taskRunningColor = PURPLE;
        Main.iconBackgroundColor = DARK_RED;

        // JTatoo theme settings
        Properties props = new Properties();
        props.put("logoString", "");

        // Main window decoration
        props.put("windowDecoration", "on"); // or macStyleWindowDecoration
        //props.put("macStyleWindowDecoration", "on");
        //props.put("dynamicLayout", "on");        
        //props.put("toolbarDecorated", "on");

        // Scroll bar
        // props.put("macStyleScrollBar", "on");
        // props.put("linuxStyleScrollBar", "on");
        // Title
        // props.put("centerWindowTitle", "on");
        // Text
        //props.put("textAntiAliasing", "on");
        //props.put("textAntiAliasingMode", "gray");
        // Main window Background and foreground colors
        //props.put("backgroundPattern", "on");
        props.put("windowTitleForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));
        props.put("windowTitleBackgroundColor", ColorUtil.toProperty(DARK_RED));
        props.put("windowTitleColorLight", ColorUtil.toProperty(DARK_RED));
        props.put("windowTitleColorDark", ColorUtil.toProperty(DARK_RED));
        props.put("windowBorderColor", ColorUtil.toProperty(DARK_RED));

        // Background and foreground colors
        props.put("backgroundColor", ColorUtil.toProperty(RED));
        props.put("foregroundColor", ColorUtil.toProperty(ColorUtil.WHITE));

        // Menu background colors
        props.put("menuColorLight", ColorUtil.toProperty(DARK_RED));
        props.put("menuColorDark", ColorUtil.toProperty(RED));
        // Menu foreground colors
        props.put("menuForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));
        // Menu items background colors
        props.put("menuBackgroundColor", ColorUtil.toProperty(RED));
        props.put("menuSelectionBackgroundColor", ColorUtil.toProperty(DARK_RED));
        props.put("menuSelectionBackgroundColorLight", ColorUtil.toProperty(DARK_RED));
        props.put("menuSelectionBackgroundColorDark", ColorUtil.toProperty(DARK_RED));
        // Menu items foreground colors
        props.put("menuSelectionForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));

        // Input/fields background and foreground colors
        props.put("inputBackgroundColor", ColorUtil.toProperty(DARK_RED));
        props.put("inputForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));

        // Selection background and foreground colors
        props.put("selectionBackgroundColor", ColorUtil.toProperty(ORANGE_DARK));
        props.put("selectionBackgroundColorLight", ColorUtil.toProperty(ORANGE_DARK));
        props.put("selectionBackgroundColorDark", ColorUtil.toProperty(ORANGE_DARK));
        props.put("selectionForegroundColor", ColorUtil.toProperty(ColorUtil.BLACK));

        // Buttons
        props.put("buttonBackgroundColor", ColorUtil.toProperty(RED));
        props.put("buttonForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));
        props.put("buttonColorLight", ColorUtil.toProperty(DARK_RED));
        props.put("buttonColorDark", ColorUtil.toProperty(DARK_RED));

        // Tooltip
        props.put("tooltipBackgroundColor", ColorUtil.toProperty(RED));
        props.put("tooltipForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE));
        props.put("tooltipBorderSize", "0");

        // Tabbed panel
        props.put("controlBackgroundColor", ColorUtil.toProperty(RED)); // tabbed pane background
        props.put("controlForegroundColor", ColorUtil.toProperty(ColorUtil.WHITE)); // tabs
        props.put("controlColorLight", ColorUtil.toProperty(RED)); // this must be set for rollover prop to work
        props.put("controlColorDark", ColorUtil.toProperty(DARK_RED)); // this must be set for rollover prop to work
        
        // Roll over buttons, table headers, tabs, checkboxes
        props.put("rolloverColorLight", ColorUtil.toProperty(ORANGE_YELLOW));
        props.put("rolloverColorDark", ColorUtil.toProperty(ORANGE_DARK));
        
        setCurrentTheme(props);
    }
}
