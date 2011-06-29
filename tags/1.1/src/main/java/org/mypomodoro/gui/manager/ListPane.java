package org.mypomodoro.gui.manager;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.ActivityInformationListListener;
import org.mypomodoro.model.AbstractActivities;
import org.mypomodoro.model.Activity;

import org.mypomodoro.gui.ControlPanel;

/**
 * This class just abstracts a JPanel for jlist and a information panel for the
 * items in the list.
 * 
 * @author nikolavp
 * 
 */
public class ListPane extends JPanel implements ActivityInformation {

    private final GridBagConstraints c = new GridBagConstraints();
    private final JList internalActivitiesList;
    private final JTextArea informationArea;
    private final AbstractActivities list;
    private final String titleList;
    /**
     * width of list cells
     */
    public static final int CELL_WIDTH = 250;
    private static final Dimension PREFERED_SIZE = new Dimension(250, 100);

    public ListPane(AbstractActivities list, String title) {
        this.list = list;
        titleList = title;
        setLayout(new GridBagLayout());
        internalActivitiesList = new JList();
        setPreferredSize(PREFERED_SIZE);
        internalActivitiesList.setFont(new Font(this.getFont().getName(), Font.PLAIN, this.getFont().getSize()));

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
    public void showInfo(Activity аctivity) {
        String pattern = "dd MMM yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String activityDate = format.format(аctivity.getDate());
        String text = "Date: ";
        if (аctivity.isUnplanned()) {
            text += "U [";
        }
        text += activityDate;
        if (аctivity.isUnplanned()) {
            text += "]";
        }
        text += "\nTitle: " + аctivity.getName()
                + "\nEstimated Pomodoros: " + аctivity.getEstimatedPoms();
        if (аctivity.getOverestimatedPoms() > 0) {
            text += " + " + аctivity.getOverestimatedPoms();
        }
        informationArea.setText(text);
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

    public void update() {
        internalActivitiesList.setListData(list.toArray());
        if (list.size() > 0) {
            internalActivitiesList.setSelectedIndex(0);
        }
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
        internalActivitiesList.setBorder(new TitledBorder(new EtchedBorder(), titleList + " (" + list.size() + ")"));
        this.informationArea.setBorder(new TitledBorder(new EtchedBorder(), "Details"));
    }

    public boolean isMaxNbTotalEstimatedPomReached(Activity activity) {
        int nbTotalEstimatedPom = list.getNbTotalEstimatedPom() + activity.getEstimatedPoms() + activity.getOverestimatedPoms();
        return nbTotalEstimatedPom > ControlPanel.preferences.getMaxNbPomPerDay();
    }
}