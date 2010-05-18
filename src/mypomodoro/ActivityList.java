package mypomodoro;
import java.sql.*;
import java.util.LinkedList;

/**
 *
 * @author Brian Wetzel
 */
public class ActivityList extends LinkedList<Activity>
{
    //singleton object
    private static ActivityList list = null;
    private Database db;
    //constructor is private, use getList method
    private ActivityList()
    {
        RefreshActivityList();
    }

    protected void RefreshActivityList(){
        Main.datalock.lock();
        try
        {
           ResultSet rs = Main.db.query("SELECT * FROM activities " +
                "WHERE priority = -1 AND is_complete = 'false';");

            try {
                int i = 0;
                while (rs.next()) { this.add(new Activity(rs)); System.out.println("added"+i++);}
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        finally
        {
            Main.datalock.unlock();
        }
    }

    public static ActivityList getList() {
      if (list == null)
          list = new ActivityList();
      return list;
    }

    public void removeById(int id1)
    {
        final int id = id1;
        try
        {
            new Thread(new Runnable(){
                public void run(){
                    Main.datalock.lock();
                    try
                    {
                        Main.db.update("begin;");
                        Main.db.update("DELETE FROM activities WHERE id=" + id + ";");
                        Main.db.update("Commit;");
                    }
                    finally
                    {
                        Main.datalock.unlock();
                        Main.updateLists();
                        Main.updateView();
                    }
                }
            }).start();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
