package mypomodoro;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;

/**
 * GUI for viewing the Report List.
 *
 * @author Brian Wetzel
 */
public class ReportListPanel extends JPanel
{
    //table for displaying completed activities
    private ReportTable table = new ReportTable();
    //information panel for selected activities
    private InformationPanel iPanel = new InformationPanel();
    //static variable for selecting the id
    public static final int ID = 7;
    public ReportListPanel()
    {
        super();
        setLayout(new BorderLayout());
        setBorder(new TitledBorder(new EtchedBorder(), "Report List"));
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(iPanel, BorderLayout.SOUTH);
    }

    public ReportTable getTable() {
        return table;
    }

    class ReportTable extends JTable
    {

        public ReportTable()
        {
            super(new ReportTableModel());
            this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    int id = (Integer)table.getModel().
                    getValueAt(table.getSelectedRow(), ID);
                    iPanel.setData(id);
                }
            });
        }

        public void updateModel()
        {
            System.out.println("settingmodel..");
            this.setModel(new ReportTableModel());
        }
    }

    /**
     * Table Model for the ActivityList table.
     */
    class ReportTableModel extends AbstractTableModel
    {

        private String[] columnNames =
        {
            "Date",
            "Name",
            "Estimated Poms",
            "Actual Poms",
            "# Interruptions",
            "Unplanned",
            "Voided",
            "ID"
        };
        Object[][] tableData;

        ;

        public ReportTableModel()
        {
            super();
            populateData();
        }

        /**
         * Populates the table using the serializer.
         */
        private void populateData()
        {
            ReportList ac = ReportList.getList();
            int rowIndex = ac.size();
            int colIndex = columnNames.length;
            tableData = new Object[rowIndex][colIndex];
            for (int i = 0; i < ac.size(); i++)
            {
                
                tableData[i][0] = ac.get(i).getDate();
                tableData[i][1] = ac.get(i).getName();
                tableData[i][2] = ac.get(i).getEstimatedPoms();
                tableData[i][3] = ac.get(i).getActualPoms();
                tableData[i][4] = ac.get(i).getNumInterruptions();
                tableData[i][5] = ac.get(i).isUnplanned();
                tableData[i][6] = ac.get(i).isVoided();
                tableData[i][7] = ac.get(i).getId();
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
            return tableData[rowIndex][columnIndex];
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

    class InformationPanel extends JPanel
    {
        public InformationPanel()
        {
            Dimension d = new Dimension(450, 125);
            setMinimumSize(d);
            setPreferredSize(d);
            setLayout(new GridLayout(1,1));
            if(ReportList.getList().size() > 0)
                setData((Integer)table.getModel().getValueAt(0, ID));
        }

        class InformationArea extends JTextArea
        {
            public InformationArea(int i)
            {
                if(ReportList.getList().size() > 0)
                {
                    setEditable(false);
                    Activity currentActivity =
                        ReportList.getList().get(0);

                    //get the activity by the index number
                    for(Activity a : ReportList.getList()){
                        if(a.getId() == i) currentActivity = a;
                    }

                    double accuracy = 1.0*currentActivity.getEstimatedPoms() / currentActivity.getActualPoms();

                    String accuracyText;

                    if (accuracy == 1.0) {

                        accuracyText = ""+accuracy;

                    } else if (accuracy > 1) {

                        accuracyText = ""+accuracy;

                    } else {

                        accuracyText = ""+accuracy;

                    }

                    String text = "Author's Name: " + currentActivity.getName()
                            + "\nDate Created: " + currentActivity.getDate()
                            + "\nActivity Name: " + currentActivity.getName()
                            + "\nType of Activity: " + currentActivity.getType()
                            + "\nDescription:" + currentActivity.getDescription()
                            + "\nEstimated Pomodoros: " + currentActivity.getEstimatedPoms()
                            + "\nActual Pomodoros: " + currentActivity.getActualPoms()
                            + "\nEstimation Accuracy: " + accuracyText
                            + "\nThis task had "
                            + currentActivity.getNumInterruptions()
                            + " Interruptions. ";
                    setText(text);
                }
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
    }
}
