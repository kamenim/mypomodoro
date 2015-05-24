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

import org.mypomodoro.Main;

/**
 * Time converter utility class
 *
 */
public class TimeConverter {
    
    // TODO the convertion in days in wrong: 1 day = nb pomodoro/day * pomodoro length
    
    // Convertion minutes to duration
    // @return time in format mm:hh; or d days if time > 1 day
    public static String convertMinutesToString(float min) {
        String time;
        long days = Math.round(Math.floor(min / (60 * 24))); // round is only used to convert to long        
        if (days >= 1) {
            min = min - days * 60 * 24; // minutes left
        }
        float hours = min / 60;
        float minutes = min % 60;
        if (days >= 1) {
            time = String.format("%d " + (days == 1 ? Labels.getString("Common.Day") : Labels.getString("Common.Days")) + " %02d:%02d", days, Math.round(Math.floor(hours)), Math.round(Math.floor(minutes)));
        } else {
            time = String.format("%02d:%02d", Math.round(Math.floor(hours)), Math.round(Math.floor(minutes)));
        }
        return time;
    }

    public static float roundToHours(float min) {
        return new Float(Math.round(Math.floor(min / 60)));
    }

    // only pomodoros length
    // @return minutes
    public static float calculateEffectiveMinutes(float pomodoros) {
        return pomodoros * Main.preferences.getPomodoroLength();
    }

    // pomodoros length + breaks
    // @return minutes
    public static float calculatePlainMinutes(float pomodoros) {
        long nbLongBreaks = Math.round(Math.floor(pomodoros / Main.preferences.getNbPomPerSet())); // one long break per set
        long nbShortbreaks = Math.round(pomodoros - nbLongBreaks); // on short break per pomodoro minus the long breaks
        return calculateEffectiveMinutes(pomodoros)
                + nbShortbreaks * Main.preferences.getShortBreakLength()
                + nbLongBreaks * Main.preferences.getLongBreakLength();
    }

    public static String getLength(int pomodoros) {
        String length;
        if (Main.preferences.getPlainHours()) {
            length = convertMinutesToString(calculatePlainMinutes(pomodoros));
        } else {
            length = convertMinutesToString(calculateEffectiveMinutes(pomodoros));
        }
        return length;
    }
}
