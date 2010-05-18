package mypomodoro;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 *
 * @author Brian Wetzel
 */
public class GeneratePanel extends JPanel
{
    //ToDo List to be generated using button controls

    private ToDoList toDoList = ToDoList.getList();
    private ActivityList activityList = ActivityList.getList();

    //JList of activities for Activity List.
    private ActivityJList activityJList = new ActivityJList();
    //JList of activities for ToDo List.
    private ToDoJList toDoJList = new ToDoJList();
    //Activity List information panel
    private InformationPanel activityIPanel =
            new InformationPanel();
    //ToDo List information panel
    private InformationPanel toDoIPanel =
            new InformationPanel();
    //width of list cells
    public static final int CELL_WIDTH = 200;

    public GeneratePanel()
    {
        super();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = .33;
        c.weighty =1.0;
        add(new ListPane(activityJList, activityIPanel), c);
        c.gridx =1;
        c.fill = GridBagConstraints.NONE;
        add(new ControlPanel(), c);
        c.gridx =2;
        c.fill = GridBagConstraints.BOTH;
        add(new ListPane(toDoJList, toDoIPanel), c);
    }

    class ListPane extends JPanel
    {
        public ListPane(JList list, JPanel panel)
        {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 1.0;
            c.weighty = 0.8;
            c.fill = GridBagConstraints.BOTH;
            add(new JScrollPane(list), c);
            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 1.0;
            add(new JScrollPane(panel), c);
        }
    }

    class InformationPanel extends JPanel
    {
        public InformationPanel()
        {
            Dimension d = new Dimension(225, 75);
            setBorder(new TitledBorder(new EtchedBorder(), "Details"));
            setMinimumSize(d);
            setPreferredSize(d);
            setMaximumSize(d);
            setLayout(new GridLayout(1,1));
        }

        class InformationArea extends JTextArea
        {
            public InformationArea(Activity a)
            {
                Activity currentActivity = a;
                
                setEditable(false);

                String text = "Name: " + currentActivity.getName()
                        + "\nDescription:" + currentActivity.getDescription()
                        + "\nEstimated Pomodoros: " + currentActivity.getEstimatedPoms();
                setText(text);
            }
            public InformationArea()
            {
               setEditable(false);
               setText("");
            }
        }
        public void setData(Activity a)
        {
            removeAll();
            Dimension min = new Dimension(450, 70);
            InformationArea iArea = new InformationArea(a);
            iArea.setMinimumSize(min);
            JScrollPane jsp = new JScrollPane(iArea);
            jsp.setMinimumSize(min);
            add(jsp);
            revalidate();
        }

        public void clearData()
        {
            removeAll();
            Dimension min = new Dimension(450, 70);
            InformationArea iArea = new InformationArea();
            iArea.setMinimumSize(min);
            JScrollPane jsp = new JScrollPane(iArea);
            jsp.setMinimumSize(min);
            add(jsp);
            revalidate();
        }
    }

