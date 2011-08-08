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
import org.mypomodoro.util.DateUtil;
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
        
        addInformationPanel();                
        addCompleteButton(panel);
        addCompleteAllButton(panel);
    }
    
    private void addInformationPanel() {
        JPanel infoPanel = new JPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = 2;
        
        GridBagConstraints igbc = new GridBagConstraints();
        infoPanel.setLayout(new GridBagLayout());
        addToDoIconPanel(infoPanel,igbc);
        addInformationArea(infoPanel,igbc);
        
        add(infoPanel, gbc);
    }

    private void addToDoIconPanel(JPanel infoPanel, GridBagConstraints igbc) {
        igbc.gridx = 0;
        igbc.gridy = 0;
        igbc.fill = GridBagConstraints.BOTH;
        igbc.weightx = 1.0;
        igbc.weighty = 0.1;
        igbc.gridheight = 1;
        infoPanel.add(iconLabel, igbc);
    }

    private void addInformationArea(JPanel infoPanel, GridBagConstraints igbc) {
        // add the information area
        igbc.gridx = 0;
        igbc.gridy = 1;
        igbc.fill = GridBagConstraints.BOTH;
        igbc.weightx = 1.0;
        igbc.weighty = 1.0;
        igbc.gridheight = GridBagConstraints.REMAINDER;
        informationArea.setEditable(false);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        // disable auto scrolling
        DefaultCaret caret = (DefaultCaret) informationArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
        infoPanel.add(new JScrollPane(informationArea), igbc);
    }

    private void addCompleteButton(final ToDoListPanel panel) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 1;
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

    private void addCompleteAllButton(final ToDoListPanel panel) {
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.gridheight = 1;
        //gbc.fill = GridBagConstraints.NONE;
        JButton changeButton = new MyButton(Labels.getString("ToDoListPanel.Complete all"));
        changeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!panel.getToDoList().isEmpty()) {
                    panel.completeAllTasksWithWarning();
                }
            }
        });
        add(changeButton, gbc);
    }

    @Override
    public void showInfo(Activity activity) {
        ToDoIconLabel.showIconLabel(iconLabel, activity);
        String text = Labels.getString("Common.Date") + ": ";
        if (activity.isUnplanned()) {
            text += "U [";
        }
        text += DateUtil.getFormatedDate(activity.getDate());
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