package org.mypomodoro.model;

import java.util.Locale;
import org.mypomodoro.db.PreferencesDAO;
import org.mypomodoro.util.DateUtil;

/**
 * Preference Object stores all user preferences.
 *
 */
public final class Preferences {

    public Preferences() {
        refresh();
    }

    // ATTRIBUTES
    /**
     * Pomodoro length
     */
    public static final int PLENGTH = 25;
    private int pomodoroLength = PLENGTH;
    /**
     * Short break length
     */
    public static final int SBLENGTH = 5;
    private int shortBreakLength = SBLENGTH;
    /**
     * Long break length
     */
    public static final int LBLENGTH = 20;
    private int longBreakLength = LBLENGTH;
    /**
     * Max nb pomodoros per activity
     */
    public static final int MNPPACTIVITY = 5;
    private int maxNbPomPerActivity = MNPPACTIVITY;
    /**
     * Max nb pomodoros per day
     */
    public static final int MNPPDAY = 10;
    private int maxNbPomPerDay = MNPPDAY;
    /**
     * Nb pomodoros per set
     */
    public static final int NPPSet = 4;
    private int nbPomPerSet = NPPSet;
    /**
     * Ticking
     */
    public static final boolean TICKING = true;
    private boolean ticking = TICKING;
    /**
     * Ringing
     */
    public static final boolean RINGING = true;
    private boolean ringing = RINGING;
    /**
     * Locale
     */
    public static Locale LOCALE = DateUtil.US_LOCALE;
    private Locale locale = LOCALE;
    /**
     * System tray
     */
    public static final boolean STRAY = true;
    private boolean systemTray = STRAY;
    /**
     * System tray pop-up message
     */
    public static final boolean STRAYMSG = true;
    private boolean systemTrayMessage = STRAYMSG;
    /**
     * Always on top
     */
    public static final boolean ALWAYS = false;
    private boolean alwaysOnTop = ALWAYS;
    /**
     * Agile mode
     */
    public static final boolean AGILE = false;
    private boolean agileMode = AGILE;
    /**
     * Plain hours
     */
    public static final boolean PLAIN = true;
    private boolean plainHours = PLAIN;

    // GETTERS
    public int getPomodoroLength() {
        return pomodoroLength;
    }

    public int getShortBreakLength() {
        return shortBreakLength;
    }

    public int getLongBreakLength() {
        return longBreakLength;
    }

    public int getMaxNbPomPerActivity() {
        return maxNbPomPerActivity;
    }

    public int getMaxNbPomPerDay() {
        return maxNbPomPerDay;
    }

    public int getNbPomPerSet() {
        return nbPomPerSet;
    }

    public boolean getTicking() {
        return ticking;
    }

    public boolean getRinging() {
        return ringing;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean getSystemTray() {
        return systemTray;
    }

    public boolean getSystemTrayMessage() {
        return systemTrayMessage;
    }

    public boolean getAlwaysOnTop() {
        return alwaysOnTop;
    }

    public boolean getAgileMode() {
        return agileMode;
    }
    
    public boolean getPlainHours() {
        return plainHours;
    }

    // SETTERS
    public void setPomodoroLength(int value) {
        pomodoroLength = value;
    }

    public void setShortBreakLength(int value) {
        shortBreakLength = value;
    }

    public void setLongBreakLength(int value) {
        longBreakLength = value;
    }

    public void setMaxNbPomPerActivity(int value) {
        maxNbPomPerActivity = value;
    }

    public void setMaxNbPomPerDay(int value) {
        maxNbPomPerDay = value;
    }

    public void setNbPomPerSet(int value) {
        nbPomPerSet = value;
    }

    public void setTicking(boolean value) {
        ticking = value;
    }

    public void setRinging(boolean value) {
        ringing = value;
    }

    public void setLocale(Locale value) {
        locale = value;
    }

    public void setSystemTray(boolean value) {
        systemTray = value;
    }

    public void setSystemTrayMessage(boolean value) {
        systemTrayMessage = value;
    }

    public void setAlwaysOnTop(boolean value) {
        alwaysOnTop = value;
    }

    public void setAgileMode(boolean value) {
        agileMode = value;
    }
    
    public void setPlainHours(boolean value) {
        plainHours = value;
    }

    public void refresh() {
        PreferencesDAO.getInstance();
    }

    public void loadPreferences() {
        PreferencesDAO.getInstance().load();
    }

    public void updatePreferences() {
        PreferencesDAO.getInstance().update();
    }
}
