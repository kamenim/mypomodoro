package org.mypomodoro.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;

import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;

/**
 * 
 * @author Jordan 
 * @author Phil Karoo
 */
public class Database {

    private Connection connection = null;
    private Statement statement = null;

    public Database() {
        try {
            // The driver can be found in src\main\resources\org\sqlite along with the database
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:pomodoro.db");
            statement = connection.createStatement();
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void close() {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        }
        catch (SQLException e) {
            System.err.println(e);
        }
    }

    public void update(String sql) {

        try {
            statement.executeUpdate(sql);
        }
        catch (SQLException e) {
            System.err.println(e);
        }
    }

    public ResultSet query(String sql) {
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(sql);
        }
        catch (Exception e) {
            System.err.println(e);
        }

        return rs;
    }

    public void init() {
        createActivitiesTable();
        createPreferencesTable();
    }

    public void createActivitiesTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS activities ( "
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "name TEXT, "
                + "type TEXT, " + "description TEXT, " + "notes TEXT, "
                + "author TEXT, " + "place TEXT, " + "date_added INTEGER, "
                + "estimated_poms INTEGER, " + "actual_poms INTEGER, "
                + "overestimated_poms INTEGER, " + "is_complete TEXT, "
                + "is_unplanned TEXT, " + "num_interruptions INTEGER, "
                + "priority INTEGER, "
                + "num_internal_interruptions INTEGER" + ");";
        update(createTableSQL);
    }

    public void createPreferencesTable() {
        String createPreferencesTableSQL = "CREATE TABLE IF NOT EXISTS preferences ( "
                + "pom_length INTEGER DEFAULT 25, "
                + "short_break_length INTEGER DEFAULT 5, "
                + "long_break_length INTEGER DEFAULT 20, "
                + "max_nb_pom_per_activity INTEGER DEFAULT 5, "
                + "max_nb_pom_per_day INTEGER DEFAULT 10, "
                + "nb_pom_per_set INTEGER DEFAULT 4, "
                + "ticking BOOLEAN DEFAULT 1, "
                + "ringing BOOLEAN DEFAULT 1, "
                + "locale TEXT, "
                + "system_tray BOOLEAN DEFAULT 1, "
                + "system_tray_msg BOOLEAN DEFAULT 1, "
                + "always_on_top BOOLEAN DEFAULT 0" + ");";
        update(createPreferencesTableSQL);
        initPreferencesTable();
    }

    private void initPreferencesTable() {
        String selectPreferencesSQL = "SELECT * FROM preferences;";
        ResultSet rs = query(selectPreferencesSQL);
        try {
            if (rs != null && !rs.next()) { // make sure there is no row in the result set
                String insertPreferencesSQL = "INSERT INTO preferences ("
                        + "pom_length,short_break_length,long_break_length,"
                        + "max_nb_pom_per_activity,max_nb_pom_per_day,nb_pom_per_set,"
                        + "ticking,ringing,locale,system_tray,system_tray_msg,always_on_top) "
                        + "VALUES ("
                        + "25,5,20,5,10,4,1,1,'en_US',1,1,0);";
                update(insertPreferencesSQL);
            }
        }
        catch (SQLException e) {
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

    public void resetData() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                update("begin;");
                update("DELETE from activities;");
                update("commit;");
                Main.updateLists();
                Main.updateView();
            }
        });

    }

    public static void main(String[] args) throws Exception {
        Database db = new Database();
        db.close();
        System.out.println("to_do:");
        ToDoList list = ToDoList.getList();
        for (Activity a : list) {
            System.out.println(a);
        }

        System.out.println("act:");
        ActivityList list2 = ActivityList.getList();
        for (Activity a : list2) {
            System.out.println(a);
        }

        System.out.println("reports:");
        Iterator<Activity> iterator = ReportList.getList().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
    private final ReentrantLock lock = new ReentrantLock();

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}