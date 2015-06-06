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
 * About rounding :
 * http://stackoverflow.com/questions/1295424/how-to-convert-float-to-int-with-java
 *
 */
public class TimeConverter {

    // Returns the time length of pomodoros based on the length of a work day in pomodoros
    // one work day = MaxNbPomPerDay
    public static String getLength(int pomodoros) {
        String length = "";
        // Integer division: Integer division returns the true result of division rounded down to the nearest integer. eg 1.8 --> 1
        int nbWorkDays = pomodoros / Main.preferences.getMaxNbPomPerDay();
        // Pomodoro lefts
        int nbPomodorosLeft = pomodoros - nbWorkDays * Main.preferences.getMaxNbPomPerDay();
        int pomodoroLeftInMinutes;
        if (Main.preferences.getPlainHours()) { // plain
            pomodoroLeftInMinutes = convertPomodorosToPlainMinutes(nbPomodorosLeft);
        } else { // effective
            pomodoroLeftInMinutes = convertPomodorosToEffectiveMinutes(nbPomodorosLeft);
        }
        int nbWorkHours = pomodoroLeftInMinutes / 60; // eg 90 min / 60 min = 1 h
        int nbWorkMinutes = pomodoroLeftInMinutes - nbWorkHours * 60; // eg 90 min - 60 min = 30 min
        if (nbWorkDays > 0) {
            length = String.format("%d " + (nbWorkDays == 1 ? Labels.getString("Common.Day") : Labels.getString("Common.Days")), nbWorkDays);
        }
        if (nbWorkDays == 0
                || nbWorkHours > 0
                || nbWorkMinutes > 0) {
            if (nbWorkDays > 0
                    && (nbWorkHours > 0
                    || nbWorkMinutes > 0)) {
                length += " ";
            }
            length += String.format("%02d:%02d", nbWorkHours, nbWorkMinutes);
        }
        return length;
    }

    public static int convertPomodorosToPlainMinutes(int pomodoros) {
        int nbLongBreaks = pomodoros / Main.preferences.getNbPomPerSet();
        int nbShortBreaks = pomodoros - nbLongBreaks;
        return convertPomodorosToEffectiveMinutes(pomodoros)
                + nbLongBreaks * Main.preferences.getLongBreakLength()
                + nbShortBreaks * Main.preferences.getShortBreakLength(); // with breaks        
    }

    private static int convertPomodorosToEffectiveMinutes(int pomodoros) {
        return pomodoros * Main.preferences.getPomodoroLength();
    }

    // Round minutes to hours
    public static float roundToHours(float min) {
        return new Float(Math.floor(min / 60)); // eg 2.78 --> 2.0
    }
}
