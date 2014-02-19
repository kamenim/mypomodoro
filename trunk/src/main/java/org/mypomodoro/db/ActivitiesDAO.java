package org.mypomodoro.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;

public class ActivitiesDAO {

    private final Database database = Main.database;
    private static final ActivitiesDAO instance = new ActivitiesDAO();

    public static ActivitiesDAO getInstance() {
        return instance;
    }

    ActivitiesDAO() {
        database.createActivitiesTable();
    }

    public void removeAll() {
        database.resetData();
    }

    public void insert(Activity newActivity) {
        String insertSQL = "INSERT INTO activities VALUES ( " + "NULL, "
                + "'" + newActivity.getName().replace("'", "''") + "', "
                + "'" + newActivity.getType().replace("'", "''") + "', "
                + "'" + newActivity.getDescription().replace("'", "''") + "', "
                + "'" + newActivity.getNotes().replace("'", "''") + "', "
                + "'" + newActivity.getAuthor().replace("'", "''") + "', "
                + "'" + newActivity.getPlace().replace("'", "''")
                + "', " + newActivity.getDate().getTime() + ", "
                + newActivity.getEstimatedPoms() + ", "
                + newActivity.getActualPoms() + ", "
                + newActivity.getOverestimatedPoms() + ", "
                + "'" + String.valueOf(newActivity.isCompleted()) + "', "
                + "'" + String.valueOf(newActivity.isUnplanned()) + "', "
                + newActivity.getNumInterruptions() + ", "
                + newActivity.getPriority() + ", "
                + newActivity.getNumInternalInterruptions() + ", "
                + newActivity.getStoryPoints() + ", "
                + newActivity.getIteration() + ", "
                + newActivity.getParentId() + ");";
        try {
            database.lock();
            database.update("begin;");
            database.update(insertSQL);
            database.update("commit;");
        } finally {
            database.unlock();
        }
        Main.updateLists();
        Main.updateView();
    }

