package org.mypomodoro.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mypomodoro.Main;
import org.mypomodoro.gui.PreferencesPanel;

public class PreferencesDAO {

    private final Database database = Main.database;
    private static final PreferencesDAO instance = new PreferencesDAO();

    public static PreferencesDAO getInstance() {
        return instance;
    }

    PreferencesDAO() {
        database.createPreferencesTable();
    }

    public void load() {
        database.lock();
        try {
            ResultSet rs = database.query("SELECT * FROM preferences;");
            try {
                if (rs.next()) {
                    PreferencesPanel.preferences.setPomodoroLength(rs.getInt("pom_length"));
                    PreferencesPanel.preferences.setShortBreakLength(rs.getInt("short_break_length"));
                    PreferencesPanel.preferences.setLongBreakLength(rs.getInt("long_break_length"));
                    PreferencesPanel.preferences.setMaxNbPomPerActivity(rs.getInt("max_nb_pom_per_activity"));
                    PreferencesPanel.preferences.setMaxNbPomPerDay(rs.getInt("max_nb_pom_per_day"));
                    PreferencesPanel.preferences.setNbPomPerSet(rs.getInt("nb_pom_per_set"));
                    PreferencesPanel.preferences.setTicking(rs.getInt("ticking") == 1);
                    PreferencesPanel.preferences.setRinging(rs.getInt("ringing") == 1);
                    String locale = rs.getString("locale");
                    String regularExpression = "[a-z]{2}_[A-Z]{2}_[a-zA-Z]+"; // locale with variant        
                    Pattern pat = Pattern.compile(regularExpression);
                    Matcher mat = pat.matcher(locale);
                    if (mat.find()) {
                        PreferencesPanel.preferences.setLocale(new Locale(locale.substring(0, 2), locale.substring(3, 5), locale.substring(6)));
                    } else {
                        regularExpression = "[a-z]{2}_[A-Z]{2}"; // locale without variant
                        pat = Pattern.compile(regularExpression);
                        mat = pat.matcher(locale);
                        if (mat.find()) {
                            PreferencesPanel.preferences.setLocale(new Locale(locale.substring(0, 2), locale.substring(3, 5)));
                        }
                    }
                    PreferencesPanel.preferences.setSystemTray(rs.getInt("system_tray") == 1);
                    PreferencesPanel.preferences.setSystemTrayMessage(rs.getInt("system_tray_msg") == 1);
                    PreferencesPanel.preferences.setAlwaysOnTop(rs.getInt("always_on_top") == 1);
                    PreferencesPanel.preferences.setAgileMode(rs.getInt("agile_mode") == 1);
                    PreferencesPanel.preferences.setPlainHours(rs.getInt("plain_hours") == 1);
                }
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        } finally {
            database.unlock();
        }
    }

    public void update() {
        String updateSQL = "UPDATE preferences SET "
                + "pom_length = " + PreferencesPanel.preferences.getPomodoroLength() + ", "
                + "short_break_length = " + PreferencesPanel.preferences.getShortBreakLength() + ", "
                + "long_break_length = " + PreferencesPanel.preferences.getLongBreakLength() + ", "
                + "max_nb_pom_per_activity = " + PreferencesPanel.preferences.getMaxNbPomPerActivity() + ", "
                + "max_nb_pom_per_day = " + PreferencesPanel.preferences.getMaxNbPomPerDay() + ", "
                + "nb_pom_per_set = " + PreferencesPanel.preferences.getNbPomPerSet() + ", "
                + "ticking = " + (PreferencesPanel.preferences.getTicking() ? 1 : 0) + ", "
                + "ringing = " + (PreferencesPanel.preferences.getRinging() ? 1 : 0) + ", "
                + "locale = '" + PreferencesPanel.preferences.getLocale().toString() + "'" + ", "
                + "system_tray = " + (PreferencesPanel.preferences.getSystemTray() ? 1 : 0) + ", "
                + "system_tray_msg = " + (PreferencesPanel.preferences.getSystemTrayMessage() ? 1 : 0) + ", "
                + "always_on_top = " + (PreferencesPanel.preferences.getAlwaysOnTop() ? 1 : 0) + ", "
                + "agile_mode = " + (PreferencesPanel.preferences.getAgileMode() ? 1 : 0) + ", "
                + "plain_hours = " + (PreferencesPanel.preferences.getPlainHours() ? 1 : 0) + ";";
        database.lock();
        try {
            database.update("begin;");
            database.update(updateSQL);
            database.update("commit;");
        } finally {
            database.unlock();
        }
    }
}
