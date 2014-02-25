package org.mypomodoro.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mypomodoro.Main;
import org.mypomodoro.gui.ControlPanel;

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
                    ControlPanel.preferences.setPomodoroLength(rs.getInt("pom_length"));
                    ControlPanel.preferences.setShortBreakLength(rs.getInt("short_break_length"));
                    ControlPanel.preferences.setLongBreakLength(rs.getInt("long_break_length"));
                    ControlPanel.preferences.setMaxNbPomPerActivity(rs.getInt("max_nb_pom_per_activity"));
                    ControlPanel.preferences.setMaxNbPomPerDay(rs.getInt("max_nb_pom_per_day"));
                    ControlPanel.preferences.setNbPomPerSet(rs.getInt("nb_pom_per_set"));
                    ControlPanel.preferences.setTicking(rs.getInt("ticking") == 1);
                    ControlPanel.preferences.setRinging(rs.getInt("ringing") == 1);
                    String locale = rs.getString("locale");
                    String regularExpression = "[a-z]{2}_[A-Z]{2}_[a-zA-Z]+"; // locale with variant        
                    Pattern pat = Pattern.compile(regularExpression);
                    Matcher mat = pat.matcher(locale);
                    if (mat.find()) {
                        ControlPanel.preferences.setLocale(new Locale(locale.substring(0, 2), locale.substring(3, 5), locale.substring(6)));
                    } else {
                        regularExpression = "[a-z]{2}_[A-Z]{2}"; // locale without variant
                        pat = Pattern.compile(regularExpression);
                        mat = pat.matcher(locale);
                        if (mat.find()) {
                            ControlPanel.preferences.setLocale(new Locale(locale.substring(0, 2), locale.substring(3, 5)));
                        }
                    }
                    ControlPanel.preferences.setSystemTray(rs.getInt("system_tray") == 1);
                    ControlPanel.preferences.setSystemTrayMessage(rs.getInt("system_tray_msg") == 1);
                    ControlPanel.preferences.setAlwaysOnTop(rs.getInt("always_on_top") == 1);
                    ControlPanel.preferences.setAgileMode(rs.getInt("agile_mode") == 1);
                }
            } catch (Exception e) {
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
                + "pom_length = " + ControlPanel.preferences.getPomodoroLength() + ", "
                + "short_break_length = " + ControlPanel.preferences.getShortBreakLength() + ", "
                + "long_break_length = " + ControlPanel.preferences.getLongBreakLength() + ", "
                + "max_nb_pom_per_activity = " + ControlPanel.preferences.getMaxNbPomPerActivity() + ", "
                + "max_nb_pom_per_day = " + ControlPanel.preferences.getMaxNbPomPerDay() + ", "
                + "nb_pom_per_set = " + ControlPanel.preferences.getNbPomPerSet() + ", "
                + "ticking = " + (ControlPanel.preferences.getTicking() ? 1 : 0) + ", "
                + "ringing = " + (ControlPanel.preferences.getRinging() ? 1 : 0) + ", "
                + "locale = '" + ControlPanel.preferences.getLocale().toString() + "'" + ", "
                + "system_tray = " + (ControlPanel.preferences.getSystemTray() ? 1 : 0) + ", "
                + "system_tray_msg = " + (ControlPanel.preferences.getSystemTrayMessage() ? 1 : 0) + ", "
                + "always_on_top = " + (ControlPanel.preferences.getAlwaysOnTop() ? 1 : 0) + ", "
                + "agile_mode = " + (ControlPanel.preferences.getAgileMode() ? 1 : 0) + ";";
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
