package mypomodoro;
import java.sql.*;
import java.util.LinkedList;

/**
 *
 * @author Brian Wetzel
 */
public class ToDoList extends LinkedList<Activity>
{
    //cannot add more pomodoros to a list than can be completed in 24hrs.
    //this is based on the estimated pomodoros.
    private static final int MAXIMUM_POMS = 40;
    
    //singleton object
    private static ToDoList list = null;

    //constructor is private, use getList method
    private ToDoList(){
        refreshList();
    }

    public void refreshList()
    {
        Main.datalock.lock();
        try
        {
            ResultSet rs = Main.db.query("SELECT * FROM activities WHERE priority > -1 AND is_complete = 'false' ORDER BY priority DESC;");
            try {
                while (rs.next()) { this.add(new Activity(rs)); }
                rs.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
        finally
        {
            Main.datalock.unlock();
        }
    }

    public static ToDoList getList() {
      if (list == null)
          list = new ToDoList();
      return list;
    }

    public void addActivity(Activity act) {
        this.add(act);
        act.setPriority(this.size() - 1);
    }

    public Activity removeActivity(Activity a) {
        
        int index = this.indexOf(a);
        this.remove(a);
        a.setPriority(-1);

        for (int j = index; j < this.size(); j++){
            Activity currentAct = this.get(j);
            currentAct.setPriority(j);
        }
        
        return a;
    }

    /**
     * Promote the Activity at the desired index, by storing the value to be
     * replaced into a buffer and then switching the values.  The root node
     * cannote be changed.
     *
     * @param index
     */
    public void promote(Activity a)
    {
        int index = this.indexOf(a);
        if( index > 0)
        {
            Activity lower = a;
            Activity higher = this.get(index - 1);
            this.set(index - 1, lower);
            this.set(index, higher);
            lower.setPriority(index - 1);
            higher.setPriority(index);
        }
    }

    /**
     * Demote the Activity at the desired index, by storing the value to be
     * replaced into a buffer adn then switching the values.  The root node
     * cannot be changed.
     * 
     * @param index
     */
    public void demote(int index)
    {
        if(index < (this.size()-1))
        {
            Activity higher = this.get(index);
            Activity lower = this.get(index + 1);
            this.set(index, lower);
            this.set(index + 1, higher);
            lower.setPriority(index);
            higher.setPriority(index + 1);
        }
    }

    public void complete() {
        Activity activity = this.removeFirst();
        activity.setIsCompleted(true);

        for (int j = 0; j < (this.size()-1); j++){
            Activity currentAct = this.get(j);
            currentAct.setPriority(j);
        }

        ReportList.getList().add(activity);
    }

}
