package org.mypomodoro.gui.manager;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;

import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mypomodoro.gui.ActivityInformationListListener;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.gui.activities.ActivityInformationPanel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

/**
 * This class just abstracts a JPanel for jlist and a information panel for the
 * items in the list.
 *
 */
public class ListPanel extends ActivityInformationPanel {

    private static final long serialVersionUID = 20110814L;
    protected final GridBagConstraints c = new GridBagConstraints();
    protected final JList internalActivitiesList;
    private final AbstractActivities list;
    private int selectedRowIndex = 0;
    final private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    // width of list cells
    public static final int CELL_WIDTH = 250;
    private static final Dimension PREFERED_SIZE = new Dimension(250, 100);

    public ListPanel(AbstractActivities list) {
        this.list = list;
        setLayout(new GridBagLayout());
        internalActivitiesList = new JList();
        internalActivitiesList.setCellRenderer(new cellRenderer());
        setPreferredSize(PREFERED_SIZE);
        internalActivitiesList.setFont(new Font(this.getFont().getName(), Font.PLAIN, this.getFont().getSize()));

        internalActivitiesList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                setSelectedRowIndex();
            }
        });
        addActivitiesList();
        addInformationArea();
    }

    protected void addActivitiesList() {
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        //c.weighty = 0.8;
        c.fill = GridBagConstraints.BOTH;
        internalActivitiesList.setFixedCellWidth(CELL_WIDTH);
        update();
        add(new JScrollPane(internalActivitiesList), c);
        internalActivitiesList.addListSelectionListener(new ActivityInformationListListener(
                this));
    }
    
    protected void addInformationArea() {
        addInformationArea(1);
    }

    protected void addInformationArea(int gridy) {
        c.gridx = 0;
        c.gridy = gridy;
        c.weightx = 1.0;
        //c.weighty = 0.2;
        informationArea.setEditable(false);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        add(new JScrollPane(informationArea), c);
    }

    public void removeActivity(Activity activity) {
        list.remove(activity);
        update();
    }

    public Activity getSelectedActivity() {
        return (Activity) internalActivitiesList.getSelectedValue();
    }

    public List<Activity> getSelectedActivities() {
        // jdk 6
        Object objectArray[] = internalActivitiesList.getSelectedValues();
        Activity[] activityArray = Arrays.copyOf(objectArray, objectArray.length, Activity[].class);
        return Arrays.asList(activityArray);
        // jdk7 
        //return internalActivitiesList.getSelectedValuesList();
    }

    public void update() {
        internalActivitiesList.setListData(list.toArray());
        init();
    }

    public void addActivity(Activity activity) {
        list.add(activity);
        update();
    }

    public void addListMouseListener(MouseListener listener) {
        internalActivitiesList.addMouseListener(listener);
    }

    public void init() {
        if (list.size() > 0) {
            if (list.size() < selectedRowIndex + 1) {
                selectedRowIndex = selectedRowIndex - 1;
            }
            internalActivitiesList.setSelectedIndex(selectedRowIndex);
        }
        this.informationArea.setBorder(new TitledBorder(new EtchedBorder(), Labels.getString("Common.Details")));
    }

    public boolean isMaxNbTotalEstimatedPomReached(Activity activity) {
        int nbTotalEstimatedPom = list.getNbTotalEstimatedPom() + activity.getEstimatedPoms() + activity.getOverestimatedPoms();
        return nbTotalEstimatedPom > ControlPanel.preferences.getMaxNbPomPerDay();
    }

    private void setSelectedRowIndex() {
        int row = internalActivitiesList.getSelectedIndex();
        if (row > -1) {
            selectedRowIndex = row;
        }
    }

    public class cellRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (isSelected) {
                renderer.setBackground(ColorUtil.BLUE_ROW);
            } else {
                renderer.setBackground(index % 2 == 0 ? Color.white : ColorUtil.YELLOW_ROW); // rows with even/odd number
            }

            Activity toDo = (Activity) value;
            renderer.setText((toDo.isUnplanned() ? "(" + "U" + ") " : "") + toDo.getName() + " (" + toDo.getActualPoms() + "/" + toDo.getEstimatedPoms() + (toDo.getOverestimatedPoms() > 0 ? " + " + toDo.getOverestimatedPoms() : "") + ")");

            if (isSelected) {
                renderer.setFont(new Font(renderer.getFont().getName(), Font.BOLD, renderer.getFont().getSize()));
            }
            if (toDo.isFinished()) {
                renderer.setForeground(ColorUtil.GREEN);
            } else {
                renderer.setForeground(ColorUtil.BLACK);
            }

            return renderer;
        }
    }
    
    public void setPanelBorder() {}
}
