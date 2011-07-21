package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.model.Activity;

import org.mypomodoro.db.ActivitiesDAO;
import java.util.Date;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.util.Labels;

public class TestMenu extends JMenu {

    public TestMenu(final MyPomodoroView view) {
        super(Labels.getString("MenuBar.Data"));
        add(new ResetDataItem(view));
        add(new TestDataItem(view));
    }

    // resets all the data files.
    class ResetDataItem extends JMenuItem {

        public ResetDataItem(final MyPomodoroView view) {
            super(Labels.getString("DataMenu.Clear All Data"));
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ActivitiesDAO.getInstance().removeAll();
                    view.updateLists();
                }
            });
        }
    }

    class TestDataItem extends JMenuItem {

        private void createTestData() {
            new Thread(new Runnable() {

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
                    java.util.Random rand = new java.util.Random();
                    int alSize = 10;

                    // insert data into the activitylist
                    for (int i = 0; i < alSize; i++) {

                        Activity a = new Activity(place[rand.nextInt(12)],
                                authors[rand.nextInt(5)],
                                name[rand.nextInt(4)], description[rand.nextInt(5)], type[rand.nextInt(5)],
                                rand.nextInt(ControlPanel.preferences.getMaxNbPomPerActivity()) + 1,
                                new Date()); // excluding 0 poms
                        a.databaseInsert();
                    }
                }
            }).start();
        }

        public TestDataItem(final MyPomodoroView view) {
            super(Labels.getString("DataMenu.Populate Test Data"));
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    createTestData();
                    view.updateLists();
                }
            });
        }
    }
}