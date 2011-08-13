package org.mypomodoro.gui.todo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.mypomodoro.Main;
import org.mypomodoro.util.Labels;

public class TimerPanel extends JPanel {
	private static final long serialVersionUID = 20110814L;
	
    private static final Dimension PREFERED_SIZE = new Dimension(250, 175);
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JButton startButton = new JButton(Labels.getString("ToDoListPanel.Start"));

    TimerPanel(Pomodoro pomodoro, JLabel pomodoroTime) {
        try {
            pomodoroTime.setFont(Font.createFont(Font.TRUETYPE_FONT,
                    Main.class.getResourceAsStream("/fonts/timer.ttf")));
        }
        catch (FontFormatException e) {
            System.err.println("TrueType not supported " + e);
        }
        catch (IOException e) {
            System.err.println("TTF file not found " + e);
        }
        pomodoroTime.setForeground(Color.DARK_GRAY);
        setPreferredSize(PREFERED_SIZE);
        setLayout(new GridBagLayout());

        addPomodoroTimerLabel(pomodoroTime, gbc);
        addStartButton(pomodoro);
    }

    private void addPomodoroTimerLabel(JLabel pomodoroTime,
            GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.SOUTH;
        pomodoroTime.setFont(pomodoroTime.getFont().deriveFont(40f));
        add(pomodoroTime, gbc);
    }

    private void addStartButton(final Pomodoro pomodoro) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.165;
        gbc.anchor = GridBagConstraints.SOUTH;
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(Color.BLACK);
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (pomodoro.getCurrentToDo() != null) {
                    if (Labels.getString("ToDoListPanel.Start").equals(startButton.getText())) {
                        if (pomodoro.isCurrentToDoComplete()) {
                            JFrame window = new JFrame();
                            String message = Labels.getString("ToDoListPanel.All pomodoros of this ToDo are already done.");
                            message += "\n(" + Labels.getString("ToDoListPanel.please complete this ToDo to make a report or make an overestimation to extend it") + ")";
                            JOptionPane.showMessageDialog(window, message);
                        } else {
                            pomodoro.start();
                            startButton.setText(Labels.getString("ToDoListPanel.Stop"));
                        }
                    } else if (pomodoro.stopWithWarning()) {
                        startButton.setText(Labels.getString("ToDoListPanel.Start"));
                    }

                }
            }
        });
        startButton.setFont(startButton.getFont().deriveFont(20f));
        add(startButton, gbc);
    }

    public void setStart() {
        startButton.setText(Labels.getString("ToDoListPanel.Start"));
    }
}