package mypomodoro;

import java.io.Serializable;
import java.util.Date;
import java.sql.*;

/**
 * Activity Objects store all the required information about tasks in the
 * Pomodoro time management system.
 * @author Brian Wetzel
 */
public class Activity implements Serializable
{
    //ATTRIBUTES
    //unique id number for the Activity (to be assigned by database)
    //default is flagged (unset)

    private int id = -1;
    //place where the activity is taking ocurring
    //set by the constructor
    private String place;
    //date the activity is entered into database
    //set by Java in constructor (not needed in GUI)
    private java.util.Date date;
    //name of the author who entered the activity into the database
    //set by the constructor
    private String author;
    //name of the activity.
    //set by constructor
    private String name;
    //description of the activity
    //set by constructor
    private String description;
    //type of activity
    //set by constructor
    private String type;
    //ToDo priority for this activity
    //default = flag (no priority)
    private int priority = -1;
    //estimated pomodoros for this task
    //set by constructor
    private int estimatedPoms;
    //actual pomodoros for this task
    //default is 0 pomodoros
    private int actualPoms = 0;
    //state of activity.  has it been voided?
    //default is not voided.
    private boolean isVoided = false;
    //state of activity.  is it unplanned (an interruption)?
    //default is planned
    private boolean isUnplanned = false;
    //state of activity. is it completed
    //default is incomplete
    private boolean isCompleted = false;
    //notes on the current activity.
    //default is none
    private String notes = "";
    //interruptions that occured during this task
    //private ActivityCollection interruptions = new ActivityCollection();
    //number of interruptions
    private int numInterruptions = 0;
    //Maximim number of pomodoros for an activity
    public static final int MAX_POMODOROS = 10;

    /**
     * Default Constructor
     */
    public Activity()
    {
    }

    /**
     * Constructor for Activity.  date attribute is stored as new Date().
     * 
     * @param place
     * @param author
     * @param name
     * @param description
     * @param type
     * @param estimatedPoms
     */
    public Activity(String place, String author,
            String name, String description, String type, int estimatedPoms)
    {
        this.place = place;
        this.date = new java.util.Date();
        this.author = author;
        this.name = name;
        this.description = description;
        this.type = type;
        this.estimatedPoms = estimatedPoms;
    }