    public Iterable<Activity> getActivities() {
        List<Activity> activities = new ArrayList<Activity>();
        try {
            database.lock();
            ResultSet rs = database.query("SELECT * FROM activities "
                    + "WHERE priority = -1 AND is_complete = 'false' ORDER BY date_added ASC;");
            try {
                while (rs.next()) {
                    activities.add(new Activity(rs));
                }
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        } finally {
            database.unlock();
        }
        return activities;
    }

    public Iterable<Activity> getReports() {
        List<Activity> activities = new ArrayList<Activity>();
        try {
            database.lock();
            ResultSet rs = database.query("SELECT * FROM activities WHERE is_complete = 'true' ORDER BY date_added DESC;");
            try {
                while (rs.next()) {
                    activities.add(new Activity(rs));
                }
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        } finally {
            database.unlock();
        }
        return activities;
    }

    public Iterable<Activity> getTODOs() {
        List<Activity> activities = new ArrayList<Activity>();
        try {
            database.lock();
            ResultSet rs = database.query("SELECT * FROM activities WHERE priority > -1 AND is_complete = 'false' ORDER BY priority;");
            try {
                while (rs.next()) {
                    activities.add(new Activity(rs));
                }
            } catch (Exception e) {
                System.err.println(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        } finally {
            database.unlock();
        }
        return activities;
    }

    public void removeById(int id) {
        try {
            database.lock();
            database.update("begin;");
            database.update("DELETE FROM activities WHERE id=" + id + ";");
        } finally {
            database.update("Commit;");
            database.unlock();
        }
    }

    public void update(Activity activity) {
        String updateSQL = "UPDATE activities SET " + "name = '"
                + activity.getName().replace("'", "''") + "', " + "type = '" + activity.getType().replace("'", "''") + "', "
                + "description = '" + activity.getDescription().replace("'", "''") + "', "
                + "notes = '" + activity.getNotes().replace("'", "''") + "', "
                + "author = '" + activity.getAuthor().replace("'", "''") + "', "
                + "place = '" + activity.getPlace().replace("'", "''") + "', "
                + "date_added = " + activity.getDate().getTime() + ", "
                + "estimated_poms = " + activity.getEstimatedPoms() + ", "
                + "actual_poms = " + activity.getActualPoms() + ", "
                + "overestimated_poms = " + activity.getOverestimatedPoms() + ", "
                + "is_complete = '" + String.valueOf(activity.isCompleted()) + "', "
                + "is_unplanned = '" + String.valueOf(activity.isUnplanned()) + "', "
                + "num_interruptions = " + activity.getNumInterruptions() + ", "
                + "priority = " + activity.getPriority() + ", "
                + "num_internal_interruptions = " + activity.getNumInternalInterruptions() + ", "
                + "story_points = " + activity.getStoryPoints() + ", "
                + "iteration = " + activity.getIteration() + ", "
                + "parent_id = " + activity.getParentId()
                + " WHERE id = " + activity.getId() + ";";
        try {
            database.lock();
            database.update("begin;");
            database.update(updateSQL);
            database.update("commit;");
        } finally {
            database.unlock();
        }
    }

    public Activity getActivityByNameAndDate(Activity newActivity) {
        Activity activity = null;
        try {
            database.lock();
            ResultSet rs = database.query("SELECT * FROM activities "
                    + "WHERE priority = -1 AND is_complete = 'false'"
                    + "AND name = '" + newActivity.getName().replace("'", "''") + "'"
                    + "AND date_added = " + newActivity.getDate().getTime() + ";");
            try {
                while (rs.next()) {
                    activity = new Activity(rs);
                }
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        } finally {
            database.unlock();
        }
        return activity;
    }

    public void removeAllReports() {
        try {
            database.lock();
            database.update("begin;");
            database.update("DELETE FROM activities WHERE is_complete = 'true';");
        } finally {
            database.update("Commit;");
            database.unlock();
        }
    }

    public void removeAllActivities() {
        try {
            database.lock();
            database.update("begin;");
            database.update("DELETE FROM activities WHERE priority = -1 AND is_complete = 'false';");
        } finally {
            database.update("Commit;");
            database.unlock();
        }
    }

    // Activities, TODOs and Reports
    public Activity getActivity(int ID) {
        Activity activity = null;
        try {
            database.lock();
            ResultSet rs = database.query("SELECT * FROM activities "
                    + "WHERE id = " + ID + ";");
            try {
                while (rs.next()) {
                    activity = new Activity(rs);
                }
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        } finally {
            database.unlock();
        }
        return activity;
    }

    public void completeAllTODOs() {
        String updateSQL = "UPDATE activities SET "
                + "is_complete = 'true'"
                + " WHERE priority > -1 AND is_complete = 'false';";
        try {
            database.lock();
            database.update("begin;");
            database.update(updateSQL);
        } finally {
            database.update("Commit;");
            database.unlock();
        }
    }

    public ArrayList<String> getTypes() {
        ArrayList<String> types = new ArrayList<String>();
        try {
            database.lock();
            ResultSet rs = database.query("SELECT DISTINCT type FROM activities ORDER BY type");
            try {
                while (rs.next()) {
                    String type = rs.getString("type");
                    if (type != null) {
                        types.add(type);
                    }
                }
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        } finally {
            database.unlock();
        }
        return types;
    }

    public ArrayList<String> getAuthors() {
        ArrayList<String> types = new ArrayList<String>();
        try {
            database.lock();
            ResultSet rs = database.query("SELECT DISTINCT author FROM activities ORDER BY author");
            try {
                while (rs.next()) {
                    String type = rs.getString("author");
                    if (type != null) {
                        types.add(type);
                    }
                }
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        } finally {
            database.unlock();
        }
        return types;
    }

    public ArrayList<String> getPlaces() {
        ArrayList<String> types = new ArrayList<String>();
        try {
            database.lock();
            ResultSet rs = database.query("SELECT DISTINCT place FROM activities ORDER BY place");
            try {
                while (rs.next()) {
                    String type = rs.getString("place");
                    if (type != null) {
                        types.add(type);
                    }
                }
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        } finally {
            database.unlock();
        }
        return types;
    }
}
