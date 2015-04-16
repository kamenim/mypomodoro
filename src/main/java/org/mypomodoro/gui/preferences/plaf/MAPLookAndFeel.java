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
import org.mypomodoro.util.ColorUtil;

/**
 * MAP look and Feel
 * RGB codes
 *
 * Based on JTattoo's Acryl Look And Feel http://www.jtattoo.net/ThemeProps.html
 *
 */
public class MAPLookAndFeel extends AcrylLookAndFeel {
    
    private final Color WHITE = Color.WHITE;
    private final Color BLACK = Color.BLACK;
    private final Color DARK_RED = new Color(200, 42, 42);
    private final Color RED = new Color(216, 54, 54);
    private final Color DARK_GRAY_TIMER = Color.DARK_GRAY;
    private final Color LIGHT_GREEN = new Color(216, 255, 216);

    public MAPLookAndFeel() {
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
        
        props.put("windowTitleForegroundColor", ColorUtil.toProperty(WHITE));
        props.put("windowTitleBackgroundColor", ColorUtil.toProperty(DARK_RED));
        props.put("windowTitleColorLight", ColorUtil.toProperty(DARK_RED));
        props.put("windowTitleColorDark", ColorUtil.toProperty(DARK_RED));
        props.put("windowBorderColor", ColorUtil.toProperty(DARK_RED));
        
        // Background and foreground colors
        props.put("backgroundColor", ColorUtil.toProperty(RED));
        props.put("foregroundColor", ColorUtil.toProperty(WHITE));
        
        // Menu background colors
        props.put("menuColorLight", ColorUtil.toProperty(DARK_RED));
        props.put("menuColorDark", ColorUtil.toProperty(RED));
        // Menu foreground colors
        props.put("menuForegroundColor", ColorUtil.toProperty(WHITE));
        // Menu items background colors
        props.put("menuBackgroundColor", ColorUtil.toProperty(RED));
        props.put("menuSelectionBackgroundColor", ColorUtil.toProperty(DARK_RED));
        props.put("menuSelectionBackgroundColorLight", ColorUtil.toProperty(DARK_RED));
        props.put("menuSelectionBackgroundColorDark", ColorUtil.toProperty(DARK_RED));
        // Menu items foreground colors
        props.put("menuSelectionForegroundColor", ColorUtil.toProperty(WHITE));

        // Input/fields background and foreground colors
        props.put("inputBackgroundColor", ColorUtil.toProperty(LIGHT_GREEN));
        props.put("inputForegroundColor", ColorUtil.toProperty(BLACK));

        // Selection background and foreground colors
        props.put("selectionBackgroundColor", ColorUtil.toProperty(Color.GREEN));
        props.put("selectionBackgroundColorLight", ColorUtil.toProperty(Color.GREEN));
        props.put("selectionBackgroundColorDark", ColorUtil.toProperty(Color.GREEN));
        props.put("selectionForegroundColor", ColorUtil.toProperty(BLACK));
                
        // Buttons
        props.put("buttonBackgroundColor", ColorUtil.toProperty(RED));
        props.put("buttonForegroundColor", ColorUtil.toProperty(WHITE));
        props.put("buttonColorLight", ColorUtil.toProperty(DARK_RED));
        props.put("buttonColorDark", ColorUtil.toProperty(DARK_RED));

        // Tooltip
        props.put("tooltipBackgroundColor", ColorUtil.toProperty(RED));
        props.put("tooltipForegroundColor", ColorUtil.toProperty(WHITE));
        props.put("tooltipBorderSize", "0");
        
        
        props.put("controlBackgroundColor", ColorUtil.toProperty(RED));
        props.put("controlForegroundColor", ColorUtil.toProperty(BLACK)); // same ad input fields
        props.put("controlColorLight", ColorUtil.toProperty(DARK_RED));
        props.put("controlColorDark", ColorUtil.toProperty(DARK_RED));
        /*props.put("rolloverColor", ColorUtil.toProperty(DARK_RED));
        props.put("rolloverColorLight", ColorUtil.toProperty(DARK_RED));
        props.put("rolloverColorDark", ColorUtil.toProperty(DARK_RED));*/
        

        //props.put("controlColor", "218 254 230");
        //props.put("controlColorLight", "218 254 230");
        //props.put("controlColorDark", ColorUtil.toProperty(ColorUtil.RED));

        
        //props.put("rolloverColor", "218 254 230");
        //props.put("rolloverColorLight", "218 254 230");
        //props.put("rolloverColorDark", ColorUtil.toProperty(ColorUtil.RED));

        
        setCurrentTheme(props);
    }
}
