package org.mypomodoro.model;

import java.sql.ResultSet;
import java.util.Date;

import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.util.DateUtil;

/**
 * Activity Objects stores all the required information about tasks in the
 * Pomodoro time management system.
 * 
 * @author Brian Wetzel 
 * @author Phil Karoo
 */
public class Activity {
    // ATTRIBUTES

    /**
     * unique id number for the Activity (to be assigned by database) default is
     * flagged (unset)
     * 
     */
    private int id = -1;
    /**
     * place where the activity is taking occurring set by the constructor
     */
    private String place;
    /**
     * date the activity is entered into database set by Java in constructor
     * (not needed in GUI)
     * 
     */
    private Date date;
    /**
     * name of the author who entered the activity into the database set by the
     * constructor
     */
    private String author;
    /**
     * name of the activity. set by constructor
     * 
     */
    private String name;
    /**
     * description of the activity set by constructor
     */
    private String description;
    /**
     * type of activity / set by constructor
     * 
     */
    private String type;
    /**
     * ToDo priority for this activity default = flag (no priority)
     * 
     */
    private int priority = -1;
    /**
     * estimated pomodoros for this task set by constructor
     */
    private int estimatedPoms = 0;
    /**
     * actual pomodoros for this task default is 0 pomodoros
     * 
     */
    private int actualPoms = 0;
    /**
     * overestimated pomodoros for this task set by constructor
     */
    private int overestimatedPoms = 0;
    /**
     * state of activity. is it unplanned (an interruption)? default is planned
     */
    private boolean isUnplanned = false;
    /**
     * state of activity. is it completed default is incomplete
     */
    private boolean isCompleted = false;
    /**
     * notes on the current activity. default is none
     */
    private String notes = "";
    /**
     * External interruptions
     */
    private int numInterruptions = 0;
    /**
     * Internal interruptions
     */
    private int numInternalInterruptions = 0;

