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

import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.TimeMinusButton;
import org.mypomodoro.buttons.TimePlusButton;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

public class TimerPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;
    private static final Dimension PREFERED_SIZE = new Dimension(250, 175);
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JButton startButton = new JButton(Labels.getString("ToDoListPanel.Start"));
    private JLabel pomodoroTime;
    private ToDoJList toDoJList;

    TimerPanel(Pomodoro pomodoro, JLabel pomodoroTime, ToDoJList toDoJList) {
        this.pomodoroTime = pomodoroTime;
        this.toDoJList = toDoJList;
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

        
        addTimeMinusButton(pomodoro);
        addPomodoroTimerLabel();
        addTimePlusButton(pomodoro);
        addStartButton(pomodoro);
    }

    private void addTimeMinusButton(final Pomodoro pomodoro) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.1;
        gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        TimeMinusButton timeMinus = new TimeMinusButton(pomodoro);
        timeMinus.setBackground(ColorUtil.WHITE);
        timeMinus.setOpaque(true);
        timeMinus.setBorder(new LineBorder(ColorUtil.BLACK, 1));
        add(timeMinus, gbc);
    }

    private void addPomodoroTimerLabel() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.SOUTH;
        pomodoroTime.setFont(pomodoroTime.getFont().deriveFont(40f));
        add(pomodoroTime, gbc);
    }

    private void addTimePlusButton(final Pomodoro pomodoro) {
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.1;
        gbc.weighty = 0.3;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        TimePlusButton timePlus = new TimePlusButton(pomodoro);
        timePlus.setBackground(ColorUtil.WHITE);
        timePlus.setOpaque(true);
        timePlus.setBorder(new LineBorder(ColorUtil.BLACK, 1));
        add(timePlus, gbc);
    }

    private void addStartButton(final Pomodoro pomodoro) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.165;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.SOUTH;
        startButton.setBackground(ColorUtil.WHITE);
        startButton.setForeground(ColorUtil.BLACK);
        Border line = new LineBorder(ColorUtil.BLACK, 2);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        startButton.setBorder(compound);
        startButton.setFocusPainted(false); // removes borders around text
        startButton.setFont(startButton.getFont().deriveFont(20f));
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (pomodoro.getCurrentToDo() != null) {
                    if (Labels.getString("ToDoListPanel.Start").equals(startButton.getText())) {
                        if (pomodoro.getCurrentToDo().isFinished()) {
                            JFrame window = new JFrame();
                            String message = Labels.getString("ToDoListPanel.All pomodoros of this ToDo are already done.");
                            message += "\n(" + Labels.getString("ToDoListPanel.please complete this ToDo to make a report or make an overestimation to extend it") + ")";
                            JOptionPane.showMessageDialog(window, message);
                        } else {
                            pomodoro.start();
                            startButton.setText(Labels.getString("ToDoListPanel.Stop"));
                            startButton.setForeground(ColorUtil.RED);
                            Border line = new LineBorder(ColorUtil.RED, 2);
                            Border margin = new EmptyBorder(5, 15, 5, 15);
                            Border compound = new CompoundBorder(line, margin);
                            startButton.setBorder(compound);
                            pomodoroTime.setForeground(ColorUtil.RED);
                            toDoJList.init(); // init list so the custom cell renderer can turn the title into red
                        }
                    } else if (pomodoro.stopWithWarning()) {
                        startButton.setText(Labels.getString("ToDoListPanel.Start"));
                        startButton.setForeground(ColorUtil.BLACK);
                        Border line = new LineBorder(ColorUtil.BLACK, 2);
                        Border margin = new EmptyBorder(5, 15, 5, 15);
                        Border compound = new CompoundBorder(line, margin);
                        startButton.setBorder(compound);
                        pomodoroTime.setForeground(ColorUtil.BLACK);
                        toDoJList.init(); // init list so the custom cell renderer can turn the title into black
                    }
                }
            }
        });
        add(startButton, gbc);
    }

    public void setStart() {
        startButton.setText(Labels.getString("ToDoListPanel.Start"));
        setStartColor(ColorUtil.BLACK);
    }

    public void setStartColor(Color color) {
        startButton.setForeground(color);
        Border line = new LineBorder(color, 2);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        startButton.setBorder(compound);
        pomodoroTime.setForeground(color);
    }
}