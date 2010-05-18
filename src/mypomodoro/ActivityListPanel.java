package mypomodoro;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionListener;

/**
 * GUI for viewing what is in the ActivityList. This can be changed later.
 * Right now it uses a TableModel to build the JTable.  Table Listeners can
 * be added to save cell edits to the ActivityCollection which can then be
 * saved to the data layer.
 *
 * @author Brian Wetzel
 */
public class ActivityListPanel extends JPanel
{
    ActivityListTable table = new ActivityListTable();
    InformationPanel iPanel = new InformationPanel();
    public static final int ID = 4;
    public ActivityListPanel()
    {
        super();
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        setBorder(new TitledBorder(new EtchedBorder(), "Activity List"));

        //Add the table
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(new JScrollPane(table), gbc);

        //Add the Control Panel
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.2;
        add(new TabPane(), gbc);
    }

    public ActivityListTable getTable() {
        return table;
    }

    public void setTable(ActivityListTable table) {
        this.table = table;
    }

    

    class TabPane extends JTabbedPane
    {
        public TabPane()
        {

            add("Details", new ControlPanel());
            add("Create Task", new JScrollPane(new ActivityCreatePanel()));
        }
    }

    class ControlPanel extends JPanel
    {
        public ControlPanel()
        {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            //Add the information panel
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weighty = GridBagConstraints.REMAINDER;
            gbc.weightx = 0.8;
            add(iPanel, gbc);

            //Add the delete button
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 0.2;
            add(new DeleteButton(), gbc);
        }
    }

    class DeleteButton extends JButton
    {
        public DeleteButton()
        {
            super("Delete");
            addActionListener(new ButtonListener());
        }

        class ButtonListener implements ActionListener
        {
            public void actionPerformed(ActionEvent e)
            {
               int index = table.getSelectedRow();
               //make sure this is pointing to the id value of the table.
               int id = (Integer)table.getModel().getValueAt(index, ID);               
               ActivityList.getList().removeById(id);
               iPanel.clearData();
               System.out.println("removed");
            }
         }
    }

    class InformationPanel extends JPanel
    {
        public InformationPanel()
        {
            Dimension d = new Dimension(450, 125);
            setMinimumSize(d);
            setPreferredSize(d);
            setLayout(new GridLayout(1,1));
            if(ActivityList.getList().size() > 0)
                setData((Integer)table.getModel().getValueAt(0, ID));
        }

        class InformationArea extends JTextArea
        {
            public InformationArea(int i)
            {
                if(ActivityList.getList().size() > 0)
                {
                    setEditable(false);
                    Activity currentActivity =
                        ActivityList.getList().get(0);
                    
                    //get the activity by the index number
                    for(Activity a : ActivityList.getList()){
                        if(a.getId() == i) currentActivity = a;
                    }
                    
                     String text = "Place: " + currentActivity.getPlace()
                            + "\nAuthor's Name: " + currentActivity.getName()
                            + "\nDate Created: " + currentActivity.getDate()
                            + "\nActivity Name: " + currentActivity.getName()
                            + "\nType of Activity: " + currentActivity.getType()
                            + "\nDescription:" + currentActivity.getDescription()
                            + "\nEstimated Pomodoros: " + currentActivity.getEstimatedPoms();
                    setText(text);
                }
            }
            public InformationArea()
            {
                setEditable(false);
                setText("");
            }
        }
        public void setData(int i)
        {
            removeAll();
            Dimension min = new Dimension(450, 70);
            InformationArea iArea = new InformationArea(i);
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

    class ActivityListTable extends JTable
    {
        public ActivityListTable()
        {
            super(new ALTableModel());
            
            this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                   int id, row  = 0;
                   row = table.getSelectedRow();
                   //Added check to handle when the row is deleted.  This results in getSelectedRow() returning -1
                   //Other uses should return a valid row number.  This was added to prevent an out of range exception
                   if (row >= 0)
                   {
                       id = (Integer)table.getModel().getValueAt(row, ID);
                       iPanel.setData(id);
                   }
                }
            });
        }

        public void updateModel()
        {
            System.out.println("settingmodel..1");
            this.setModel(new ALTableModel());
        }
    }

    /**
     * Table Model for the ActivityList table.
     */
    class ALTableModel extends AbstractTableModel
    {

        private String[] columnNames =
        {
            "Date",
            "Name",
            "Type",
            "Estpomo",
            "ID"
        };
        Object[][] tableData;;

        public ALTableModel()
        {
            super();
            populateData();
        }

        /**
         * Populates the table from the database.
         */
        private void populateData()
        {
            ActivityList ac = ActivityList.getList();
            System.out.println(ac.toString()+" "+ac.size());
            
            int rowIndex = ac.size();

            int colIndex = columnNames.length;
            tableData = new Object[rowIndex][colIndex];
            for (int i = 0; i < rowIndex; i++)
            {

                tableData[i][0] = ac.get(i).getDate();
                tableData[i][1] = ac.get(i).getName();
                tableData[i][2] = ac.get(i).getType();
                tableData[i][3] = ac.get(i).getEstimatedPoms();
                tableData[i][4] = ac.get(i).getId();
            }
        }
        
        public int getRowCount()
        {
            return tableData.length;
        }

        public int getColumnCount()
        {
            return columnNames.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex)
       {
           if (rowIndex >= 0)
               return tableData[rowIndex][columnIndex];
           else
                   return null;
       }

        @Override
        public String getColumnName(int column)
        {
            return columnNames[column];
        }

        @Override
        public Class getColumnClass(int c)
        {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return false;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            tableData[rowIndex][columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    class ActivityCreatePanel extends CreatePanel
    {
        public ActivityCreatePanel()
        {
            super();
            for(ActionListener a: sbutton.getActionListeners())
            {
                sbutton.removeActionListener(a);
            }
            sbutton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    saveData();
                    clearForm();
                    table.updateModel();
                }
            });
        }

    }
}
