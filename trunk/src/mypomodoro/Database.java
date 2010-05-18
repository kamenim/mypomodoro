package mypomodoro;
import java.sql.*;

/**
 *
 * @author Jordan
 */
public class Database {
    private Connection connection = null;
    private Statement statement = null;

    public Database(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:pomodoro.db");
            statement = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(String sql) {
        try {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            update(sql);
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
    

    public void initialize() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS activities ( "
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "name TEXT, "
            + "type TEXT, "
            + "description TEXT, "
            + "notes TEXT, "
            + "author TEXT, "
            + "place TEXT, "
            + "date_added INTEGER, "
            + "estimated_poms INTEGER, "
            + "actual_poms INTEGER, "
            + "is_void TEXT, "
            + "is_complete TEXT, "
            + "is_unplanned TEXT, "
            + "num_interruptions INTEGER, "
            + "priority INTEGER"
            + ");";
       update(createTableSQL);
    }

    public void createTestData()
    {
        new Thread(new Runnable(){

          public void run() {
                String[] authors = {"Brian", "Paul", "Bobby", "Jordan", "Rick"};
                String[] place = {"GGC", "School", "Work", "Home", "Atlanta", "Chicago", "Seattle", "Boston", "Baltimore", "Philadelphia", "Los Angeles", "New York"};
                String[] name = {"Write SD Project Essay", "Finish Packaging Application", "Finish Application", "Complete Testing"};
                String[] description = {"Address software project development," +
                        " expected issues, potential alternatives, risk management " +
                        "and implementation and testing strategies.",
                        "Combine all jar files into a single executable jar",
                        "Post all source and executables on Google Project Hosting",
                        "Resolve most of the known bugs.",
                        "Preform manual testing of GUI"};
                String[] type = {"Homework", "Work", "Testing", "Programming", "Distribution"};
                java.util.Random rand = new java.util.Random();
                int alSize = 10;

                //insert data into the activitylist
                for(int i = 0; i < alSize; i++){

                   Activity a = new Activity(place[rand.nextInt(12)], authors[rand.nextInt(5)],
                           name[rand.nextInt(4)], description[rand.nextInt(5)],
                           type[rand.nextInt(5)], rand.nextInt(10));
                   a.databaseInsert();
                }                
           }
        }).start();
    }

    public void resetData()
    {
        new Thread(new Runnable(){
           public void run()
            {
                update("begin;");
                update("DELETE from activities;");
                update("commit;");
                ActivityList.getList().clear();
                ActivityList.getList().RefreshActivityList();
                ToDoList.getList().clear();
                ToDoList.getList().refreshList();
                ReportList.getList().clear();
                ReportList.getList().refreshList();
                Main.updateView();
            }
        }).start();
        
    }

    public static void main(String[] args) throws Exception
    {
        Database db = new Database();
        db.initialize();
        db.close();

        System.out.println("to_do:");
        ToDoList list = ToDoList.getList();
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
        }

        System.out.println("act:");
        ActivityList list2 = ActivityList.getList();
        for (int i = 0; i < list2.size(); i++) {
            System.out.println(list2.get(i).toString());
        }

        System.out.println("reports:");
        ReportList list3 = ReportList.getList();
        for (int i = 0; i < list3.size(); i++) {
            System.out.println(list3.get(i).toString());
        }
    }
}
