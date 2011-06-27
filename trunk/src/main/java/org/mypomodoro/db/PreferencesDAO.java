package org.mypomodoro.db;

import java.sql.ResultSet;
import java.sql.SQLException;

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
                ControlPanel.preferences.setPomodoroLength(rs.getInt("pom_length"));
                ControlPanel.preferences.setShortBreakLength(rs.getInt("short_break_length"));
                ControlPanel.preferences.setLongBreakLength(rs.getInt("long_break_length"));
                ControlPanel.preferences.setMaxNbPomPerActivity(rs.getInt("max_nb_pom_per_activity"));
                ControlPanel.preferences.setMaxNbPomPerDay(rs.getInt("max_nb_pom_per_day"));
                ControlPanel.preferences.setNbPomPerSet(rs.getInt("nb_pom_per_set"));
                ControlPanel.preferences.setTicking(rs.getInt("ticking") == 1 ? true : false);
                ControlPanel.preferences.setRinging(rs.getInt("ringing") == 1 ? true : false);
                ControlPanel.preferences.setLocale(rs.getString("locale"));
            }
            catch (Exception e) {
                System.err.println(e);
            }
            finally {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    System.err.println(e);
                }
            }
        }
        finally {
            database.unlock();
        }
    }

    public void update() {
        String updateSQL = "UPDATE preferences SET "
                + "pom_length = " + ControlPanel.preferences.getPomodoroLength()
                + ", " + "short_break_length = " + ControlPanel.preferences.getShortBreakLength()
                + ", " + "long_break_length = " + ControlPanel.preferences.getLongBreakLength()
                + ", " + "max_nb_pom_per_activity = " + ControlPanel.preferences.getMaxNbPomPerActivity()
                + ", " + "max_nb_pom_per_day = " + ControlPanel.preferences.getMaxNbPomPerDay()
                + ", " + "nb_pom_per_set = " + ControlPanel.preferences.getNbPomPerSet()
                + ", " + "ticking = " + ( ControlPanel.preferences.getTicking() ? 1 : 0 )
                + ", " + "ringing = " + ( ControlPanel.preferences.getRinging() ? 1 : 0 ) + ";"; 
                //+ ", " + "locale = " + ControlPanel.preferences.getLocale() + ";";
        database.lock();
        try {
            database.update("begin;");
            database.update(updateSQL);
            database.update("commit;");
        }
        finally {
            database.unlock();
        }
    }
}