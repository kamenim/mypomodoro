/* 
 * Copyright (C) 2014
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.gui.todo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.ImageIcon;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.mypomodoro.Main;
import org.mypomodoro.buttons.TimeMinusButton;
import org.mypomodoro.buttons.TimePlusButton;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

public class TimerPanel extends JPanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final Dimension PREFERED_SIZE = new Dimension(250, 175);
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final JButton startButton = new JButton(Labels.getString("ToDoListPanel.Start"));
    private final JLabel pomodoroTime;
    private final ToDoPanel panel;
    private final ImageIcon refreshIcon = new ImageIcon(Main.class.getResource("/images/refresh.png"));

    TimerPanel(Pomodoro pomodoro, JLabel pomodoroTime, ToDoPanel panel) {
        this.pomodoroTime = pomodoroTime;
        this.panel = panel;
        // Timer font
        try {
            pomodoroTime.setFont(Font.createFont(Font.TRUETYPE_FONT,
                    Main.class.getResourceAsStream("/fonts/timer.ttf")));
        } catch (FontFormatException ex) {
            pomodoroTime.setFont(new JLabel().getFont().deriveFont(Font.PLAIN));
            logger.error("TrueType not supported for font Timer. Replacoed with default System font.", ex);
        } catch (IOException ex) {
            pomodoroTime.setFont(new JLabel().getFont().deriveFont(Font.PLAIN));
            logger.error("Timer TTF file not found. Replaced with default System font.", ex);
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
        // must be set to 'true' to make the button opaque in all type of graphical/theme System environment (eg Win7 aero vs Win XP classic)
        // setOpaque(false) makes nice round button on Win7 aero        
        timeMinus.setOpaque(true);
        timeMinus.setMargin(new Insets(1, 1, 1, 1)); // inner margin
        timeMinus.setFocusPainted(false); // removes borders around text
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
        // must be set to 'true' to make the button opaque in all type of graphical/theme System environment (eg Win7 aero vs Win XP classic)
        // setOpaque(false) makes nice round button on Win7 aero 
        timePlus.setOpaque(true);        
        timePlus.setMargin(new Insets(1, 1, 1, 1)); // inner margin
        timePlus.setFocusPainted(false); // removes borders around text
        add(timePlus, gbc);
    }

    private void addStartButton(final Pomodoro pomodoro) {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.165;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.SOUTH;
        // must be set to 'true' to make the button opaque in all type of graphical/theme System environment (eg Win7 aero vs Win XP classic)
        // setOpaque(false) makes nice round button on Win7 aero
        startButton.setOpaque(true);
        startButton.setForeground(ColorUtil.BLACK);
        startButton.setMargin(new Insets(5, 15, 5, 15)); // inner margin
        startButton.setFocusPainted(false); // removes borders around text        
        startButton.setFont(startButton.getFont().deriveFont(20f));
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Activity currentToDo = pomodoro.getCurrentToDo();
                if (currentToDo != null) {
                    if (Labels.getString("ToDoListPanel.Start").equals(startButton.getText())) {
                        if (panel.getTable().getSelectedRowCount() == 1) { // this addresses the case when a task is selected during the pomodoro of another task
                            int row = panel.getTable().getSelectedRow();
                            pomodoro.setCurrentToDoId((Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey()));
                        }
                        panel.showCurrentSelectedRow(); // in any case
                        // Retrieve activity from the database in case it's changed (concurrent work : another use may have worked on it)                                       
                        if (currentToDo.hasChanged()) {
                            String title = Labels.getString("ToDoListPanel.ToDo changed");
                            String message = Labels.getString("ToDoListPanel.The ToDo has changed");
                            JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, refreshIcon);
                        } else {
                            if (currentToDo.isFinished()) {
                                String message = Labels.getString("ToDoListPanel.All pomodoros of this ToDo are already done");
                                message += System.getProperty("line.separator") + "(" + Labels.getString("ToDoListPanel.please complete this ToDo to make a report or make an overestimation to extend it") + ")";
                                JOptionPane.showMessageDialog(Main.gui, message);
                            } else {
                                pomodoro.start();
                                startButton.setText(Labels.getString("ToDoListPanel.Stop"));
                                startButton.setForeground(ColorUtil.RED);
                                pomodoroTime.setForeground(ColorUtil.RED);
                            }
                        }
                    } else if (pomodoro.stopWithWarning()) {
                        panel.showCurrentSelectedRow(); // in any case
                        startButton.setText(Labels.getString("ToDoListPanel.Start"));
                        startButton.setForeground(ColorUtil.BLACK);
                        pomodoroTime.setForeground(ColorUtil.BLACK);
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
        pomodoroTime.setForeground(color);
    }
}
