package mypomodoro;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.border.*;

/**
 * Panel that keeps that time, and does all the stuff with the ToDo List.
 * Uses a pomodoro timer and a break timer, each looping
 * into the other without stopping.
 *
 * @author Brian Wetzel
 */
public class ToDoListPanel extends JPanel
{
    //ToDoList, contains activities
    private ToDoList toDoList = ToDoList.getList();
    //JList component for the ToDo List
    private ToDoJList toDoJList = new ToDoJList();

    //Constant for the length of a pomodoro
    private long pomodoro = 1500000;
    //public static final long POMODORO = 1500000;
    //Constant for the length of a short break
    private long shortBreak = 300000;
    //public static final long SHORT_BREAK = 300000;
    //Constant for the length of a long break
    private long longBreak = 2700000;
    //public static final long LONG_BREAK = 2700000;
    //Which break are we on?
    //default is the first break...up to 4.
    private int nextBreak = 1;
    //
    PomCountdown pomCD = new PomCountdown();
    //Pomodoro Timer, contructed with a Countdown ActionListener
    private Timer pomTimer = new Timer(1000, pomCD);
    //Break Timer constructed with a Countdown ActionListener.
    private Timer breakTimer = new Timer(1000, new BreakCountdown());
    //Simple date format for the timer label (needs to be refrenced by
    //multiple ActionListeners
    private java.text.SimpleDateFormat sdf =
            new java.text.SimpleDateFormat("mm : ss");
    //Timer Label ... Could improve this by changing the font.
    private JLabel pomodoroTimer =
        new JLabel(sdf.format(new java.util.Date(pomodoro)));
    //Information Panel goes in the tabpane
    private InformationPanel informationPanel = new InformationPanel();
    //Interrupt Panel goes in the tab pane
    private CreatePanel interruptPanel = new InterruptPanel();
    //width of list cells
    public static final int CELL_WIDTH = 200;

    /**
     * Default Constructor for ToDoList Panel.
     */
    public ToDoListPanel() throws IOException, FontFormatException
    {
        super();
        setBackground(Color.white);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        //Add the To Do list
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.weighty = .5;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(toDoJList), gbc);

        //Add a timer panel
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = .3;
        gbc.weighty=.5;
        add(wrapInBackgroundImage(new TimerPanel(),
                new ImageIcon(Main.class.getResource("resources/images/myPomodoroIconNoTime250.png")), JLabel.TOP, JLabel.LEADING), gbc);


