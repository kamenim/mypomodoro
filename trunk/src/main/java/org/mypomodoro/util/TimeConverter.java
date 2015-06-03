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

    private static final long nbPomodoroPerWorkDay = Main.preferences.getMaxNbPomPerDay(); // eg: 6 pom/day = 6 breaks
    private static final long nbLongBreaksPerWorkDay = Math.round(Math.floor(nbPomodoroPerWorkDay / Main.preferences.getNbPomPerSet())); // eg: 6 pom/day / 3 pom/set --> 2 long breaks
    private static final long nbShortbreaksPerWorkDay = nbPomodoroPerWorkDay - nbLongBreaksPerWorkDay; // eg: 6 breaks - 2 long breaks = 4 short breaks

    // returns the time length of pomodoros based on the length of a work day in pomodoros
    // one work day = MaxNbPomPerDay
    public static String getLength(int pomodoros) {
        String length;
        if (Main.preferences.getPlainHours()) { // plain
            length = convertPomodorosToPlainWorkTime(pomodoros);
        } else { // effective
            length = convertPomodorosToEffectiveWorkTime(pomodoros);
        }
        return length;
    }

    public static String convertPomodorosToPlainWorkTime(int pomodoros) {
        long plainWorkDayOfPomdorosInMinutes = nbPomodoroPerWorkDay * Main.preferences.getPomodoroLength(); // no breaks
        long workTimeOfPomodorosInMinutes = calculatePlainMinutes(pomodoros);
        return convertPomodorosToWorkTime(plainWorkDayOfPomdorosInMinutes, workTimeOfPomodorosInMinutes);
    }

    public static String convertPomodorosToEffectiveWorkTime(int pomodoros) {
        long effectiveWorkDayOfPomdorosAndBreaksInMinutes = nbPomodoroPerWorkDay * Main.preferences.getPomodoroLength()
                + nbLongBreaksPerWorkDay * Main.preferences.getLongBreakLength()
                + nbShortbreaksPerWorkDay * Main.preferences.getShortBreakLength(); // with breaks
        long workTimeOfPomodorosAndBreaksInMinutes = calculateEffectiveMinutes(pomodoros); // with breaks
        return convertPomodorosToWorkTime(effectiveWorkDayOfPomdorosAndBreaksInMinutes, workTimeOfPomodorosAndBreaksInMinutes);
    }

    // workDayOfPomdorosAndBreaksInMinutes represents a day in term of nb max of pomodoros and breaks per day
    // workTimeOfPomodorosInMinutes reprents the length of pomdoros in minutes
    private static String convertPomodorosToWorkTime(long workDayOfPomdorosAndBreaksInMinutes, long workTimeOfPomodorosInMinutes) {
        long workTimeInDays = Math.round(Math.floor(workTimeOfPomodorosInMinutes / workDayOfPomdorosAndBreaksInMinutes)); // eg 5,6 --> 5 days; 0,3 --> 0 day
        long workTimeLeftTotalInMinutes = workTimeInDays > 0 ? workTimeOfPomodorosInMinutes - (workTimeInDays * workDayOfPomdorosAndBreaksInMinutes) : workTimeOfPomodorosInMinutes; // eg 3,6 * 25 min/pom = 90 min
        long workTimeLeftInHours = Math.round(Math.floor(workTimeLeftTotalInMinutes / 60)); // 90 min / 60 min = 1 h
        long workTimeLeftInMinutes = Math.round(Math.floor(workTimeLeftTotalInMinutes - workTimeLeftInHours * 60)); // 90 min - 60 min = 30 min
        String plainWorkTime;
        if (workTimeInDays > 0) {
            plainWorkTime = String.format("%d " + (workTimeInDays == 1 ? Labels.getString("Common.Day") : Labels.getString("Common.Days")) + " %02d:%02d", workTimeInDays, workTimeLeftInHours, workTimeLeftInMinutes);
        } else {
            plainWorkTime = String.format("%02d:%02d", workTimeLeftInHours, workTimeLeftInMinutes);
        }
        return plainWorkTime;
    }

    // pomodoros length + breaks    
    public static long calculatePlainMinutes(long pomodoros) {
        long nbLongBreaks = Math.round(Math.floor(pomodoros / Main.preferences.getNbPomPerSet()));
        long nbShortBreaks = pomodoros - nbLongBreaks;
        return calculateEffectiveMinutes(pomodoros)
                + nbLongBreaks * Main.preferences.getLongBreakLength()
                + nbShortBreaks * Main.preferences.getShortBreakLength();
    }

    // pomodoros only
    private static long calculateEffectiveMinutes(long pomodoros) {
        return pomodoros * Main.preferences.getPomodoroLength();
    }

    // Round minutes to hours
    public static float roundToHours(float min) {
        return new Float(Math.round(Math.floor(min / 60)));
    }
}
