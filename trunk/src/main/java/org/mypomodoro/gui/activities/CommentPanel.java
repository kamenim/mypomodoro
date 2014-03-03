package org.mypomodoro.gui.activities;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.buttons.AbstractPomodoroButton;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.Labels;

/**
 * Panel that displays comment on the current Activity and allows editing it
 *
 */
public class CommentPanel extends ActivityInformationPanel {

    private static final long serialVersionUID = 20110814L;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public CommentPanel(ActivitiesPanel activitiesPanel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addCommentArea();
        addSaveButton(activitiesPanel);
    }

    private void addSaveButton(final ActivitiesPanel panel) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        // gbc.fill = GridBagConstraints.NONE;
        JButton changeButton = new AbstractPomodoroButton(
                Labels.getString("Common.Save"));
        changeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveComment(informationArea.getText());
            }
        });
        add(changeButton, gbc);
    }

    private void addCommentArea() {
        // add the comment area
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        informationArea.setEditable(true);
        informationArea.setLineWrap(true);
        informationArea.setWrapStyleWord(true);
        add(new JScrollPane(informationArea), gbc);
    }

    @Override
    public void selectInfo(Activity activity) {
        // template for user stories and epics
        if (ControlPanel.preferences.getAgileMode()
                && activity.getNotes().trim().length() == 0
                && activity.isStory()) {
            StringBuilder text = new StringBuilder();
            text.append("Story line" + "\n");
            text.append("-------------" + "\n");
            text.append("As a <user role>, I want to <action> in order to <purpose>" + "\n\n");
            text.append("User acceptance criteria" + "\n");
            text.append("----------------------------------" + "\n");
            text.append("* " + "\n");
            text.append("* " + "\n\n");
            text.append("Test cases" + "\n");
            text.append("----------------" + "\n");
            text.append("* " + "\n");
            text.append("* ");
            textMap.put("comment", text.toString());
        } else {
            textMap.put("comment", activity.getNotes());
        }
    }

    @Override
    public boolean isMultipleSelectionAllowed() {
        return false;
    }
}
