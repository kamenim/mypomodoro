package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.buttons.myButton;

import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.model.Activity;

/**
 * Panel that displays description on the current Pomodoro and
 * allows editing it as a comment
 * 
 * @author Phil Karoo
 */
public class CommentPanel extends JPanel implements ActivityInformation {

    private final JTextArea commentArea = new JTextArea();
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final GridBagConstraints gbc = new GridBagConstraints();

    public CommentPanel(ToDoListPanel panel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addToDoIconPanel();
        addCommentArea();
        addSaveButton(panel);
    }

    private void addSaveButton(final ToDoListPanel panel) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        //gbc.fill = GridBagConstraints.NONE;
        JButton changeButton = new myButton(ControlPanel.labels.getString("Common.Save"));
        changeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveComment(commentArea.getText());
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

    private void addCommentArea() {
        // add the comment area
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        commentArea.setEditable(true);
        add(new JScrollPane(commentArea), gbc);
    }

    @Override
    public void showInfo(Activity activity) {
        ToDoIconLabel.showIconLabel(iconLabel, activity);
        String text = activity.getNotes();
        commentArea.setText(text);
    }

    @Override
    public void clearInfo() {
        ToDoIconLabel.clearIconLabel(iconLabel);
        commentArea.setText("");
    }
    
    public JLabel getIconLabel() {
        return iconLabel;
    }
}