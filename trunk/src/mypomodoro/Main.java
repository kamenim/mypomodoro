package mypomodoro;

import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JFrame;
import javax.swing.JWindow;

/**
 * Main Application Starter
 *
 * @author Brian Wetzel
 */
public class Main
{
    public static ActivityListPanel activityListPanel;
    public static GeneratePanel generatePanel;
    public static ToDoListPanel toDoListPanel;
    public static ReportListPanel reportListPanel;
    public static Database db;
    public static ReentrantLock datalock = new ReentrantLock();

    public static void updateView()
    {
        new Thread(new Runnable(){

            public void run() {
                
                    activityListPanel.getTable().updateModel();
                    toDoListPanel.getToDoJList().refresh();
                    generatePanel.getActivityJList().refresh();
                    generatePanel.getToDoJList().refresh();
                    reportListPanel.getTable().updateModel();

            }

        }).start();
    }

    public static void updateLists()
    {
        ActivityList.getList().clear();
        ActivityList.getList().RefreshActivityList();
        ToDoList.getList().clear();
        ToDoList.getList().refreshList();
        ReportList.getList().clear();
        ReportList.getList().refreshList();
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FontFormatException, IOException
    {
        db = new Database();
        db.initialize();
        Main.activityListPanel = new ActivityListPanel();
        Main.generatePanel = new GeneratePanel();
        Main.toDoListPanel = new ToDoListPanel();
        Main.reportListPanel = new ReportListPanel();
        final MyPomodoroView gui = new MyPomodoroView();
        gui.setVisible(true);
        Dimension screenSize = gui.getToolkit().getScreenSize();
        int w = (int) ((screenSize.getWidth() - gui.getSize().width) / 2);
        int h = (int) ((screenSize.getHeight() - gui.getSize().height) / 2);
        gui.setLocation(w, h);
        gui.addComponentListener(new java.awt.event.ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent event)
            {
                gui.setSize(Math.max(650, gui.getWidth()),
                            Math.max(550, gui.getHeight()));
            }
        });
    }
}
