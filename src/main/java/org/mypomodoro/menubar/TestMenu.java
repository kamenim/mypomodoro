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
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.mypomodoro.Main;
import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.gui.PreferencesPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

public class TestMenu extends JMenu {

    private static final long serialVersionUID = 20110814L;

    public TestMenu(final MyPomodoroView view) {
        super(Labels.getString("MenuBar.Data"));
        add(new TestDataItem(view));
        add(new JSeparator());
        add(new ResetDataItem(view));
        addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent e) {
                // do nothing
            }

            @Override
            public void focusLost(FocusEvent e) {
                MenuSelectionManager.defaultManager().clearSelectedPath();
            }
        });
    }

    // resets all the data files.
    class ResetDataItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public ResetDataItem(final MyPomodoroView view) {
            super(Labels.getString("DataMenu.Clear All Data"));
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ActivitiesDAO.getInstance().deleteAll();
                    Main.updateLists();
                    Main.updateView();
                }
            });
        }
    }

    class TestDataItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        private void createTestData() {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    Float[] storypoint = new Float[]{0f, 0.5f, 0.5f, 0.5f, 1f, 1f, 1f, 2f, 2f, 2f, 3f, 3f, 5f, 5f, 8f, 8f, 13f, 20f};
                    Integer[] iteration = new Integer[]{-1, 0, 1, 2, 3, 4};
                    java.util.Random rand = new java.util.Random();
                    int alSize = 300;
                    int increment = 1;
                    for (int i = 0; i < alSize; i++) {
                        int minusDay = rand.nextInt(20);
                        Activity a = new Activity(
                                "Place" + " " + (rand.nextInt(10) + 1),
                                "Author" + " " + (rand.nextInt(10) + 1),
                                "Task" + " " + (i + 1),
                                "",
                                "Type" + " " + (rand.nextInt(10) + 1),
                                rand.nextInt(PreferencesPanel.preferences.getMaxNbPomPerActivity()) + 1,
                                storypoint[rand.nextInt(storypoint.length)],
                                iteration[rand.nextInt(iteration.length)],
                                (new DateTime(new Date()).minusDays(minusDay)).toDate());

                        a.setIsCompleted(rand.nextBoolean());
                        a.setOverestimatedPoms(rand.nextInt(3));
                        int actual = rand.nextInt(a.getEstimatedPoms() + a.getOverestimatedPoms());
                        actual += (a.getEstimatedPoms() + a.getOverestimatedPoms() > actual) ? rand.nextInt(a.getEstimatedPoms() + a.getOverestimatedPoms() - actual) : 0; // give some weigth to actual so there are more real pomodoros
                        a.setActualPoms(actual);
                        if (a.getIteration() == -1) {
                            a.setStoryPoints(0);
                        }
                        if (a.isCompleted()) { // Tasks for the Report list
                            // TODO review this code
                            Date date = (new DateTime(a.getDate()).plusDays(rand.nextInt(minusDay + 1))).toDate();
                            while (new DateTime(date).getDayOfWeek() == DateTimeConstants.SATURDAY
                                    || new DateTime(date).getDayOfWeek() == DateTimeConstants.SUNDAY) { // excluding saturdays and sundays
                                date = (new DateTime(date).plusDays(1)).toDate();
                            }
                            a.setDateCompleted(date);
                        } else { // Task for the Activity and ToDo list
                            if (rand.nextBoolean() && rand.nextBoolean()) { // Tasks for the ToDo list (make it shorter than the other two lists)
                                if (a.getIteration() >= 0) {
                                    a.setIteration(iteration[iteration.length - 1]); // use highest iteration number for tasks in the Iteration backlog
                                }
                                a.setPriority(increment);
                                increment++;
                            } else { // Tasks for the Activity list 
                                if (a.getIteration() >= 0) {
                                    a.setIteration(iteration[iteration.length - 1] + 1); // use unstarted iteration number
                                }
                                a.setOverestimatedPoms(0);
                                a.setActualPoms(0);
                                a.setDate(new Date());
                            }
                        }
                        a.databaseInsert();
                    }
                }
            });
        }

        public TestDataItem(final MyPomodoroView view) {
            super(Labels.getString("DataMenu.Generate Test Data"));
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    createTestData();
                    Main.updateLists();
                    Main.updateView();
                }
            });
        }
    }
}
