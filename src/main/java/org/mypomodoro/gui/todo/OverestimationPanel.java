package org.mypomodoro.gui.todo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.Main;

import org.mypomodoro.buttons.AbstractPomodoroButton;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.Labels;

/**
 * Panel that allows overestimating the number of pomodoros of the current ToDo
 *
 */
public class OverestimationPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    protected final OverestimationInputForm overestimationInputFormPanel = new OverestimationInputForm();
    private final JLabel iconLabel = new JLabel("", JLabel.LEFT);
    private final GridBagConstraints gbc = new GridBagConstraints();

    public OverestimationPanel(ToDoPanel todoPanel) {
        setLayout(new GridBagLayout());
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));

        addToDoIconPanel();
        addOverestimationInputFormPanel();
        addSaveButton(todoPanel);
    }

    private void addSaveButton(final ToDoPanel todoPanel) {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.gridheight = 2;
        // gbc.fill = GridBagConstraints.NONE;
        JButton changeButton = new AbstractPomodoroButton(
                Labels.getString("Common.Save"));
        changeButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveOverestimation(todoPanel, overestimationInputFormPanel.getOverestimationPomodoros().getSelectedIndex() + 1);
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
        gbc.insets = new Insets(0, 3, 0, 0); // margin left
        add(iconLabel, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
    }

    private void addOverestimationInputFormPanel() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.9;
        gbc.gridheight = 1;
        //gbc.gridheight = GridBagConstraints.REMAINDER;
        add(overestimationInputFormPanel, gbc);
    }

    public void saveOverestimation(ToDoPanel panel, int overestimatedPomodoros) {
        int row = panel.getTable().getSelectedRow();
        Integer id = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
        Activity selectedToDo = panel.getActivityById(id);
        // Overestimation
        selectedToDo.setOverestimatedPoms(selectedToDo.getOverestimatedPoms() + overestimatedPomodoros);
        ToDoList.getList().update(selectedToDo);
        selectedToDo.databaseUpdate();
        panel.getTable().getModel().setValueAt(selectedToDo.getEstimatedPoms(), panel.getTable().convertRowIndexToModel(row), panel.getIdKey() - 3); // update estimated colunm column index = 3 (the renderer will do the rest)        
        panel.refreshRemaining();
        overestimationInputFormPanel.reset();
        String title = Labels.getString("ToDoListPanel.Overestimate ToDo");
        String message = Labels.getString(
                "ToDoListPanel.Nb of estimated pomodoros increased by {0}",
                overestimatedPomodoros);
        JOptionPane.showConfirmDialog(Main.gui, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    public JLabel getIconLabel() {
        return iconLabel;
    }
}
