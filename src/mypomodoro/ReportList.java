package mypomodoro;
import java.sql.*;
import java.util.LinkedList;

/**
 *
 * @author Brian Wetzel
 */
public class ReportList extends LinkedList<Activity>
{
    //singleton object
    private static ReportList list = null;

    //constructor is private, use getList method
    private ReportList(){
        refreshList();
    }

    public void refreshList()
    {
        Main.datalock.lock();
        try
        {
                ResultSet rs = Main.db.query("SELECT * FROM activities WHERE is_complete = 'true';");
            try {
                while (rs.next()) { this.add(new Activity(rs)); }
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

    public static ReportList getList() {
      if (list == null)
          list = new ReportList();
      return list;
    }
}