    public Activity(ResultSet rs){
        try {
          this.id = rs.getInt("id");
          this.name = rs.getString("name");
          this.type = rs.getString("type");
          this.description = rs.getString("description");
          this.notes = rs.getString("notes");
          this.author = rs.getString("author");
          this.place = rs.getString("place");
          this.date = new java.util.Date(rs.getLong("date_added"));
          this.estimatedPoms = rs.getInt("estimated_poms");
          this.actualPoms = rs.getInt("actual_poms");
          this.isVoided = Boolean.valueOf(rs.getString("is_void"));
          this.isCompleted = Boolean.valueOf(rs.getString("is_complete"));
          this.isUnplanned = Boolean.valueOf(rs.getString("is_unplanned"));
          this.numInterruptions = rs.getInt("num_interruptions");
          this.priority = rs.getInt("priority");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    

    //GETTERS
    public int getActualPoms()
    {
        return actualPoms;
    }

    public String getAuthor()
    {
        return author;
    }

    public Date getDate()
    {
        return date;
    }

    public String getDescription()
    {
        return description;
    }

    public int getEstimatedPoms()
    {
        return estimatedPoms;
    }

    public int getId()
    {
        return id;
    }

    public boolean isCompleted()
    {
        return isCompleted;
    }

    public boolean isUnplanned()
    {
        return isUnplanned;
    }

    public boolean isVoided()
    {
        return isVoided;
    }

    public String getName()
    {
        return name;
    }

    public String getNotes()
    {
        return notes;
    }

    public String getPlace()
    {
        return place;
    }

    public int getPriority()
    {
        return priority;
    }

    public String getType()
    {
        return type;
    }

    /*
    public ActivityCollection getInterruptions()
    {
        return interruptions;
    }
     */

    public int getNumInterruptions()
    {
        return numInterruptions;
    }

    //SETTERS
    public void setActualPoms(int actualPoms)
    {
        this.actualPoms = actualPoms;
        databaseUpdate();
    }

    public void setIsCompleted(boolean isCompleted)
    {
        this.isCompleted = isCompleted;
        databaseUpdate();
    }

    public void setIsUnplanned(boolean isUnplanned)
    {
        this.isUnplanned = isUnplanned;
        databaseUpdate();
    }

    public void setIsVoided(boolean isVoided)
    {
        this.isVoided = isVoided;
        databaseUpdate();
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
        databaseUpdate();
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
        databaseUpdate();
    }

    public void incrementPoms()
    {
        actualPoms++;
        databaseUpdate();
    }

    public void incrementInter()
    {
        numInterruptions++;
        databaseUpdate();
    }

    /**
     * Determines if the activity contains valid data.  Stores an ArrayList of
     * the boolean values, if any are false it returns false.  This makes it
     * easier to add new validation requirements.  This may not be the best way
     * to handle the interdependencies, but you would just create a new
     * expression instead of nesting if statements.
     * @return
     */
    public boolean isValid()
    {
        java.util.ArrayList<Boolean> validationList =
                new java.util.ArrayList<Boolean>();
        validationList.add(!this.place.isEmpty());
        validationList.add(!this.author.isEmpty());
        validationList.add(!this.name.isEmpty());
        validationList.add(!this.description.isEmpty());
        validationList.add(!this.type.isEmpty());
        validationList.add(
                (this.estimatedPoms > 0 && this.estimatedPoms <= MAX_POMODOROS));
        for(Boolean b : validationList)
        {
            if (!b) return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        String str = name;
        return str;
    }


    //when this code is refactored this should all go in the database class
    public void databaseInsert() {
        new Thread(new Runnable(){
            public void run(){
                String insertSQL = "INSERT INTO activities VALUES ( "
                    + "NULL, "
                    + "'" + name + "', "
                    + "'" + type + "', "
                    + "'" + description + "', "
                    + "'" + notes + "', "
                    + "'" + author + "', "
                    + "'" + place + "', "
                    + date.getTime() + ", "
                    + estimatedPoms + ", "
                    + actualPoms + ", "
                    + "'" + Boolean.valueOf(isVoided).toString() + "', "
                    + "'" + Boolean.valueOf(isCompleted).toString() + "', "
                    + "'" + Boolean.valueOf(isUnplanned).toString() + "', "
                    + numInterruptions + ", "
                    + priority
                    + ");";
                Main.datalock.lock();
                try
                {
                    Main.db.update("begin;");
                    Main.db.update(insertSQL);
                    Main.db.update("commit;");

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

    private void databaseUpdate() {
        new Thread(new Runnable(){
            public void run(){
                String updateSQL = "UPDATE activities SET "
                    + "name = '" + name + "', "
                    + "type = '" + type + "', "
                    + "description = '" + description + "', "
                    + "notes = '" + notes + "', "
                    + "author = '" + author + "', "
                    + "place = '" + place + "', "
                    + "date_added = " + date.getTime() + ", "
                    + "estimated_poms = " + estimatedPoms + ", "
                    + "actual_poms = " + actualPoms + ", "
                    + "is_void = '" + Boolean.valueOf(isVoided).toString() + "', "
                    + "is_complete = '" + Boolean.valueOf(isCompleted).toString() + "', "
                    + "is_unplanned = '" + Boolean.valueOf(isUnplanned).toString() + "', "
                    + "num_interruptions = " + numInterruptions + ", "
                    + "priority = " + priority
                    + " WHERE id = " + id + ";";

                //Database db = new Database();

                Main.datalock.lock();
                try
                {
                    Main.db.update("begin;");
                    Main.db.update(updateSQL);
                    Main.db.update("commit;");

                }
                finally
                {
                    Main.datalock.unlock();
                }

            }
        }).start();

    }
}
