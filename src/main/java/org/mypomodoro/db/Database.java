package org.mypomodoro.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;

import org.mypomodoro.Main;
import org.mypomodoro.db.mysql.MySQLConfigLoader;

/**
 * Database
 *
 */
public class Database {

    private Connection connection = null;
    private Statement statement = null;
    private String driverClassName = "org.sqlite.JDBC";
    private String connectionStatement = "jdbc:sqlite:pomodoro.db";
    final public static String SQLLITE = "SQLLITE";
    final public static String MYSQL = "MYSQL";
    // Database specific
    private String autoIncrementKeyword = "AUTOINCREMENT";
    private String longInteger = "INTEGER";
    public String selectStatementSeqId = "SELECT seq FROM sqlite_sequence WHERE name = 'activities'";
    public String sequenceIdName = "seq";

    /*
     Postgresql
     autoIncrementKeyword = "???";
     longInteger = "???";
     selectStatementSeqId = "SELECT CURRVAL(pg_get_serial_sequence('activities','id'))";
     sequenceIdName = "pg_get_serial_sequence";
     */
    public Database() {
        try {
            MySQLConfigLoader.loadProperties();
            if (MySQLConfigLoader.isValid()) {
                driverClassName = "com.mysql.jdbc.Driver";
                connectionStatement = "jdbc:mysql://" + MySQLConfigLoader.getHost() + "/" + MySQLConfigLoader.getDatabase() + "?"
                        + "user=" + MySQLConfigLoader.getUser() + "&password=" + MySQLConfigLoader.getPassword();
                // Database specific
                autoIncrementKeyword = "AUTO_INCREMENT";
                longInteger = "BIGINT";
                selectStatementSeqId = "SELECT LAST_INSERT_ID()";
                sequenceIdName = "last_insert_id()";                
            }
        } catch (IOException ex) {
            // do nothing
        }
        // Connect to database
        try {
            Class.forName(driverClassName);
            connection = DriverManager.getConnection(connectionStatement);
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public void update(String sql) {

        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println(e);
        }
    }

    public ResultSet query(String sql) {
        ResultSet rs = null;

        try {
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
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
                + "id INTEGER PRIMARY KEY " + autoIncrementKeyword + ", "
                + "name TEXT, "
                + "type TEXT, " + "description TEXT, "
                + "notes TEXT, "
                + "author TEXT, " + "place TEXT, "
                + "date_added " + longInteger + ", "
                + "date_completed " + longInteger + ", "
                + "estimated_poms INTEGER, "
                + "actual_poms INTEGER, "
                + "overestimated_poms INTEGER, "
                + "is_complete TEXT, "
                + "is_unplanned TEXT, "
                + "num_interruptions INTEGER, "
                + "priority INTEGER, "
                + "num_internal_interruptions INTEGER, "
                + "story_points FLOAT, "
                + "iteration INTEGER, "
                + "parent_id INTEGER" + ");";
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
                + "always_on_top BOOLEAN DEFAULT 0, "
                + "agile_mode BOOLEAN DEFAULT 0" + ");";
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
                        + "ticking,ringing,locale,system_tray,system_tray_msg,always_on_top,agile_mode) "
                        + "VALUES ("
                        + "25,5,20,5,10,4,1,1,'en_US',1,1,0,0);";
                update(insertPreferencesSQL);
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

    private final ReentrantLock lock = new ReentrantLock();

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
