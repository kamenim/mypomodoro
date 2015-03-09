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
import java.util.Properties;

/**
 * MAP look and Feel
 *
 * Based on JTattoo's Acryl Look And Feel http://www.jtattoo.net/ThemeProps.html
 *
 */
public class MAPLookAndFeel extends AcrylLookAndFeel {

    public MAPLookAndFeel() {
        Properties props = new Properties();
        props.put("logoString", "");

        //props.put("selectionBackgroundColor", "180 240 197");
        props.put("selectionBackgroundColor", "216 54 54");
        //props.put("menuSelectionBackgroundColor", "180 240 197");
        props.put("menuSelectionBackgroundColor", "216 54 54");

        props.put("controlColor", "218 254 230");
        props.put("controlColorLight", "218 254 230");
        //props.put("controlColorDark", "180 240 197");
        props.put("controlColorDark", "216 54 54");

        props.put("buttonColor", "218 230 254");
        props.put("buttonColorLight", "255 255 255");
        props.put("buttonColorDark", "244 242 232");

        props.put("rolloverColor", "218 254 230");
        props.put("rolloverColorLight", "218 254 230");
        //props.put("rolloverColorDark", "180 240 197");
        props.put("rolloverColorDark", "216 54 54");

        props.put("windowTitleForegroundColor", "0 0 0");
        //props.put("windowTitleBackgroundColor", "180 240 197");
        props.put("windowTitleBackgroundColor", "216 54 54");
        props.put("windowTitleColorLight", "218 254 230");
        //props.put("windowTitleColorDark", "180 240 197");
        props.put("windowTitleColorDark", "216 54 54");
        props.put("windowBorderColor", "218 254 230");
        setCurrentTheme(props);
    }
}
