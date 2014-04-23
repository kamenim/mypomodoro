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
package org.mypomodoro.util;

import org.mypomodoro.gui.preferences.PreferencesPanel;

/**
 * Time converter utility class
 *
 */
public class TimeConverter {

    // Convertion minutes to duration
    public static String convertToTime(int min) {
        String time;
        int days = min / (60 * 24);
        if (days >= 1) {
            min = min - days * 60 * 24;
        }
        int hours = min / 60;
        int minutes = min % 60;
        if (days >= 1) {
            time = String.format("%d " + Labels.getString("Common.Days"), days);
        } else {
            time = String.format("%02d:%02d", hours, minutes);
        }
        return time;
    }

    // only pomodoros length
    public static int calculateEffectiveHours(int estimate) {
        return estimate * PreferencesPanel.preferences.getPomodoroLength();
    }

    // pomodoros length + breaks
    public static int calculatePlainHours(int estimate) {
        int nbLongBreaks = estimate / PreferencesPanel.preferences.getNbPomPerSet(); // one long break per set
        int nbShortbreaks = estimate - nbLongBreaks; // on short break per pomodoro minus the long breaks
        return calculateEffectiveHours(estimate)
                + nbShortbreaks * PreferencesPanel.preferences.getShortBreakLength()
                + nbLongBreaks * PreferencesPanel.preferences.getLongBreakLength();
    }

    public static String getLength(int pomodoros) {
        String length;
        if (PreferencesPanel.preferences.getPlainHours()) {
            length = convertToTime(calculatePlainHours(pomodoros));
        } else {
            length = convertToTime(calculateEffectiveHours(pomodoros));
        }
        return length;
    }
}
