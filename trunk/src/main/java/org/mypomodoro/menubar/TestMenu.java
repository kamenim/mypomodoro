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
import java.util.Calendar;
import java.util.Date;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingUtilities;
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
                    String[] authors = {"Brian", "Paul", "Bobby", "Jordan",
                        "Rick"};
                    String[] place = {"GGC", "School", "Work", "Home",
                        "Atlanta", "Chicago", "Seattle", "Boston",
                        "Baltimore", "Philadelphia", "Los Angeles",
                        "New York"};
                    String[] name = {"Write SD Project Essay",
                        "Finish Packaging Application",
                        "Finish Application", "Complete Testing"};
                    String[] description = {
                        "Address software project development,"
                        + " expected issues, potential alternatives, risk management "
                        + "and implementation and testing strategies.",
                        "Combine all jar files into a single executable jar",
                        "Post all source and executables on Google Project Hosting",
                        "Resolve most of the known bugs.",
                        "Preform manual testing of GUI"};
                    String[] type = {"Homework", "Work", "Testing",
                        "Programming", "Distribution"};
                    Float[] storypoint = new Float[]{0f, 0f, 0f, 0f, 0.5f, 0.5f, 0.5f, 1f, 1f, 1f, 2f, 2f, 2f, 3f, 3f, 5f, 5f, 8f, 8f, 13f, 20f, 40f, 100f};
                    Integer[] iteration = new Integer[]{-1, 0, 1, 2, 3, 4, 5};
                    java.util.Random rand = new java.util.Random();
                    int alSize = 10;
                    for (int i = 0; i < alSize; i++) {
                        Activity a = new Activity(place[rand.nextInt(12)],
                                authors[rand.nextInt(5)],
                                name[rand.nextInt(4)],
                                description[rand.nextInt(5)],
                                type[rand.nextInt(5)],
                                rand.nextInt(PreferencesPanel.preferences.getMaxNbPomPerActivity()) + 1,
                                storypoint[rand.nextInt(23)],
                                iteration[rand.nextInt(7)],
                                new Date());
                        a.databaseInsert();
                    }
                }
            });
        }

        public TestDataItem(final MyPomodoroView view) {
            super(Labels.getString("DataMenu.Populate Test Data"));
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    createTestData();
                    Main.updateActivityList();
                    Main.updateActivityListView();
                }
            });
        }
    }
}