    class ActivityJList extends JList
    {
        public ActivityJList()
        {
            super(ActivityList.getList().toArray(new Activity[0]));
            //setFixedCellWidth(CELL_WIDTH);
            setBorder(new TitledBorder(new EtchedBorder(), "Activity List"));
            addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    ActivityJList list = (ActivityJList) e.getSource();
                    if(list.getSelectedValue() != null)
                    {
                        activityIPanel.setData((Activity)list.getSelectedValue());
                    }
                    else
                    {
                        activityIPanel.clearData();
                    }
                }
            });
        }

        public void refresh()
        {
            setListData((Activity[]) activityList.toArray(new Activity[0]));
        }


    }

    public ActivityJList getActivityJList() {
        return activityJList;
    }

    

    class ToDoJList extends JList
    {
        public ToDoJList()
        {
            super(toDoList.toArray(new Activity[0]));
            setFixedCellWidth(CELL_WIDTH);
            setBorder(new TitledBorder(new EtchedBorder(), "ToDo List"));
            addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    ToDoJList list = (ToDoJList) e.getSource();
                    if(list.getSelectedValue() != null)
                    {
                        toDoIPanel.setData((Activity)list.getSelectedValue());
                    }
                    else
                    {
                        toDoIPanel.clearData();
                    }
                }
            });
        }

        public void refresh()
        {
            setListData((Activity[]) toDoList.toArray(new Activity[0]));
        }
    }

    public ToDoJList getToDoJList() {
        return toDoJList;
    }

    class ControlPanel extends JPanel
    {
        public ControlPanel()
        {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            Dimension buttonSize = new Dimension(100, 30);

            //AddButton
            AddButton addButton = new AddButton();
            addButton.setMinimumSize(buttonSize);
            addButton.setPreferredSize(buttonSize);
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            add(addButton, gbc);

            //Remove Button
            RemoveButton removeButton = new RemoveButton();
            removeButton.setMinimumSize(buttonSize);
            removeButton.setPreferredSize(buttonSize);
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            add(removeButton, gbc);

            //Promote Button
            PromoteButton promoteButton = new PromoteButton();
            promoteButton.setMinimumSize(buttonSize);
            promoteButton.setPreferredSize(buttonSize);
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            add(promoteButton, gbc);

            //Demote Button
            DemoteButton demoteButton = new DemoteButton();
            demoteButton.setMinimumSize(buttonSize);
            demoteButton.setPreferredSize(buttonSize);
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            add(demoteButton, gbc);
        }

        //add activity button class
        class AddButton extends JButton
        {
            public AddButton()
            {
                super("ADD");
                addActionListener(new ButtonListener());
            }

            //action listener for add button.
            class ButtonListener implements ActionListener
            {
                public void actionPerformed(ActionEvent e)
                {
                    Activity activity = (Activity) activityJList.getSelectedValue();
                    activityList.remove(activity);
                    toDoList.addActivity(activity);
                   // Main.updateView();
                    activityJList.refresh();
                    toDoJList.refresh();
                    toDoJList.setSelectedIndex(toDoJList.getModel().getSize()-1);
                    
                    revalidate();
                }
            }
        }

        //remove activity button class
        class RemoveButton extends JButton
        {
            public RemoveButton()
            {
                super("REMOVE");
                addActionListener(new ButtonListener());
            }

            //action listener for add button.
            class ButtonListener implements ActionListener
            {
                public void actionPerformed(ActionEvent e)
                {
                    Activity activity = (Activity)toDoJList.getSelectedValue();
                    toDoList.removeActivity(activity);
                    activityList.add(activity);
                   // Main.updateView();
                    activityJList.refresh();
                    toDoJList.refresh();
                    activityJList.setSelectedIndex(activityJList.getModel().getSize() -1);
                    
                    revalidate();
                }
            }
        }

            //remove activity button class
            class PromoteButton extends JButton
            {
                public PromoteButton()
                {
                    super("PROMOTE");
                    addActionListener(new ButtonListener());
                }

                //action listener for add button.
                class ButtonListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        int index = toDoJList.getSelectedIndex();
                        if(index > 0)
                        {
                            Activity activity = (Activity)toDoJList.getSelectedValue();
                            toDoList.promote(activity);
                            toDoJList.refresh();
                            toDoJList.setSelectedIndex(index - 1);
                            //Main.updateView();
                        }
                    }
                }
            }

            class DemoteButton extends JButton
            {
                public DemoteButton()
                {
                    super("DEMOTE");
                    addActionListener(new ButtonListener());
                }

                //action listener for add button.
                class ButtonListener implements ActionListener
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        int index = toDoJList.getSelectedIndex();
                        if(index < (toDoList.size()-1))
                        {
                            toDoList.demote(index);
                            toDoJList.refresh();
                            toDoJList.setSelectedIndex(index + 1);
                            //Main.updateView();
                        }
                    }
                }
            }

    }
}
