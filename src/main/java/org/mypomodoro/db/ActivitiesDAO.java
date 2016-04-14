/* 
 * Copyright (C) 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.mypomodoro.Main;
import org.mypomodoro.db.mysql.MySQLConfigLoader;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.DateUtil;

public class ActivitiesDAO {

    private final Database database = Main.database;
    private static final ActivitiesDAO INSTANCE = new ActivitiesDAO();

    public static ActivitiesDAO getInstance() {
        return INSTANCE;
    }

    ActivitiesDAO() {
        database.createActivitiesTable();
    }

    public int insert(Activity newActivity) {
        int id = -1;
        String insertSQL = "INSERT INTO activities VALUES ( " + "NULL, "
                + "'" + newActivity.getName().replace("'", "''") + "', "
                + "'" + newActivity.getType().replace("'", "''") + "', "
                + "'" + newActivity.getDescription().replace("'", "''") + "', "
                + "'" + newActivity.getNotes().replace("'", "''") + "', "
                + "'" + newActivity.getAuthor().replace("'", "''") + "', "
                + "'" + newActivity.getPlace().replace("'", "''") + "', "
                + newActivity.getDate().getTime() + ", "
                + newActivity.getDateCompleted().getTime() + ", "
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
                + newActivity.getParentId() + ", "
                + "'" + String.valueOf(newActivity.isDoneDone()) + "', "
                + newActivity.getDateDoneDone().getTime() + ");";
        try {
            database.lock();
            database.update("begin;");
            database.update(insertSQL);
            database.update("commit;");
            // Get primary key
            ResultSet rs = database.query(database.selectStatementSeqId);
            try {
                if (rs.next()) {
                    id = rs.getInt(database.sequenceIdName);
                }
            } catch (SQLException ex) {
                Main.logger.error("", ex);
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                }
            }
        } finally {
            database.unlock();
        }
        return id;
    }

    public void update(Activity activity) {
        String updateSQL = "UPDATE activities SET " + "name = '"
                + activity.getName().replace("'", "''") + "', " + "type = '" + activity.getType().replace("'", "''") + "', "
                + "description = '" + activity.getDescription().replace("'", "''") + "', "
                + "notes = '" + activity.getNotes().replace("'", "''") + "', "
                + "author = '" + activity.getAuthor().replace("'", "''") + "', "
                + "place = '" + activity.getPlace().replace("'", "''") + "', "
                + "date_added = " + activity.getDate().getTime() + ", "
                + "date_completed = " + activity.getDateCompleted().getTime() + ", "
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
                + "parent_id = " + activity.getParentId() + ", "
                + "is_donedone = '" + String.valueOf(activity.isDoneDone()) + "',"
                + "date_donedone = " + activity.getDateDoneDone().getTime()
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

    public void updateComment(Activity activity) {
        String updateSQL = "UPDATE activities SET "
                + "notes = '" + activity.getNotes().replace("'", "''") + "'"
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

    public void updateComplete(Activity activity) {
        String updateSQL = "UPDATE activities SET "
                + "is_complete = '" + String.valueOf(activity.isCompleted()) + "'";
        updateSQL += ", date_completed = " + activity.getDateCompleted().getTime();
        updateSQL += " WHERE id = " + activity.getId() + ";";
        try {
            database.lock();
            database.update("begin;");
            database.update(updateSQL);
            database.update("commit;");
        } finally {
            database.unlock();
        }
    }

    public void updateDoneDone(Activity activity) {
        String updateSQL = "UPDATE activities SET "
                + "is_donedone = '" + String.valueOf(activity.isDoneDone()) + "'";
        updateSQL += ", date_donedone = " + activity.getDateDoneDone().getTime();
        updateSQL += " WHERE id = " + activity.getId() + ";";
        try {
            database.lock();
            database.update("begin;");
            database.update(updateSQL);
            database.update("commit;");
        } finally {
            database.unlock();
        }
    }

    // Delete task and subtasks
    public void delete(Activity activity) {
        try {
            database.lock();
            database.update("begin;");
            database.update("DELETE FROM activities WHERE id=" + activity.getId() + " OR parent_id=" + activity.getId() + ";");
        } finally {
            database.update("Commit;");
            database.unlock();
        }
    }

    public Iterable<Activity> getActivities() {
        List<Activity> activities = new ArrayList<Activity>();
        try {
            database.lock();
            String query = "SELECT * FROM activities "
                    + "WHERE priority = -1 AND ((parent_id = -1 AND is_complete = 'false') " // tasks
                    + "OR (parent_id > -1 AND parent_id IN (SELECT id FROM activities WHERE priority = -1 AND parent_id = -1 AND is_complete = 'false'))) " // subtasks                                        
                    + "ORDER BY " + (Main.preferences.getAgileMode() ? "iteration, name ASC;" : "date_added ASC;");
            ResultSet rs = database.query(query);
            try {
                while (rs.next()) {
                    activities.add(new Activity(rs));
                }
            } catch (SQLException ex) {
                // Upgrade from 3.3, 3.4, 4.0, 4.1 or 4.1.1 TO 4.2.0
                Main.logger.error("Fixing following issue... Done", ex);
                if (MySQLConfigLoader.isValid()) {
                    database.update("ALTER TABLE activities ADD (is_donedone VARCHAR(255) DEFAULT 'false', date_donedone " + Main.database.getLongIntegerVarName() + ");");
                } else {
                    database.update("ALTER TABLE activities ADD is_donedone TEXT DEFAULT 'false';");
                    database.update("ALTER TABLE activities ADD date_donedone " + Main.database.getLongIntegerVarName() + ";");
                }
                try {
                    // re-init the result set (rs.first()/beforeFirst() won't suffice)
                    rs = database.query(query);
                    while (rs.next()) {
                        activities.add(new Activity(rs));
                    }
                } catch (SQLException ex1) {
                    Main.logger.error("", ex);
                }
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
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
            String query = "SELECT * FROM activities "
                    + "WHERE priority > -1 AND ((parent_id = -1 AND is_complete = 'false') " // tasks
                    + "OR (parent_id > -1 AND parent_id IN (SELECT id FROM activities WHERE priority > -1 AND parent_id = -1 AND is_complete = 'false'))) " // subtasks                                        
                    + "ORDER BY priority ASC;";
            ResultSet rs = database.query(query);
            try {
                while (rs.next()) {
                    activities.add(new Activity(rs));
                }
            } catch (SQLException ex) {
                Main.logger.error("", ex);
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
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
            String query = "SELECT * FROM activities "
                    + "WHERE priority = -1 AND ((parent_id = -1 AND is_complete = 'true') " // tasks
                    + "OR (parent_id > -1 AND parent_id IN (SELECT id FROM activities WHERE priority = -1 AND parent_id = -1 AND is_complete = 'true'))) " // subtasks                                        
                    + "ORDER BY priority ASC;";
            ResultSet rs = database.query(query);
            try {
                while (rs.next()) {
                    activities.add(new Activity(rs));
                }
            } catch (SQLException ex) {
                Main.logger.error("", ex);
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                }
            }
        } finally {
            database.unlock();
        }
        return activities;
    }

    public Activity getActivityByName(Activity newActivity) {
        Activity activity = null;
        try {
            database.lock();
            ResultSet rs = database.query("SELECT * FROM activities "
                    + "WHERE parent_id = -1 AND priority = -1 AND is_complete = 'false' "
                    + "AND name = '" + newActivity.getName().replace("'", "''") + "';");
            try {
                while (rs.next()) {
                    activity = new Activity(rs);
                }
            } catch (SQLException ex) {
                Main.logger.error("", ex);
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                }
            }
        } finally {
            database.unlock();
        }
        return activity;
    }

    //////////////////////
    /// CHARTS
    //////////////////////
    // Tasks (Dates)
    public ArrayList<Activity> getActivitiesForChartDateRange(Date startDate, Date endDate, ArrayList<Date> datesToBeIncluded, boolean excludeToDos) {
        return getActivitiesForChartDateRange(startDate, endDate, datesToBeIncluded, excludeToDos, -1);
    }

    // Tasks (Dates)
    public ArrayList<Activity> getActivitiesForChartDateRange(Date startDate, Date endDate, ArrayList<Date> datesToBeIncluded, boolean excludeToDos, int iteration) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        if (datesToBeIncluded.size() > 0) {
            try {
                database.lock();
                // Task condition
                String whereCondition = "parent_id = -1 AND ";
                if (iteration >= 0) {
                    whereCondition += "iteration = " + iteration + " AND "; // specific iteration
                }
                if (!excludeToDos) {
                    whereCondition += "(priority > -1 OR (";
                }
                whereCondition += "is_complete = 'true' ";
                int increment = 1;
                whereCondition += "AND (";
                for (Date date : datesToBeIncluded) {
                    if (increment > 1) {
                        whereCondition += " OR ";
                    }
                    whereCondition += "(date_completed >= " + DateUtil.getDateAtStartOfDay(date).getTime() + " ";
                    whereCondition += "AND date_completed < " + DateUtil.getDateAtMidnight(date).getTime() + ")";
                    increment++;
                }
                whereCondition += ") ";
                if (!excludeToDos) {
                    whereCondition += ")) ";
                }
                // Query
                String query = "SELECT * FROM activities WHERE ";
                query += whereCondition;
                query += "ORDER BY date_completed DESC";  // from newest to oldest
                ResultSet rs = database.query(query);
                try {
                    while (rs.next()) {
                        activities.add(new Activity(rs));
                    }
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        Main.logger.error("", ex);
                    }
                }
            } finally {
                database.unlock();
            }
        }
        return activities;
    }

    // Subtasks (Dates)
    public ArrayList<Activity> getSubActivitiesForChartDateRange(Date startDate, Date endDate, ArrayList<Date> datesToBeIncluded, boolean excludeToDos) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        if (datesToBeIncluded.size() > 0) {
            try {
                database.lock();
                int increment = 1;
                // Subtask condition
                String whereSutaskCondition = "parent_id > -1 AND ";
                if (!excludeToDos) {
                    whereSutaskCondition += "(priority > -1 OR (";
                }
                whereSutaskCondition += "is_complete = 'true' ";
                whereSutaskCondition += "AND (";
                for (Date date : datesToBeIncluded) {
                    if (increment > 1) {
                        whereSutaskCondition += " OR ";
                    }
                    whereSutaskCondition += "(date_completed >= " + DateUtil.getDateAtStartOfDay(date).getTime() + " ";
                    whereSutaskCondition += "AND date_completed < " + DateUtil.getDateAtMidnight(date).getTime() + ")";
                    increment++;
                }
                whereSutaskCondition += ") ";
                if (!excludeToDos) {
                    whereSutaskCondition += ")) ";
                }
                // Task condition                
                String whereTaskCondition = "";
                /*if (iteration >= 0) {
                    whereTaskCondition += "AND (parent_id IN "; // subtasks
                    whereTaskCondition += "(SELECT id FROM activities WHERE ";                    
                    whereTaskCondition += "iteration = " + iteration + ") "; // specific iteration
                     
                    whereTaskCondition += ")) ";
                }*/
                // Query
                String query = "SELECT * FROM activities WHERE ";
                query += whereSutaskCondition; // subtasks
                query += whereTaskCondition; // tasks                
                query += "ORDER BY date_completed DESC";  // from newest to oldest                
                ResultSet rs = database.query(query);
                try {
                    while (rs.next()) {
                        activities.add(new Activity(rs));
                    }
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        Main.logger.error("", ex);
                    }
                }
            } finally {
                database.unlock();
            }
        }
        return activities;
    }

    // Tasks (Iterations)
    public ArrayList<Activity> getActivitiesForChartIterationRange(int startIteration, int endIteration) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        try {
            database.lock();
            // Task condition
            String whereTaskCondition = "parent_id = -1 ";
            whereTaskCondition += "AND iteration >= " + startIteration + " ";
            whereTaskCondition += "AND iteration <= " + endIteration + " ";
            whereTaskCondition += "AND (priority > -1 OR is_complete = 'true') ";
            // Query
            String query = "SELECT * FROM activities WHERE ";
            query += whereTaskCondition;
            query += "ORDER BY iteration ASC"; // from lowest to highest            
            ResultSet rs = database.query(query);
            try {
                while (rs.next()) {
                    activities.add(new Activity(rs));
                }
            } catch (SQLException ex) {
                Main.logger.error("", ex);
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                }
            }
        } finally {
            database.unlock();
        }
        return activities;
    }

    // Scope: tasks and story points (Dates)
    public ArrayList<Float> getSumOfStoryPointsOfActivitiesDateRange(ArrayList<Date> datesToBeIncluded) {
        ArrayList<Float> storyPoints = new ArrayList<Float>();
        if (datesToBeIncluded.size() > 0) {
            try {
                database.lock();
                for (Date date : datesToBeIncluded) {
                    // Date reopen not to be taken into account as it is merely an existing activity rescheduled at a later date                    
                    ResultSet rs = database.query("SELECT SUM(story_points) as sum FROM activities "
                            + "WHERE parent_id = -1 AND date_added < " + (new DateTime(DateUtil.getDateAtMidnight(date))).getMillis());
                    try {
                        while (rs.next()) {
                            storyPoints.add((Float) rs.getFloat("sum"));
                        }
                    } catch (SQLException ex) {
                        Main.logger.error("", ex);
                    } finally {
                        try {
                            rs.close();
                        } catch (SQLException ex) {
                            Main.logger.error("", ex);
                        }
                    }
                }
            } finally {
                database.unlock();
            }
        }
        return storyPoints;
    }

    // Scope: tasks and story points (Iterations)
    public ArrayList<Float> getSumOfStoryPointsOfActivitiesIterationRange(int startIteration, int endIteration) {
        ArrayList<Float> storyPoints = new ArrayList<Float>();
        try {
            database.lock();
            for (int i = startIteration; i <= endIteration; i++) {
                ResultSet rs = database.query("SELECT SUM(story_points) as sum FROM activities "
                        + "INNER JOIN "
                        + "(SELECT MAX(date_completed) as maxDateCompleted FROM activities WHERE parent_id = -1 AND is_complete = 'true' AND iteration = " + i + ") SubQuery "
                        + "ON (SubQuery.maxDateCompleted > 0 AND (activities.date_added <= SubQuery.maxDateCompleted OR activities.date_completed = 0))");
                try {
                    while (rs.next()) {
                        Float sumOfStoryPoints = (Float) rs.getFloat("sum");
                        sumOfStoryPoints = i > startIteration && sumOfStoryPoints == 0 ? storyPoints.get(storyPoints.size() - 1) : sumOfStoryPoints; // iteration no yet started : using sum of the previous iteration
                        storyPoints.add(sumOfStoryPoints);
                    }
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        Main.logger.error("", ex);
                    }
                }
            }
        } finally {
            database.unlock();
        }
        return storyPoints;
    }

    // Scope: tasks or subtasks and pomodoros (Dates)
    public ArrayList<Float> getSumOfPomodorosOfActivitiesDateRange(ArrayList<Date> datesToBeIncluded, boolean subtasks) {
        ArrayList<Float> pomodoros = new ArrayList<Float>();
        if (datesToBeIncluded.size() > 0) {
            try {
                database.lock();
                for (Date date : datesToBeIncluded) {
                    String query = "SELECT SUM(estimated_poms) + SUM(overestimated_poms) as sum FROM activities WHERE ";
                    if (subtasks) {
                        query += "parent_id > -1 ";
                    } else {
                        query += "parent_id = -1 ";
                    }
                    query += "AND date_added < " + (new DateTime(DateUtil.getDateAtMidnight(date))).getMillis();
                    // Date reopen not to be taken into account as it is merely an existing activity rescheduled at a later date                    
                    ResultSet rs = database.query(query);
                    try {
                        while (rs.next()) {
                            pomodoros.add((Float) rs.getFloat("sum"));
                        }
                    } catch (SQLException ex) {
                        Main.logger.error("", ex);
                    } finally {
                        try {
                            rs.close();
                        } catch (SQLException ex) {
                            Main.logger.error("", ex);
                        }
                    }
                }
            } finally {
                database.unlock();
            }
        }
        return pomodoros;
    }

    // Scope: tasks and pomodoros (Iterations)
    public ArrayList<Float> getSumOfPomodorosOfActivitiesIterationRange(int startIteration, int endIteration) {
        ArrayList<Float> pomodoros = new ArrayList<Float>();
        try {
            database.lock();
            for (int i = startIteration; i <= endIteration; i++) {
                ResultSet rs = database.query("SELECT SUM(estimated_poms) + SUM(overestimated_poms) as sum FROM activities "
                        + "INNER JOIN "
                        + "(SELECT MAX(date_completed) as maxDateCompleted FROM activities WHERE parent_id = -1 AND is_complete = 'true' AND iteration = " + i + ") SubQuery "
                        + "ON (SubQuery.maxDateCompleted > 0 AND (activities.date_added <= SubQuery.maxDateCompleted OR activities.date_completed = 0))");
                try {
                    while (rs.next()) {
                        Float sumOfPomodoros = (Float) rs.getFloat("sum");
                        sumOfPomodoros = i > startIteration && sumOfPomodoros == 0 ? pomodoros.get(pomodoros.size() - 1) : sumOfPomodoros; // iteration no yet started : using sum of the previous iteration
                        pomodoros.add(sumOfPomodoros);
                    }
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        Main.logger.error("", ex);
                    }
                }
            }
        } finally {
            database.unlock();
        }
        return pomodoros;
    }

    // Scope: tasks or subtasks (Dates)
    public ArrayList<Float> getSumOfTasksOfActivitiesDateRange(ArrayList<Date> datesToBeIncluded, boolean subtasks) {
        ArrayList<Float> tasks = new ArrayList<Float>();
        if (datesToBeIncluded.size() > 0) {
            try {
                database.lock();
                for (Date date : datesToBeIncluded) {
                    String query = "SELECT COUNT(*) as sum FROM activities WHERE ";
                    if (subtasks) {
                        query += "parent_id > -1 ";
                    } else {
                        query += "parent_id = -1 ";
                    }
                    query += "AND date_added < " + (new DateTime(DateUtil.getDateAtMidnight(date))).getMillis();
                    // Date reopen not to be taken into account as it is merely an existing activity rescheduled at a later date                    
                    ResultSet rs = database.query(query);
                    try {
                        while (rs.next()) {
                            tasks.add((Float) rs.getFloat("sum"));
                        }
                    } catch (SQLException ex) {
                        Main.logger.error("", ex);
                    } finally {
                        try {
                            rs.close();
                        } catch (SQLException ex) {
                            Main.logger.error("", ex);
                        }
                    }
                }
            } finally {
                database.unlock();
            }
        }
        return tasks;
    }

    // Scope: tasks (Iterations)
    public ArrayList<Float> getSumOfTasksOfActivitiesIterationRange(int startIteration, int endIteration) {
        ArrayList<Float> tasks = new ArrayList<Float>();
        try {
            database.lock();
            for (int i = startIteration; i <= endIteration; i++) {
                ResultSet rs = database.query("SELECT COUNT(*) as sum FROM activities "
                        + "INNER JOIN "
                        + "(SELECT MAX(date_completed) as maxDateCompleted FROM activities WHERE parent_id = -1 AND is_complete = 'true' AND iteration = " + i + ") SubQuery "
                        //+ "ON (SubQuery.maxDateCompleted > 0 AND parent_id = -1 AND (activities.date_added <= SubQuery.maxDateCompleted OR activities.date_completed = 0))");
                        + "ON (parent_id = -1 AND (activities.date_added <= SubQuery.maxDateCompleted OR activities.date_completed = 0))");
                try {
                    while (rs.next()) {
                        Float sum = (Float) rs.getFloat("sum");
                        sum = i > startIteration && sum == 0 ? tasks.get(tasks.size() - 1) : sum; // iteration no yet started : using sum of the previous iteration
                        tasks.add(sum);
                    }
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        Main.logger.error("", ex);
                    }
                }
            }
        } finally {
            database.unlock();
        }
        return tasks;
    }

    public void deleteAll() {
        try {
            database.lock();
            database.update("begin;");
            database.update("DELETE from activities;");
        } finally {
            database.update("Commit;");
            database.unlock();
        }
    }

    public Activity getActivity(int id) {
        Activity activity = null;
        try {
            database.lock();
            ResultSet rs = database.query("SELECT * FROM activities "
                    + "WHERE id = " + id + ";");
            try {
                while (rs.next()) {
                    activity = new Activity(rs);
                }
            } catch (SQLException ex) {
                Main.logger.error("", ex);
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                }
            }
        } finally {
            database.unlock();
        }
        return activity;
    }

    /*
     // move all ToDos back to Activity list
     public void moveAllTODOs() {
     String updateSQL = "UPDATE activities SET "
     + "priority = -1 "
     + "WHERE priority > -1 AND is_complete = 'false';";
     try {
     database.lock();
     database.update("begin;");
     database.update(updateSQL);
     } finally {
     database.update("Commit;");
     database.unlock();
     }
     }

     // move all ToDos to Report list
     public void completeAllTODOs() {
     String updateSQL = "UPDATE activities SET "
     + "is_complete = 'true',"
     + "priority = -1,"
     + "date_completed = " + new Date().getTime()
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

     // move all Reports back to Activity list
     public void reopenAllReports() {
     String updateSQL = "UPDATE activities SET "
     + "is_complete = 'false',"
     + "date_completed = " + new Date().getTime()
     + " WHERE priority = -1 AND is_complete = 'true';";
     try {
     database.lock();
     database.update("begin;");
     database.update(updateSQL);
     } finally {
     database.update("Commit;");
     database.unlock();
     }
     }
     */
    public ArrayList<String> getTypes() {
        ArrayList<String> types = new ArrayList<String>();
        try {
            database.lock();
            ResultSet rs = database.query("SELECT DISTINCT type FROM activities ORDER BY type ASC");
            try {
                while (rs.next()) {
                    String type = rs.getString("type");
                    if (type != null) {
                        types.add(type);
                    }
                }
            } catch (SQLException ex) {
                Main.logger.error("", ex);
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                }
            }
        } finally {
            database.unlock();
        }
        return types;
    }

    // Not used
    /*public ArrayList<String> getSubTaskTypes() {
     ArrayList<String> types = new ArrayList<String>();
     try {
     database.lock();
     ResultSet rs = database.query("SELECT DISTINCT type FROM activities WHERE parent_id > -1 ORDER BY type ASC");
     try {
     while (rs.next()) {
     String type = rs.getString("type");
     if (type != null) {
     types.add(type);
     }
     }
     } catch (SQLException ex) {
     Main.logger.error("", ex);
     } finally {
     try {
     rs.close();
     } catch (SQLException ex) {
     Main.logger.error("", ex);
     }
     }
     } finally {
     database.unlock();
     }
     return types;
     }*/
    public ArrayList<String> getAuthors() {
        ArrayList<String> types = new ArrayList<String>();
        try {
            database.lock();
            ResultSet rs = database.query("SELECT DISTINCT author FROM activities ORDER BY author ASC");
            try {
                while (rs.next()) {
                    String type = rs.getString("author");
                    if (type != null) {
                        types.add(type);
                    }
                }
            } catch (SQLException ex) {
                Main.logger.error("", ex);
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
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
            ResultSet rs = database.query("SELECT DISTINCT place FROM activities ORDER BY place ASC");
            try {
                while (rs.next()) {
                    String type = rs.getString("place");
                    if (type != null) {
                        types.add(type);
                    }
                }
            } catch (SQLException ex) {
                Main.logger.error("", ex);
            } finally {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Main.logger.error("", ex);
                }
            }
        } finally {
            database.unlock();
        }
        return types;
    }
}
