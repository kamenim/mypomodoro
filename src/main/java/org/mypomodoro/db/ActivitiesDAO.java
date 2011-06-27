package org.mypomodoro.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;

public class ActivitiesDAO {

    private final Database database;
    private static final ActivitiesDAO instance = new ActivitiesDAO();

    public static ActivitiesDAO getInstance() {
        return instance;
    }

    ActivitiesDAO() {
        database = new Database();
        database.createActivitiesTable();
    }

    public void removeAll() {
        database.resetData();
    }

    public void insert(Activity newActivity) {
        String insertSQL = "INSERT INTO activities VALUES ( " + "NULL, " + "'"
                + newActivity.getName() + "', " + "'" + newActivity.getType()
                + "', " + "'" + newActivity.getDescription() + "', " + "'"
                + newActivity.getNotes() + "', " + "'"
                + newActivity.getAuthor() + "', " + "'"
                + newActivity.getPlace() + "', "
                + newActivity.getDate().getTime() + ", "
                + newActivity.getEstimatedPoms() + ", "
                + newActivity.getActualPoms() + ", "
                + newActivity.getOverestimatedPoms() + ", " + "'"
                + String.valueOf(newActivity.isCompleted()) + "', " + "'"
                + String.valueOf(newActivity.isUnplanned()) + "', "
                + newActivity.getNumInterruptions() + ", "
                + newActivity.getPriority() + ");";
        try {
            database.lock();
            database.update("begin;");
            database.update(insertSQL);
            database.update("commit;");
        }
        finally {
            database.unlock();
        }
        Main.updateLists();
        Main.updateView();
    }

    public Iterable<Activity> getActivities() {
        database.lock();
        List<Activity> activities = new ArrayList<Activity>();
        try {
            ResultSet rs = database.query("SELECT * FROM activities "
                    + "WHERE priority = -1 AND is_complete = 'false';");
            try {
                while (rs.next()) {
                    activities.add(new Activity(rs));
                }
            }
            catch (SQLException e) {
                System.err.println(e);
            }
            finally {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    System.err.println(e);
                }
            }
        }
        finally {
            database.unlock();
        }
        return activities;
    }

    public Iterable<Activity> getCompletedActivities() {
        List<Activity> activities = new ArrayList<Activity>();
        database.lock();
        try {
            ResultSet rs = database.query("SELECT * FROM activities WHERE is_complete = 'true';");
            try {
                while (rs.next()) {
                    activities.add(new Activity(rs));
                }
            }
            catch (Exception e) {
                System.err.println(e);
            }
            finally {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    System.err.println(e);
                }
            }
        }
        finally {
            database.unlock();
        }
        return activities;
    }

    public Iterable<Activity> getTodoList() {
        List<Activity> activities = new ArrayList<Activity>();
        database.lock();
        try {
            ResultSet rs = database.query("SELECT * FROM activities WHERE priority > -1 AND is_complete = 'false' ORDER BY priority DESC;");
            try {
                while (rs.next()) {
                    activities.add(new Activity(rs));
                }
            }
            catch (Exception e) {
                System.err.println(e);
            }
            finally {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    System.err.println(e);
                }
            }
        }
        finally {
            database.unlock();
        }
        return activities;
    }

    public void removeById(int id) {
        try {
            database.lock();
            database.update("begin;");
            database.update("DELETE FROM activities WHERE id=" + id + ";");
        }
        finally {
            database.update("Commit;");
            database.unlock();
        }
    }

    public void update(Activity activity) {
        String updateSQL = "UPDATE activities SET " + "name = '"
                + activity.getName() + "', " + "type = '" + activity.getType()
                + "', " + "description = '" + activity.getDescription() + "', "
                + "notes = '" + activity.getNotes() + "', " + "author = '"
                + activity.getAuthor() + "', " + "place = '"
                + activity.getPlace() + "', " + "date_added = "
                + activity.getDate().getTime() + ", " + "estimated_poms = "
                + activity.getEstimatedPoms() + ", " + "actual_poms = "
                + activity.getActualPoms() + ", " + "overestimated_poms = "
                + activity.getOverestimatedPoms() + ", "
                + "is_complete = '" + String.valueOf(activity.isCompleted())
                + "', " + "is_unplanned = '"
                + String.valueOf(activity.isUnplanned()) + "', "
                + "num_interruptions = " + activity.getNumInterruptions()
                + ", " + "priority = " + activity.getPriority()
                + " WHERE id = " + activity.getId() + ";";

        database.lock();
        try {
            database.update("begin;");
            database.update(updateSQL);
            database.update("commit;");
        }
        finally {
            database.unlock();
        }
    }

    public Activity getActivityByNameAndDate(Activity newActivity) {
        database.lock();
        Activity activity = null;
        try {
            ResultSet rs = database.query("SELECT * FROM activities "
                    + "WHERE priority = -1 AND is_complete = 'false'"
                    + "AND name = '" + newActivity.getName() + "'"
                    + "AND date_added = " + newActivity.getDate().getTime() + ";");
            try {
                while (rs.next()) {
                    activity = new Activity(rs);
                }
            }
            catch (SQLException e) {
                System.err.println(e);
            }
            finally {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                    System.err.println(e);
                }
            }
        }
        finally {
            database.unlock();
        }
        return activity;
    }
}