    /**
     * Default Constructor
     */
    public Activity() {
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Constructor for Activity. date attribute is stored as new Date().
     * 
     * @param place
     * @param author
     * @param name
     * @param description
     * @param type
     * @param estimatedPoms
     * @param dateActivity
     */
    public Activity(String place, String author, String name,
            String description, String type, int estimatedPoms, Date dateActivity) {
        this(place, author, name, description, type, estimatedPoms, dateActivity, -1);
    }

    public Activity(String place, String author, String name,
            String description, String type, int estimatedPoms, Date dateActivity, int activityId) {
        this.place = place;
        this.date = new Date();
        this.author = author;
        this.name = name;
        this.description = description;
        this.type = type;
        this.estimatedPoms = estimatedPoms;
        this.date = dateActivity;
        this.id = activityId > 0 ? activityId : this.id;
    }

    public Activity(ResultSet rs) {
        try {
            this.id = rs.getInt("id");
            this.name = rs.getString("name");
            this.type = rs.getString("type");
            this.description = rs.getString("description");
            this.notes = rs.getString("notes");
            this.author = rs.getString("author");
            this.place = rs.getString("place");
            this.date = new Date(rs.getLong("date_added"));
            this.estimatedPoms = rs.getInt("estimated_poms");
            this.actualPoms = rs.getInt("actual_poms");
            this.overestimatedPoms = rs.getInt("overestimated_poms");
            this.isCompleted = Boolean.valueOf(rs.getString("is_complete"));
            this.isUnplanned = Boolean.valueOf(rs.getString("is_unplanned"));
            this.numInterruptions = rs.getInt("num_interruptions");
            this.priority = rs.getInt("priority");
            this.numInternalInterruptions = rs.getInt("num_internal_interruptions");
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    // GETTERS
    public int getActualPoms() {
        return actualPoms;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public int getEstimatedPoms() {
        return estimatedPoms;
    }

    public int getId() {
        return id;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public boolean isUnplanned() {
        return isUnplanned;
    }

    public int getOverestimatedPoms() {
        return overestimatedPoms;
    }

    public String getName() {
        return name;
    }

    public String getNotes() {
        return notes;
    }

    public String getPlace() {
        return place;
    }

    public int getPriority() {
        return priority;
    }

    public String getType() {
        return type;
    }

    public int getNumInterruptions() {
        return numInterruptions;
    }

    public int getNumInternalInterruptions() {
        return numInternalInterruptions;
    }

    // SETTERS
    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public void setIsUnplanned(boolean isUnplanned) {
        this.isUnplanned = isUnplanned;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setEstimatedPoms(int estimatedPoms) {
        this.estimatedPoms = estimatedPoms;
    }

    public void setOverestimatedPoms(int overestimatedPoms) {
        this.overestimatedPoms = overestimatedPoms;
    }

    public void incrementPoms() {
        actualPoms++;
    }

    public void incrementInter() {
        numInterruptions++;
    }

    public void incrementInternalInter() {
        numInternalInterruptions++;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Determines if the activity contains valid data. Stores an ArrayList of
     * the boolean values, if any are false it returns false. This makes it
     * easier to add new validation requirements. This may not be the best way
     * to handle the interdependencies, but you would just create a new
     * expression instead of nesting if statements.
     * 
     * @return true if valid
     */
    public boolean isValid() {
        return !name.isEmpty() && validNumberOfPomodoros() && date != null;
    }

    private boolean validNumberOfPomodoros() {
        return estimatedPoms > 0;
    }

    public void databaseInsert() {
        ActivitiesDAO.getInstance().insert(this);
    }

    public void databaseUpdate() {
        ActivitiesDAO.getInstance().update(this);
    }

    public boolean alreadyExists() {
        return ActivitiesDAO.getInstance().getActivityByNameAndDate(this) != null;
    }

    public boolean isDateToday() {
        String dateActivityFormat = DateUtil.getFormatedDate(getDate());
        String todayFormat = DateUtil.getFormatedDate(new Date());
        return dateActivityFormat.equalsIgnoreCase(todayFormat);
    }

    // Activity (not a ToDo nor a report)
    public boolean isActivity() {
        return getPriority() == -1 && !isCompleted();
    }

    public static Activity getActivity(int Id) {
        return ActivitiesDAO.getInstance().getActivity(Id);
    }

    public String[] toArray() {
        return toArray("dd MM yyyy");
    }

    public String[] toArray(String pattern) {
        String[] attributes = new String[16];
        attributes[0] = isUnplanned ? "1" : "0";
        attributes[1] = DateUtil.getFormatedDate(date, pattern);
        attributes[2] = DateUtil.getFormatedTime(date, pattern); // time
        attributes[3] = name;
        attributes[4] = estimatedPoms + "";
        attributes[5] = overestimatedPoms + "";
        attributes[6] = actualPoms + "";
        attributes[7] = ( actualPoms - estimatedPoms ) + "";
        attributes[8] = overestimatedPoms > 0 ? ( actualPoms - estimatedPoms - overestimatedPoms ) + "" : "";
        attributes[9] = numInternalInterruptions + "";
        attributes[10] = numInterruptions + "";
        attributes[11] = type;
        attributes[12] = author;
        attributes[13] = place;
        attributes[14] = description;
        attributes[15] = notes;
        return attributes;
    }
    
    public Object[] toRowArray() {
        Object[] attributes = new Object[16];
        attributes[0] = isUnplanned ? true : false;
        attributes[1] = date;
        attributes[2] = DateUtil.getFormatedTime(date); // time
        attributes[3] = name;
        attributes[4] = estimatedPoms;
        attributes[5] = overestimatedPoms;
        attributes[6] = actualPoms;
        attributes[7] = actualPoms - estimatedPoms;
        attributes[8] = overestimatedPoms > 0 ? ( actualPoms - estimatedPoms - overestimatedPoms ) : "";
        attributes[9] = numInternalInterruptions;
        attributes[10] = numInterruptions;
        attributes[11] = type;
        attributes[12] = author;
        attributes[13] = place;
        attributes[14] = description;
        attributes[15] = notes;
        return attributes;
    }
}