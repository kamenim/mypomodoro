package org.mypomodoro.gui.manager;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.ActivityInformationListListener;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * This class just abstracts a JPanel for jlist and a information panel for the
 * items in the list.
 *
 * @author nikolavp
 * @author Phil Karoo
 *
 */
public class ListPane extends JPanel implements ActivityInformation {

    private static final long serialVersionUID = 20110814L;
    private final GridBagConstraints c = new GridBagConstraints();
    private final JList internalActivitiesList;
    private final JTextArea informationArea;
    private final AbstractActivities list;
    private final String titleList;
    private int selectedRowIndex = 0;
    final private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    // width of list cells
    public static final int CELL_WIDTH = 250;
    private static final Dimension PREFERED_SIZE = new Dimension(250, 100);

    public ListPane(AbstractActivities list, String title) {
        this.list = list;
        titleList = title;
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

        this.informationArea = new JTextArea();
        addActivitiesList();
        addInformationArea();
    }

    private void addInformationArea() {
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.2;
        informationArea.setEditable(false);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        add(new JScrollPane(informationArea), c);
    }

    private void addActivitiesList() {
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.8;
        c.fill = GridBagConstraints.BOTH;
        internalActivitiesList.setFixedCellWidth(CELL_WIDTH);
        update();
        add(new JScrollPane(internalActivitiesList), c);
        internalActivitiesList.addListSelectionListener(new ActivityInformationListListener(
                this));
    }

    @Override
    public void showInfo(Activity activity) {
        String text = Labels.getString("Common.Date") + ": ";
        if (activity.isUnplanned()) {
            text += "U [";
        }
        text += DateUtil.getFormatedDate(activity.getDate());
        if (activity.isUnplanned()) {
            text += "]";
        }
        text += "\n" + Labels.getString("Common.Title") + ": " + activity.getName()
                + "\n" + Labels.getString("Common.Estimated pomodoros") + ": " + activity.getEstimatedPoms();
        if (activity.getOverestimatedPoms() > 0) {
            text += " + " + activity.getOverestimatedPoms();
        }
        text += "\n" + Labels.getString("Common.Type") + ": " + (activity.getType().isEmpty() ? "-" : activity.getType())
                + "\n" + Labels.getString("Common.Author") + ": " + (activity.getAuthor().isEmpty() ? "-" : activity.getAuthor())
                + "\n" + Labels.getString("Common.Place") + ": " + (activity.getPlace().isEmpty() ? "-" : activity.getPlace());
        informationArea.setText(text);
        // disable auto scrolling
        informationArea.setCaretPosition(0);
    }

    @Override
    public void clearInfo() {
        informationArea.setText("");
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
        internalActivitiesList.setBorder(new TitledBorder(new EtchedBorder(), titleList + " (" + list.size() + ")"));
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

            Activity toDo = (Activity) value;
            renderer.setText((toDo.isUnplanned() ? "(" + "U" + ") " : "") + toDo.getName() + " (" + toDo.getActualPoms() + "/" + (toDo.getEstimatedPoms() + toDo.getOverestimatedPoms()) + ")");

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
}
