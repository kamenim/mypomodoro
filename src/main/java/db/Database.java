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
			createTables();
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
			System.out.println(sql);
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

	private void createTables() {
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

	public void createTestData() {
		new Thread(new Runnable() {

			public void run() {
				String[] authors = { "Brian", "Paul", "Bobby", "Jordan", "Rick" };
				String[] place = { "GGC", "School", "Work", "Home", "Atlanta",
						"Chicago", "Seattle", "Boston", "Baltimore",
						"Philadelphia", "Los Angeles", "New York" };
				String[] name = { "Write SD Project Essay",
						"Finish Packaging Application", "Finish Application",
						"Complete Testing" };
				String[] description = {
						"Address software project development,"
								+ " expected issues, potential alternatives, risk management "
								+ "and implementation and testing strategies.",
						"Combine all jar files into a single executable jar",
						"Post all source and executables on Google Project Hosting",
						"Resolve most of the known bugs.",
						"Preform manual testing of GUI" };
				String[] type = { "Homework", "Work", "Testing", "Programming",
						"Distribution" };
				java.util.Random rand = new java.util.Random();
				int alSize = 10;

				// insert data into the activitylist
				for (int i = 0; i < alSize; i++) {

					Activity a = new Activity(place[rand.nextInt(12)],
							authors[rand.nextInt(5)], name[rand.nextInt(4)],
							description[rand.nextInt(5)],
							type[rand.nextInt(5)], rand.nextInt(10));
					a.databaseInsert();
				}
			}
		}).start();
	}

	public void resetData() {
		new Thread(new Runnable() {
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
