package org.mypomodoro.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.create.list.AuthorList;
import org.mypomodoro.gui.create.list.PlaceList;
import org.mypomodoro.gui.create.list.TypeList;
import org.mypomodoro.util.DateUtil;

/**
 * Activity Objects stores all the required information about tasks in the
 * Pomodoro time management system.
 *
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
     * Story points
     */
    private float storyPoints = 0;
    /**
     * Iteration
     */
    private int iteration = 0;

    /**
     * Parent Id
     */
    private int parentId = -1;

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
     * Constructor for Activity
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
        this.author = author;
        this.name = name;
        this.description = description;
        this.type = type;
        this.estimatedPoms = estimatedPoms;
        this.date = dateActivity;
        this.id = activityId > 0 ? activityId : this.id;
    }

    /**
     * Constructor for Activity
     *
     * @param place
     * @param author
     * @param name
     * @param description
     * @param type
     * @param estimatedPoms
     * @param dateActivity
     * @param overestimatedPoms
     * @param actualPoms (real)
     * @param internalInterruptions
     * @param externalInterruptions
     * @param notes (comment)
     * @param unplanned
     * @param completed
     */
    public Activity(String place, String author, String name,
            String description, String type, int estimatedPoms,
            Date dateActivity, int overestimatedPoms, int actualPoms,
            int internalInterruptions, int externalInterruptions, String notes,
            boolean unplanned, boolean completed) {
        this(place, author, name, description, type, estimatedPoms,
                dateActivity, overestimatedPoms, actualPoms,
                internalInterruptions, externalInterruptions, notes,
                unplanned, completed, -1);
    }

    public Activity(String place, String author, String name,
            String description, String type, int estimatedPoms,
            Date dateActivity, int overestimatedPoms, int actualPoms,
            int internalInterruptions, int externalInterruptions, String notes,
            boolean unplanned, boolean completed, int activityId) {
        this.place = place;
        this.author = author;
        this.name = name;
        this.description = description;
        this.type = type;
        this.estimatedPoms = estimatedPoms;
        this.date = dateActivity;
        this.overestimatedPoms = overestimatedPoms;
        this.actualPoms = actualPoms;
        this.numInternalInterruptions = internalInterruptions;
        this.numInterruptions = externalInterruptions;
        this.notes = notes;
        this.isUnplanned = unplanned;
        this.isCompleted = completed;
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
            this.storyPoints = rs.getFloat("story_points");
            this.iteration = rs.getInt("iteration");
            this.parentId = rs.getInt("parent_id");
        } catch (SQLException e) {
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

    public float getStoryPoints() {
        return storyPoints;
    }

    public int getIteration() {
        return iteration;
    }

    public int getParentId() {
        return parentId;
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

    public void setActualPoms(int actualPoms) {
        this.actualPoms = actualPoms;
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

    public void setStoryPoints(float storyPoints) {
        this.storyPoints = storyPoints;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
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
        // update lists
        TypeList.addType(getType());
        AuthorList.addAuthor(getAuthor());
        PlaceList.addPlace(getPlace());
    }

    public void databaseUpdate() {
        ActivitiesDAO.getInstance().update(this);
        // update lists
        TypeList.addType(getType());
        AuthorList.addAuthor(getAuthor());
        PlaceList.addPlace(getPlace());

    }

    public boolean alreadyExists() {
        return ActivitiesDAO.getInstance().getActivityByNameAndDate(this) != null;
    }

    public boolean isDateToday() {
        String dateActivityFormat = DateUtil.getFormatedDate(getDate());
        String todayFormat = DateUtil.getFormatedDate(new Date());
        return dateActivityFormat.equalsIgnoreCase(todayFormat);
    }

    // Activity (not a ToDo nor a Report)
    public boolean isActivity() {
        return getPriority() == -1 && !isCompleted();
    }

    public static Activity getActivity(int Id) {
        return ActivitiesDAO.getInstance().getActivity(Id);
    }

    public boolean isFinished() {
        return actualPoms == estimatedPoms + overestimatedPoms;
    }

    public boolean isStory() {
        return ControlPanel.preferences.getAgileMode()
                && (getType().equalsIgnoreCase("User story") || getType().equalsIgnoreCase("Epic"));
    }
}