        //Add the tab pane
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        add(new TabPane(), gbc);
    }

    public long getPomodoro() {
        return pomodoro;
    }

    public void setPomodoro(long pomodoro)
    {
        
        this.pomodoro = pomodoro;
        if(!this.pomTimer.isRunning())
        {
            this.pomodoroTimer.setText(sdf.format(new java.util.Date(pomodoro)));
           ((PomCountdown)pomTimer.getActionListeners()[0]).updateTimer();
        }
    }

    public long getLongBreak() {
        return longBreak;
    }

    public void setLongBreak(long longBreak) {
        this.longBreak = longBreak;
        ((BreakCountdown)(breakTimer.getActionListeners()[0])).setBreakLength();
    }

    public long getShortBreak() {
        return shortBreak;
    }

    public void setShortBreak(long shortBreak) {
        this.shortBreak = shortBreak;
        ((BreakCountdown)(breakTimer.getActionListeners()[0])).setBreakLength();
    }



    public void refresh()
    {
        toDoJList.refresh();
    }

    /**
     * JPanel for the ToDO list.
     */
    class ToDoJList extends JList
    {
        public ToDoJList()
        {
            super(toDoList.toArray(new Activity[0]));
            setFixedCellWidth(CELL_WIDTH);
            setBorder(new TitledBorder(new EtchedBorder(), "ToDo List"));
        }

        public void refresh()
        {
            setListData((Activity[]) toDoList.toArray(new Activity[0]));
        }
    }

    public ToDoJList getToDoJList() {
        return toDoJList;
    }

    

    class TabPane extends JTabbedPane
    {
        public TabPane()
        {
            setBackground(Color.white);
            add("Details", informationPanel);
            add("Interrupt", new JScrollPane(interruptPanel));
        }
    }

    class TimerPanel extends JPanel
    {
        TimerPanel() throws java.io.IOException, FontFormatException
        {
            pomodoroTimer.setFont(Font.createFont(Font.TRUETYPE_FONT, Main.class.getResourceAsStream("resources/DS-DIGIB.TTF")));
            pomodoroTimer.setForeground(Color.DARK_GRAY);
            setPreferredSize(new Dimension(250, 175));
            setBackground(Color.white);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            //Add the Pomodoro Timer
             gbc.gridx = 0;
             gbc.gridy = 0;
             gbc.fill = GridBagConstraints.NONE;
             gbc.weighty = .3;
             gbc.anchor = GridBagConstraints.SOUTH;
             pomodoroTimer.setFont(pomodoroTimer.getFont().deriveFont(40f));
             add(pomodoroTimer, gbc);

            //Add the Start Button
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weighty = 0.165;
            gbc.anchor = GridBagConstraints.SOUTH;
            StartButton startButton = new StartButton();
            startButton.setFont(startButton.getFont().deriveFont(20f));
            add(startButton, gbc);
        }
    }

    public static JPanel wrapInBackgroundImage(JComponent component,
            Icon backgroundIcon,
            int verticalAlignment,
            int horizontalAlignment) {

                // make the passed in swing component transparent
                component.setOpaque(false);

                // create wrapper JPanel
                JPanel backgroundPanel = new JPanel(new GridBagLayout());
                backgroundPanel.setBackground(Color.white);
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx=0;
                gbc.gridy=0;
                // add the passed in swing component first to ensure that it is in front
                backgroundPanel.add(component, gbc);

                // create a label to paint the background image
                JLabel backgroundImage = new JLabel(backgroundIcon);

                // set minimum and preferred sizes so that the size of the image
                // does not affect the layout size
                backgroundImage.setPreferredSize(new Dimension(250,250));
                backgroundImage.setMinimumSize(new Dimension(250,250));

                // align the image as specified.
                backgroundImage.setVerticalAlignment(verticalAlignment);
                backgroundImage.setHorizontalAlignment(horizontalAlignment);

                // add the background label
                backgroundPanel.add(backgroundImage, gbc);

                // return the wrapper
                return backgroundPanel;
            }

    /**
     * Panel that displays information on the current Pomodoro...this should
     * be updated when the ToDo list is updated.
     */
    class InformationPanel extends JPanel
    {
        public InformationPanel()
        {
            refresh();
        }

        class InformationArea extends JTextArea
        {
            public InformationArea()
            {
                if(toDoList.size() > 0)
                {
                    setBorder(new EtchedBorder());
                    setEditable(false);
                    Activity currentActivity = toDoList.get(0);
                    String text = "Name: " + currentActivity.getName()
                            + "\nDescription:" + currentActivity.getDescription()
                            + "\nEstimated Pomodoros: " + currentActivity.getEstimatedPoms()
                            + "\nActual Pomodoros: " + currentActivity.getActualPoms()
                            + "\nInterruptions: " + currentActivity.getNumInterruptions();
                    setText(text);
                }
                else
                {
                    
                }
            }
        }
        public void refresh()
        {
            removeAll();
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            //add the information area
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx =1.0;
            gbc.weighty = 1.0;
            gbc.gridheight = GridBagConstraints.REMAINDER;
            add(new JScrollPane(new InformationArea()), gbc);

            //add complete task button
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx =0.1;
            gbc.fill = GridBagConstraints.NONE;
            add(new CompleteButton(), gbc);

            //add void task button
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.weightx = 0.1;
            gbc.fill = GridBagConstraints.NONE;
            add(new VoidButton(), gbc);
        }
    }


    /**
     * Start Button starts the Pomodoro Timer. (cannot be stopped??)
     */
    class StartButton extends JButton
    {
        public StartButton()
        {
            super("Start");
            addActionListener(new ButtonListener());
            setForeground(Color.green);
        }
        
        class ButtonListener implements ActionListener
        {
            public void actionPerformed(ActionEvent e)
            {
                pomTimer.start();
            }
         }
    }

    /**
     * Lets Listeners know that the current activity is completed.
     */
    class CompleteButton extends JButton
    {
        public CompleteButton()
        {
            super("Task Complete");
            addActionListener(new ButtonListener());
        }

        class ButtonListener implements ActionListener
        {
            public void actionPerformed(ActionEvent e)
            {
                toDoList.get(0).setIsCompleted(true);
            }
         }
    }

    class VoidButton extends JButton
    {
        public VoidButton()
        {
            super("Void Task");
            addActionListener(new ButtonListener());
        }

        class ButtonListener implements ActionListener
        {
            public void actionPerformed(ActionEvent e)
            {
                if(!toDoList.get(0).isCompleted())
                {
                    toDoList.get(0).setIsVoided(true);
                    completeActivity();
                    pomTimer.stop();
                    ((PomCountdown)pomTimer.getActionListeners()[0]).updateTimer();
                    breakTimer.stop();
                    pomodoroTimer.setText(sdf.format(new java.util.Date(pomodoro)));
                    System.out.println(toDoList.get(0).isVoided());
                }
            }
         }
    }

    /**
     * Countdown listener for a pomodoro
     * should add the activity to the report list if the activity is completed
     * at the end of a pomodoro. (This should also be checked at the end of a break)
     * Otherwise increment the pomodoros for the current activity.
     */
    class PomCountdown implements ActionListener
    {
        private long time = pomodoro - 1000;
        public void actionPerformed(ActionEvent e)
        {            
            if (time >= 0)
            {
                pomodoroTimer.setText(sdf.format(new java.util.Date(time)));
                time -= 1000;
            }
            else
            {
                Toolkit.getDefaultToolkit().beep();
                System.out.println("Going on Break");
                pomTimer.stop();
                time = pomodoro - 1000; //reset the timer

                //Check and see if the current task is completed.
                if(toDoList.get(0).isCompleted())
                {
                    completeActivity();
                    if(toDoList.isEmpty())
                    {
                        //pomodoroTimer.setText("List Complete");
                        pomTimer.stop();
                    }
                }
                else
                {
                    toDoList.get(0).incrementPoms();
                    informationPanel.refresh();
                    System.out.println("poms: "+toDoList.get(0).getActualPoms());
                }
                breakTimer.restart();
            }
        }
        public void updateTimer()
        {
            time = pomodoro - 1000;
        }
        public long getTime()
        {
            return time;
        }
    }

    /**
     * Coundown Listener for the break.  Should remove a pomodoro from the
     * todo list and add it to the report list if it is complete by the end
     * of this break. (someone could forget to click it at the end of the pomodoro,
     * they would not want to start a new pomodoro on that task.)
     * The timer will long breaks every four breaks.
     */
    class BreakCountdown implements ActionListener
    {
        private long time = getBreakLength();
        public void actionPerformed(ActionEvent e)
        {
            if (time >= 0)
            {
                pomodoroTimer.setText(sdf.format(new java.util.Date(time)));
                time -= 1000;
            }
            else
            {
                Toolkit.getDefaultToolkit().beep();
                System.out.println("Starting Pomodoro");
                breakTimer.stop();
                time = getBreakLength(); //reset the timer.
                
                //Check and see if the current task is completed.
                if(toDoList.get(0).isCompleted())
                {
                    completeActivity();
                    if(toDoList.isEmpty())
                    {
                        pomodoroTimer.setText("List Complete");
                        pomTimer.stop();
                    }
                }

                //Start the pomodoro timer.
                pomTimer.start();
            }
        }

        /**
         * Method is needed to change the break length to long break every four breaks.
         * @return
         */
        private long getBreakLength()
        {
            time = shortBreak - 1000;
            if(nextBreak == 4)
            {
                time = longBreak - 1000;
                nextBreak = 1;
            }
            else nextBreak++;
            System.out.println("break: "+nextBreak);
            return time;
        }

        public void setBreakLength()
        {
            time = shortBreak - 1000;
            if(nextBreak == 1)
            {
                time = longBreak - 1000;
            }
        }
    }

    class InterruptPanel extends CreatePanel
    {
        public InterruptPanel()
        {
            super();
            removeAll();
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1.0;
            c.weighty = 0.80;
            c.fill = GridBagConstraints.BOTH;
            add(inputFormPanel, c);
            c.gridx =0;
            c.gridy = 1;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.NORTH;
            c.weighty = 0.2;
            add(new SaveButton2(), c);
        }

        class SaveButton2 extends JButton
        {
            public SaveButton2()
            {
                super("Save Interruption");
                addActionListener(new ButtonListener());
            }

            class ButtonListener implements ActionListener
            {

                public void actionPerformed(ActionEvent event)
                {                    
                    saveData();
                    clearForm();
                    toDoList.get(0).incrementInter();
                    toDoJList.refresh();
                    informationPanel.refresh();                    
                }
            }
        }

        public void saveData()
        {
            String place = placeTF.getText();
            String author = authorTF.getText();
            String name = nameTF.getText();
            String description = descriptionTA.getText();
            String type = typeTF.getText();
            int estimatedPoms = 0;
            try
            {
                estimatedPoms =(!estimatedPomsTF.getText().equals("")) ?
                    Integer.parseInt(estimatedPomsTF.getText()) : 0;
            } catch (NumberFormatException e)
            {

            }

            Activity newActivity =
                    new Activity(place, author, name, description, type, estimatedPoms);
            newActivity.setIsUnplanned(true);
            System.out.println(newActivity.toString());
            System.out.println(newActivity.isValid());
            if(newActivity.isValid())
            {
                ActivityList.getList().add(newActivity);
                validation.setForeground(Color.green);
                newActivity.databaseInsert();
                validation.setText("Activity Added.");
            }
            else
            {
                validation.setForeground(Color.red);
                validation.setText("Invalid Input.");
            }
            System.out.println(ActivityList.getList().toString());
        }
    }

    /**
     * Method to complete the current activity.  Will remove the activity from
     * the current place in the ToDoList and add it to the tail of the report
     * List.
     */
    private void completeActivity()
    {
        toDoList.complete();
        toDoJList.refresh();
        informationPanel.refresh();
    }
}
