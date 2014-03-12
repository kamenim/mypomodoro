package org.mypomodoro.util;

import org.mypomodoro.gui.PreferencesPanel;

/**
 * Time converter utility class
 *
 */
public class TimeConverter {

    // Convertion minutes to duration
    public static String convertToTime(int min) {
        int hours = min / 60;
        int minutes = min % 60;
        return String.format("%02d:%02d", hours, minutes);
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
        String length = "";
        if (PreferencesPanel.preferences.getPlainHours()) {
            length = convertToTime(calculatePlainHours(pomodoros));
        } else {
            length = convertToTime(calculateEffectiveHours(pomodoros));
        }
        return length;
    }
}
