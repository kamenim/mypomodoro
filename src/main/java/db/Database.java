package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;

/**
 * 
 * @author Jordan
 */
public class Database {
	private Connection connection = null;
	private Statement statement = null;

	public Database() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:pomodoro.db");
			statement = connection.createStatement();			
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}

	public void update(String sql) {

		try {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet query(String sql) {
		ResultSet rs = null;

		try {
			rs = statement.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
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
				+ "is_void TEXT, " + "is_complete TEXT, "
				+ "is_unplanned TEXT, " + "num_interruptions INTEGER, "
				+ "priority INTEGER" + ");";
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
				+ "ringing BOOLEAN DEFAULT 1" + ");";
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
                        + "ticking,ringing) "
                        + "VALUES ("
                        + "25,5,20,5,10,4,1,1);";
                update(insertPreferencesSQL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

	public void resetData() {
		new Thread(new Runnable() {
            @Override
			public void run() {
				update("begin;");
				update("DELETE from activities;");
				update("commit;");                
				Main.updateLists();
				Main.updateView();
			}
		}).start();

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
