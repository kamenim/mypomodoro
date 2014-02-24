package org.mypomodoro.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import org.mypomodoro.Main;
import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

public class TestMenu extends JMenu {

    private static final long serialVersionUID = 20110814L;

    public TestMenu(final MyPomodoroView view) {
        super(Labels.getString("MenuBar.Data"));
        add(new TestDataItem(view));
        add(new JSeparator());
        add(new ResetDataItem(view));
    }

    // resets all the data files.
    class ResetDataItem extends JMenuItem {

        private static final long serialVersionUID = 20110814L;

        public ResetDataItem(final MyPomodoroView view) {
            super(Labels.getString("DataMenu.Clear All Data"));
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    ActivitiesDAO.getInstance().removeAll();
                    view.updateView();
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

                    Date date = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                    Date currentDateAtMidnight = cal.getTime();

                    // insert data into the activitylist
                    for (int i = 0; i < alSize; i++) {

                        Activity a = new Activity(place[rand.nextInt(12)],
                                authors[rand.nextInt(5)],
                                name[rand.nextInt(4)],
                                description[rand.nextInt(5)],
                                type[rand.nextInt(5)],
                                rand.nextInt(ControlPanel.preferences.getMaxNbPomPerActivity()) + 1,
                                storypoint[rand.nextInt(23)],
                                iteration[rand.nextInt(7)],
                                currentDateAtMidnight);
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
                    view.updateView();
                }
            });
        }
    }
}
