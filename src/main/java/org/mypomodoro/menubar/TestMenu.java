/* 
 * Copyright (C) 2014
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
package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;
import java.util.Random;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import org.joda.time.DateTime;
import org.mypomodoro.Main;
import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.gui.MainPanel;
import org.mypomodoro.gui.PreferencesPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ActivityList;
import org.mypomodoro.model.ReportList;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.WaitCursor;

public class TestMenu extends JMenu {

    private static final long serialVersionUID = 20110814L;

    private final int nbTask = 300;

    public TestMenu(final MainPanel view) {
        super(Labels.getString("MenuBar.Data"));
        add(new TestDataItem(view));
        add(new JSeparator());
        add(new ResetDataItem(view));
        addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent ignored) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
            }
        });
    }

    // create test data
    class TestDataItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        private void createTestData() {
            new Thread() { // This new thread is necessary for updating the progress bar
                @Override
                public void run() {
                    if (!WaitCursor.isStarted()) {
                        // Start wait cursor
                        WaitCursor.startWaitCursor();
                        // Disable item menu
                        setEnabled(false);
                        // Set progress bar
                        Main.progressBar.setVisible(true);
                        Main.progressBar.getBar().setValue(0);
                        Main.progressBar.getBar().setMaximum(nbTask);

                        Float[] storypoint = new Float[]{0f, 0.5f, 0.5f, 0.5f, 1f, 1f, 1f, 2f, 2f, 2f, 3f, 3f, 5f, 5f, 8f, 8f, 13f, 20f};
                        Integer[] iteration = new Integer[]{-1, 0, 1, 2, 3, 4};
                        Random rand = new Random();
                        int activityListValue = 0;
                        int todoListValue = 0;
                        int reportListValue = 0;
                        final StringBuilder progressText = new StringBuilder();
                        for (int i = 0; i < nbTask; i++) {
                            int minusDay = rand.nextInt(20);
                            final Activity a = new Activity(
                                    "Place" + " " + (rand.nextInt(10) + 1),
                                    "Author" + " " + (rand.nextInt(10) + 1),
                                    "Task" + " " + (i + 1),
                                    "",
                                    "Type" + " " + (rand.nextInt(10) + 1),
                                    rand.nextInt(PreferencesPanel.preferences.getMaxNbPomPerActivity()) + 1,
                                    storypoint[rand.nextInt(storypoint.length)],
                                    iteration[rand.nextInt(iteration.length)],
                                    (!PreferencesPanel.preferences.getAgileMode() ? new Date() : (new DateTime(new Date()).minusDays(minusDay == 0 ? 1 : minusDay)).toDate()));  // any date in the past 20 days
                            a.setIsCompleted(rand.nextBoolean() && rand.nextBoolean()); // less than Activity List but more than ToDo list
                            a.setOverestimatedPoms(rand.nextBoolean() && rand.nextBoolean() ? rand.nextInt(5) : 0);
                            int actual = rand.nextInt(a.getEstimatedPoms());
                            if (a.getOverestimatedPoms() > 0) {
                                actual = a.getEstimatedPoms() + rand.nextInt(a.getOverestimatedPoms());
                            } else {
                                actual += rand.nextInt(a.getEstimatedPoms() + a.getOverestimatedPoms() - actual); // give some weigth to actual so there are more real pomodoros for completed tasks
                            }
                            a.setActualPoms(actual);
                            if (a.getIteration() == -1) {
                                a.setStoryPoints(0);
                            }
                            if (a.isCompleted()) { // Tasks for the Report list                             
                                actual = actual == 0 ? 1 : actual;
                                if (rand.nextBoolean()) { // once in a while set finished
                                    actual = a.getEstimatedPoms() + a.getOverestimatedPoms();
                                }
                                a.setActualPoms(actual);
                                Date dateCompleted = (new DateTime(a.getDate()).plusDays(minusDay == 0 ? 0 : rand.nextInt(minusDay))).toDate(); // any date between the date of creation / schedule and today (could be the same day) 
                                ReportList.getList().add(a, dateCompleted);
                                reportListValue++;
                            } else { // Task for the Activity and ToDo list
                                if (rand.nextBoolean() && rand.nextBoolean() && rand.nextBoolean()) { // less than Activity List and Report List
                                    if (rand.nextBoolean() && rand.nextBoolean()) { // once in a while set finished
                                        actual = a.getEstimatedPoms() + a.getOverestimatedPoms();
                                        a.setActualPoms(actual);
                                    }
                                    if (a.getIteration() >= 0) {
                                        a.setIteration(iteration[iteration.length - 1]); // use highest iteration number for tasks in the Iteration backlog
                                    }
                                    ToDoList.getList().add(a);
                                    todoListValue++;
                                } else { // Tasks for the Activity list 
                                    if (a.getIteration() >= 0) {
                                        a.setIteration(iteration[iteration.length - 1] + 1); // use unstarted iteration number
                                    }
                                    a.setOverestimatedPoms(0);
                                    a.setActualPoms(0);
                                    ActivityList.getList().add(a, a.getDate());
                                    activityListValue++;
                                }
                            }
                            final int progressValue = i + 1;
                            progressText.setLength(0); // reset string builder
                            progressText.append(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ActivityListPanel.Activity List") + " : ");
                            progressText.append(Integer.toString(activityListValue));
                            progressText.append(" | ");
                            progressText.append(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List") + " : ");
                            progressText.append(Integer.toString(todoListValue));
                            progressText.append(" | ");
                            progressText.append(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ReportListPanel.Report List") + " : ");
                            progressText.append(Integer.toString(reportListValue));
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    Main.progressBar.getBar().setValue(progressValue); // % - required to see the progress                                
                                    Main.progressBar.getBar().setString(progressText.toString()); // task
                                    //Main.progressBar.getBar().setString(Integer.toString(progressValue) + " / " + nbTask); // task
                                }
                            });
                        }
                        // Close progress bar
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Main.progressBar.getBar().setString(Labels.getString("ProgressBar.Done") + " (" + progressText + ")");
                                new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            sleep(1000); // wait one second before hiding the progress bar
                                        } catch (InterruptedException ignored) {
                                        }
                                        // hide progress bar
                                        Main.progressBar.getBar().setString(null);
                                        Main.progressBar.setVisible(false);
                                    }
                                }.start();

                            }
                        });
                        // Enable item menu
                        setEnabled(true);
                        // Stop wait cursor
                        WaitCursor.stopWaitCursor();
                        // Updating views at once (updating individual view in the loop is likely to create ConcurrentModificationException exceptions)
                        Main.updateViews(); // this has to be done inside the current thread
                    }
                }
            }.start();
        }

        public TestDataItem(final MainPanel view) {
            super(Labels.getString("DataMenu.Generate Test Data") + " (" + nbTask + ")");
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    createTestData();
                }
            });
        }
    }

    // delete all data
    class ResetDataItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public ResetDataItem(final MainPanel view) {
            super(Labels.getString("DataMenu.Clear All Data"));
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ActivitiesDAO.getInstance().deleteAll();
                    Main.updateLists();
                    Main.updateViews();
                }
            });
        }
    }
}
