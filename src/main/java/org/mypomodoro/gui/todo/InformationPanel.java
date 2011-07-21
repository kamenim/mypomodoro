package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.text.DefaultCaret;
import org.mypomodoro.buttons.MyButton;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays information on the current Pomodoro...this should be
 * updated when the ToDo list is updated.
 */
public class InformationPanel extends JPanel implements ActivityInformation {

    private final JTextArea informationArea = new JTextArea();
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final GridBagConstraints gbc = new GridBagConstraints();

    public InformationPanel(ToDoListPanel panel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addToDoIconPanel();
        addInformationArea();
        addCompleteButton(panel);
    }

    private void addCompleteButton(final ToDoListPanel panel) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        //gbc.fill = GridBagConstraints.NONE;
        JButton changeButton = new MyButton(Labels.getString("ToDoListPanel.Complete"));
        changeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.completeTaskWithWarning();
            }
        });
        add(changeButton, gbc);
    }

    private void addToDoIconPanel() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.gridheight = 1;
        add(iconLabel, gbc);
    }

    private void addInformationArea() {
        // add the information area
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        informationArea.setEditable(false);
        // disable auto scrolling
        DefaultCaret caret = (DefaultCaret) informationArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        add(new JScrollPane(informationArea), gbc);
    }

    @Override
    public void showInfo(Activity activity) {
        ToDoIconLabel.showIconLabel(iconLabel, activity);
        String pattern = "dd MMM yyyy";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        String activityDate = format.format(activity.getDate());
        String text = Labels.getString("Common.Date") + ": ";
        if (activity.isUnplanned()) {
            text += "U [";
        }
        text += activityDate;
        if (activity.isUnplanned()) {
            text += "]";
        }
        text += "\n" + Labels.getString("Common.Type") + ": " + activity.getType()
                + "\n" + Labels.getString("Common.Author") + ": " + activity.getAuthor()
                + "\n" + Labels.getString("Common.Place") + ": " + activity.getPlace()
                + "\n" + Labels.getString("Common.Description") + ": " + activity.getDescription();
        informationArea.setText(text);
    }

    @Override
    public void clearInfo() {
        ToDoIconLabel.clearIconLabel(iconLabel);
        informationArea.setText("");
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }
}