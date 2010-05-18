package mypomodoro;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.help.*;
import javax.swing.border.*;
import javax.swing.event.*;


/**
 * Application GUI for myPomodoro.
 *
 * @author Brian Wetzel
 */
public class MyPomodoroView extends JFrame
{
    private HelpSet hs;
    private HelpBroker hb;
    private URL hsURL;
    public static final int FRAME_WIDTH = 480;
    public static final int FRAME_HEIGHT = 600;
    private ToDoListPanel toDoListPanel = Main.toDoListPanel;
    private CreatePanel createPanel = new CreatePanel();
    private GeneratePanel generatePanel = Main.generatePanel;
    private ReportListPanel reportListPanel = Main.reportListPanel;
    private ActivityListPanel activityListPanel = Main.activityListPanel;
    private MyPomodoroMenuBar menuBar = new MyPomodoroMenuBar();
    private MyPomodoroIconBar iconBar = new MyPomodoroIconBar();

    public MyPomodoroView()
    {
        super("myPomodoro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        setIconImage(new ImageIcon(Main.class.getResource("resources/images/pomodoro16.png")).getImage());
        setJMenuBar(menuBar);
        menuBar.setWindow(new SplashScreen());
        setSize(FRAME_HEIGHT, FRAME_WIDTH);
    }

    class WindowPanel extends JPanel
    {
        public WindowPanel(Container panel)
        {
            setLayout(new BorderLayout());
            add(iconBar, BorderLayout.NORTH);
            add(panel, BorderLayout.CENTER);
        }
    }

    class MyPomodoroIconBar extends JPanel
    {
        private ArrayList<MyIcon> myIcons = new ArrayList<MyIcon>();
        private ArrayList<ImageIcon> myIconsOn = new ArrayList<ImageIcon>();
        private ArrayList<ImageIcon> myIconsOff = new ArrayList<ImageIcon>();

        public MyPomodoroIconBar()
        {
            
            myIconsOn.add(new ImageIcon(Main.class.getResource("resources/images/createButton2.png")));
            myIconsOn.add(new ImageIcon(Main.class.getResource("resources/images/activityButton2.png")));
            myIconsOn.add(new ImageIcon(Main.class.getResource("resources/images/managerButton2.png")));
            myIconsOn.add(new ImageIcon(Main.class.getResource("resources/images/todoButton2.png")));
            myIconsOn.add(new ImageIcon(Main.class.getResource("resources/images/reportButton2.png")));

            myIconsOff.add(new ImageIcon(Main.class.getResource("resources/images/createButton.png")));
            myIconsOff.add(new ImageIcon(Main.class.getResource("resources/images/activityButton.png")));
            myIconsOff.add(new ImageIcon(Main.class.getResource("resources/images/managerButton.png")));
            myIconsOff.add(new ImageIcon(Main.class.getResource("resources/images/todoButton.png")));
            myIconsOff.add(new ImageIcon(Main.class.getResource("resources/images/reportButton.png")));

            myIcons.add(new MyIcon("Create", myIconsOff.get(0), createPanel));
            myIcons.add(new MyIcon("Activity", myIconsOff.get(1), activityListPanel));
            myIcons.add(new MyIcon("Manager", myIconsOff.get(2), generatePanel));
            myIcons.add(new MyIcon("ToDo", myIconsOff.get(3), toDoListPanel));
            myIcons.add(new MyIcon("Report", myIconsOff.get(4), reportListPanel));


            setBorder(new BevelBorder(BevelBorder.RAISED));
            setPreferredSize(new Dimension(getWidth(), 80));
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0.5;
            for(MyIcon i : myIcons)
            {
                add(i, c);
                c.gridx++;
            }
        }

        public void highlightIcon(Icon icon)
        {
            for(MyIcon i: myIcons)
            {
                if(i.getIcon().equals(icon))
                    i.setIcon(myIconsOn.get(myIcons.indexOf(i)));
                else
                    i.setIcon(myIconsOff.get(myIcons.indexOf(i)));
            }
        }

        public MyIcon getSelectedIcon()
        {
            
            MyIcon icon = myIcons.get(0);
            for(MyIcon i : myIcons)
            {
                if(i.getIcon().equals(myIconsOn.get(myIcons.indexOf(i))))
                    icon = i;
            }

            return icon;
        }

        class MyIcon extends JLabel
        {
            private JPanel panel;
            public MyIcon(String Text, Icon filename, JPanel p)
            {
                super(Text, filename, CENTER);
                panel = p;
                Dimension d = new Dimension(75, 75);
                setPreferredSize(d);
                setMinimumSize(d);
                setHorizontalTextPosition(JLabel.CENTER);
                setVerticalTextPosition(JLabel.BOTTOM);
                final JPanel panel = p;
                final JLabel icon = this;
                addMouseListener(new MouseListener()
                {
                    public void mouseClicked(MouseEvent e)
                    {
                        // throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void mousePressed(MouseEvent e)
                    {
                        updateLists();
                        iconBar.highlightIcon(icon.getIcon());
                        menuBar.setWindow(panel);
                    }

                    public void mouseReleased(MouseEvent e)
                    {
                        // throw new UnsupportedOperationException("Not supported yet.");
                    }

                    public void mouseEntered(MouseEvent e)
                    {
                        icon.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
                    }

                    public void mouseExited(MouseEvent e)
                    {
                        icon.setBorder(null);
                    }
                });
            }

            public JPanel getPanel() {
                return panel;
            }


        }
    }

    public void updateLists()
    {
        Main.updateView();
    }

    

    class MyPomodoroMenuBar extends JMenuBar
    {
        public MyPomodoroMenuBar()
        {
            add(new FileMenu());
            add(new ViewMenu());
            add(new TestMenu());
            add(new HelpMenu());           
        }

        private void setWindow(Container e)
        {
            setContentPane(new WindowPanel(e));
            revalidate();
        }

        class TestMenu extends JMenu
        {
            public TestMenu()
            {
                super("Data");
                add(new ResetDataItem());
                add(new TestDataItem());
            }

            //resets all the data files.
            class ResetDataItem extends JMenuItem
            {
                public ResetDataItem()
                {
                    super("Clear All Data");
                    addActionListener(new MenuItemListener());
                }
                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        new Database().resetData();
                        updateLists();
                    }
                }
            }

            class TestDataItem extends JMenuItem
            {
                public TestDataItem()
                {
                    super("Populate Test Data");
                    addActionListener(new MenuItemListener());
                }
                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        new Database().createTestData();
                        updateLists();
                    }
                }
            }
        }

        class FileMenu extends JMenu
        {
            public FileMenu()
            {
                super("File");
                add(new ControlPanelItem());
                add(new CreateActivityItem());                
                add(new ExitItem());
            }

            class ControlPanelItem extends JMenuItem
            {
                public ControlPanelItem()
                {
                    super("Preferences");
                     //Adds Keyboard Shortcut Alt-P
                    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                            ActionEvent.ALT_MASK));
                    addActionListener(new MenuItemListener());
                }
                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        setWindow(new ControlPanel());
                    }
                }
            }

            class CreateActivityItem extends JMenuItem
            {
                public CreateActivityItem()
                {
                    super("New Activity");
                    //Adds Keyboard Shortcut Alt-N
                    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                            ActionEvent.ALT_MASK));
                    addActionListener(new MenuItemListener());
                }
                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        setWindow(createPanel);
                    }
                }
            }

            class ExitItem extends JMenuItem
            {
                public ExitItem()
                {
                    super("Exit");
                    addActionListener(new MenuItemListener());
                }
                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        System.exit(0);
                    }
                }
            }
        }

        //View Menu
        class ViewMenu extends JMenu
        {
            public ViewMenu()
            {
                super("View");
                add(new ActivityListItem());
                add(new GenerateListItem());
                add(new ToDoListItem());
                add(new ReportListItem());
            }

            class ActivityListItem extends JMenuItem
            {
                public ActivityListItem()
                {
                    super("Activity List");
                    //Adds Keyboard Shortcut Alt-A
                    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                            ActionEvent.ALT_MASK));
                    addActionListener(new MenuItemListener());
                }
                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        updateLists();
                        setWindow(activityListPanel);
                    }
                }
            }

            class ReportListItem extends JMenuItem
            {
                public ReportListItem()
                {
                    super("Report List");
                    //Adds Keyboard Shortcut Alt-R
                    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                            ActionEvent.ALT_MASK));
                    addActionListener(new MenuItemListener());
                }
                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        updateLists();
                        setWindow(reportListPanel);
                    }
                }
            }

            class GenerateListItem extends JMenuItem
            {
                public GenerateListItem()
                {
                    super("Manager");
                    //Adds Keyboard Shortcut Alt-M
                    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M,
                            ActionEvent.ALT_MASK));
                    addActionListener(new MenuItemListener());
                }
                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        updateLists();
                        setWindow(generatePanel);
                    }
                }
            }

            class ToDoListItem extends JMenuItem
            {
                public ToDoListItem()
                {
                    super("ToDo List");
                    //Adds Keyboard Shortcut Alt-T
                    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,
                            ActionEvent.ALT_MASK));
                    addActionListener(new MenuItemListener());
                }
                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        setWindow(toDoListPanel);
                        updateLists();
                    }
                }
            }
            
       }

        //Help Menu
       class HelpMenu extends JMenu
       {

            public HelpMenu()
            {
                super("Help");
                //add(new AboutMenuItem());
                add(new HelpMenuItem());
                add(new HelpPomodoroTechnique());
                add(new HelpPomodoroCheatSheet());
                add(new HelpPomodoroBook());
            }

            class HelpMenuItem extends JMenuItem
            {
                public HelpMenuItem()
                {
                    super("Help System");
                    //Adds Keyboard Shortcut Alt-H
                    setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H,
                            ActionEvent.ALT_MASK));
                    addActionListener(new MenuItemListener());
                }
                class MenuItemListener implements ActionListener
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        // Identify the location of the help set file
                        String pathToHS = "/docs/helpset.xml";
                        //Create a URL for the location of the help set
                        try
                        {
                            URL hsURL = getClass().getResource(pathToHS);
                            hs = new HelpSet(null, hsURL);
                        }
                        catch (Exception ee)
                        {
                            // Print info to the console if there is an exception
                            System.out.println("HelpSet " + ee.getMessage());
                            System.out.println("Help Set " + pathToHS + " not found");
                            return;
                        }

                        // Create a HelpBroker object for manipulating the help set
                        hb = hs.createHelpBroker();
                        //Display help set
                        hb.setDisplayed(true);
                        //Sets Location relative to App
                        hb.setLocation(MyPomodoroView.this.getLocation());
                    }
                }
            }

            class HelpPomodoroTechnique extends JMenuItem
            {

                public HelpPomodoroTechnique()
                {
                    super("The Pomodoro Technique Website");
                    addActionListener(new MenuItemListener());
                }

                class MenuItemListener implements ActionListener
                {

                    public void actionPerformed(ActionEvent e)
                    {
                        final JTextField urlField = new JTextField("http://www.pomodorotechnique.com/");
                        BareBonesBrowserLaunch.openURL(urlField.getText().trim());
                    }
                }
            }

            class HelpPomodoroCheatSheet extends JMenuItem
            {

                public HelpPomodoroCheatSheet()
                {
                    super("Download The Pomodoro Technique Cheat Sheet");
                    addActionListener(new MenuItemListener());
                }

                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        final JTextField urlField = new JTextField("http://www.pomodorotechnique.com/downloads/pomodoro_cheat_sheet.pdf");
                        BareBonesBrowserLaunch.openURL(urlField.getText().trim());
                    }
                }
            }

            class HelpPomodoroBook extends JMenuItem
            {

                public HelpPomodoroBook()
                {
                    super("Download The Pomodoro Technique Book");
                    addActionListener(new MenuItemListener());
                }

                class MenuItemListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        final JTextField urlField = new JTextField("http://www.pomodorotechnique.com/resources/cirillo/ThePomodoroTechnique_v1-3.pdf");
                        BareBonesBrowserLaunch.openURL(urlField.getText().trim());
                    }
                }
            }
        }
    }

    class SplashScreen extends JPanel
    {
        public SplashScreen()
        {
            setBackground(Color.white);
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            JLabel title = new JLabel("myPomodoro");
            title.setFont(title.getFont().deriveFont(48f));
            title.setForeground(Color.red);



            c.gridx=0;
            c.gridy =0;
            c.fill = GridBagConstraints.BOTH;
            //add(title, c);
            c.gridy = 1;
            add(new JLabel(new ImageIcon(Main.class.getResource("resources/images/pomodoroTechnique128.png")), JLabel.CENTER), c);
        }
    }

    class ControlPanel extends JPanel
    {

        private TimerValueSlider pomodoroSlider;
        private TimerValueSlider shortBreakSlider;
        private TimerValueSlider longBreakSlider;
        
        public ControlPanel()
        {
            int pomtime = (int)toDoListPanel.getPomodoro()/60000;
            int shorttime = (int)toDoListPanel.getShortBreak()/60000;
            int longtime = (int)toDoListPanel.getLongBreak()/60000;
            pomodoroSlider =
                new TimerValueSlider(0, 45, pomtime, "Pomodoro Length: ");
            shortBreakSlider =
                new TimerValueSlider(0, 10, shorttime, "Short Break Length: ");
            longBreakSlider =
                new TimerValueSlider(0, 120, longtime, "Long Break Length: ");
            setBackground(Color.white);
            setLayout(new GridBagLayout());
            GridBagConstraints c= new GridBagConstraints();

            JButton setValue = new JButton("Save Settings");
            setValue.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    toDoListPanel.setPomodoro(pomodoroSlider.getSliderValue()*60000);
                    toDoListPanel.setLongBreak(longBreakSlider.getSliderValue()*60000);
                    toDoListPanel.setShortBreak(shortBreakSlider.getSliderValue()*60000);
                    menuBar.setWindow(iconBar.getSelectedIcon().getPanel());
                }
            });
            c.gridx=0;
            c.gridy=0;
            c.weighty = .5;
            c.fill = GridBagConstraints.BOTH;
            add(pomodoroSlider, c);
            c.gridy=1;
            add(shortBreakSlider, c);
            c.gridy=2;
            add(longBreakSlider, c);
            c.gridy=3;
            c.fill = GridBagConstraints.NONE;
            add(setValue, c);
        }

        class TimerValueSlider extends JPanel
        {
            private JSlider slider;
            private JLabel label = new JLabel();
            public TimerValueSlider(int min, int max, int val, String name)
            {
                super();
                setBackground(Color.white);
                setLayout(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.gridx=0;
                c.gridy=0;
                add(new JLabel(name));
                c.gridx=1;
                
                slider = new JSlider(min, max, val);
                slider.addChangeListener(new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        label.setText(""+slider.getValue()+" minutes");
                    }
                });
                add(slider, c);
                c.gridx=2;
                label.setText(""+slider.getValue()+" minutes");
                add(label,c);

            }
            public int getSliderValue()
            {
                return slider.getValue();
            }
        }
    }
}
