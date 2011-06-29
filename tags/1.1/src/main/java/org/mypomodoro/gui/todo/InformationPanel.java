package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;

/**
 * Panel that displays information on the current Pomodoro...this should be
 * updated when the ToDo list is updated.
 */
public class InformationPanel extends JPanel implements ActivityInformation {

    private final JTextArea informationArea = new JTextArea();
    private final GridBagConstraints gbc = new GridBagConstraints();

    public InformationPanel(ToDoListPanel panel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addInformationArea();
        addCompleteButton(panel);
    }

    private void addCompleteButton(final ToDoListPanel panel) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        JButton changeButton = new JButton("Complete");
        changeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.completeTaskWithWarning();
            }
        });
        add(changeButton, gbc);
    }

    private void addInformationArea() {
        // add the information area
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        informationArea.setEditable(false);
        add(new JScrollPane(informationArea), gbc);
    }

    @Override
    public void showInfo(Activity activity) {
        String pattern = "dd MMM yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String activityDate = format.format(activity.getDate());
        String text = "Date: ";
        if (activity.isUnplanned()) {
            text += "U [";
        }
        text += activityDate;
        if (activity.isUnplanned()) {
            text += "]";
        }
        text += "\nTitle: " + activity.getName()
                + "\nEstimated Pomodoros: " + activity.getEstimatedPoms();
        if (activity.getOverestimatedPoms() > 0) {
            text += " + " + activity.getOverestimatedPoms();
        }
        text += "\nReal Pomodoros: " + activity.getActualPoms()
                + "\nExternal Interruptions: " + activity.getNumInterruptions()
                + "\nDescription: " + activity.getDescription();
        informationArea.setText(text);
    }

    @Override
    public void clearInfo() {
        informationArea.setText("");
    }
}