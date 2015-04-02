/* 
 * Copyright (C)
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.TimeMinusButton;
import org.mypomodoro.buttons.TimePlusButton;
import org.mypomodoro.buttons.TransparentButton;
import org.mypomodoro.model.Activity;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

public class TimerPanel extends JPanel {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private static final Dimension PREFERED_SIZE = new Dimension(230, 220);
    private final GridBagConstraints gbc = new GridBagConstraints();
    private final ImageIcon startIcon = new ImageIcon(Main.class.getResource("/images/start.png"));
    private final ImageIcon stopIcon = new ImageIcon(Main.class.getResource("/images/stop.png"));
    private final ImageIcon stopRedIcon = new ImageIcon(Main.class.getResource("/images/stopred.png"));
    private final TransparentButton startButton = new TransparentButton(startIcon);
    private final ImageIcon pauseIcon = new ImageIcon(Main.class.getResource("/images/pause.png"));
    private final ImageIcon pauseRedIcon = new ImageIcon(Main.class.getResource("/images/pausered.png"));
    private final ImageIcon resumeIcon = new ImageIcon(Main.class.getResource("/images/resume.png"));
    private final ImageIcon resumeRedIcon = new ImageIcon(Main.class.getResource("/images/resumered.png"));
    private final TransparentButton pauseButton = new TransparentButton(pauseRedIcon);
    private final JLabel pomodoroTime;
    private final ToDoPanel panel;
    private final TimePlusButton timePlus;
    private final TimeMinusButton timeMinus;
    public static boolean strictPomodoro = false;
    private final ImageIcon refreshIcon = new ImageIcon(Main.class.getResource("/images/refresh.png"));

    TimerPanel(Pomodoro pomodoro, JLabel pomodoroTime, ToDoPanel panel) {
        this.pomodoroTime = pomodoroTime;
        this.panel = panel;
        setLayout(new GridBagLayout());
        setPreferredSize(PREFERED_SIZE);
        // Transparent !
        setOpaque(false);
        // Timer font
        try {
            pomodoroTime.setFont(Font.createFont(Font.TRUETYPE_FONT,
                    Main.class.getResourceAsStream("/fonts/timer.ttf")));
        } catch (FontFormatException ex) {
            pomodoroTime.setFont(new JLabel().getFont().deriveFont(Font.PLAIN));
            logger.error("TrueType not supported. Replaced with default System font.", ex);
        } catch (IOException ex) {
            pomodoroTime.setFont(new JLabel().getFont().deriveFont(Font.PLAIN));
            logger.error("Timer TTF file not found. Replaced with default System font.", ex);
        }
        pomodoroTime.setForeground(Color.DARK_GRAY);
        timeMinus = new TimeMinusButton(pomodoro);
        addTimeMinusButton();
        addPomodoroTimerLabel();
        timePlus = new TimePlusButton(pomodoro);
        addTimePlusButton();
        addStartButton(pomodoro);
        addPauseButton(pomodoro);
    }

    private void addPauseButton(final Pomodoro pomodoro) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTH;
        pauseButton.setToolTipText(Labels.getString("ToDoListPanel.Pause"));
        pauseButton.setMargin(new Insets(0, 20, 0, 20)); // inner margin
        pauseButton.setFocusPainted(false); // removes borders around icon
        pauseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Activity currentToDo = pomodoro.getCurrentToDo();
                if (currentToDo != null) {
                    if (!pomodoro.getTimer().isRunning()) { // resume 
                        pomodoro.resume();
                        if (pomodoro.inPomodoro()) {
                            pauseButton.setIcon(pauseRedIcon);
                        } else {
                            pauseButton.setIcon(pauseIcon);
                        }
                        pauseButton.setMargin(new Insets(0, 20, 0, 20)); // icon 21 px wide                       
                        pauseButton.setToolTipText(Labels.getString("ToDoListPanel.Pause"));
                    } else { // pause
                        pomodoro.pause();
                        if (pomodoro.inPomodoro()) {
                            pauseButton.setIcon(resumeRedIcon);
                        } else {
                            pauseButton.setIcon(resumeIcon);
                        }
                        pauseButton.setMargin(new Insets(0, 17, 0, 16)); // icon 28 px wide
                        pauseButton.setToolTipText(Labels.getString("ToDoListPanel.Resume"));
                    }
                }
            }
        });
        add(pauseButton, gbc);
    }

    private void addTimeMinusButton() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.EAST;
        timeMinus.setVisible(true); // this is a TransparentButton
        timeMinus.setMargin(new Insets(1, 1, 1, 1)); // inner margin
        timeMinus.setFocusPainted(false); // removes borders around icon
        add(timeMinus, gbc);
    }

    private void addPomodoroTimerLabel() {
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        pomodoroTime.setFont(pomodoroTime.getFont().deriveFont(40f));
        add(pomodoroTime, gbc);
    }

    private void addTimePlusButton() {
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        gbc.anchor = GridBagConstraints.WEST;
        timePlus.setVisible(true); // this is a TransparentButton
        timePlus.setMargin(new Insets(1, 1, 1, 1)); // inner margin
        timePlus.setFocusPainted(false); // removes borders around icon
        add(timePlus, gbc);
    }

    private void addStartButton(final Pomodoro pomodoro) {
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.NORTH;
        startButton.setToolTipText(Labels.getString("ToDoListPanel.Start"));
        startButton.setVisible(true);
        startButton.setMargin(new Insets(0, 20, 0, 20)); // inner margin
        startButton.setFocusPainted(false); // removes borders around icon
        startButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Activity currentToDo = pomodoro.getCurrentToDo();
                if (currentToDo != null) {
                    if (startButton.getIcon().equals(startIcon)) {
                        if (panel.getTable().getSelectedRowCount() == 1) { // this addresses the case when a task is selected during the pomodoro of another task
                            int row = panel.getTable().getSelectedRow();
                            pomodoro.setCurrentToDoId((Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey()));
                            currentToDo = pomodoro.getCurrentToDo();
                        }
                        panel.showCurrentSelectedRow(); // in any case
                        // Retrieve activity from the database in case it's changed (concurrent work : another user may have worked on it)                                       
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
                                if (!strictPomodoro || (strictPomodoro && currentToDo.getEstimatedPoms() > 0)) { // strict pomodoro mode doesn't allow starting task with no estimate
                                    pomodoro.start();
                                    startButton.setIcon(stopRedIcon);
                                    startButton.setToolTipText(Labels.getString("ToDoListPanel.Stop"));
                                    if (strictPomodoro) {
                                        startButton.setVisible(false);
                                    }
                                    pomodoroTime.setForeground(ColorUtil.RED);
                                    timePlus.setTimePlusRedIcon(true); // turn time plus button red
                                    timeMinus.setTimeMinusRedIcon(true); // turn time minus button red
                                    if (!strictPomodoro) {
                                        pauseButton.setVisible(true);
                                    }
                                }
                            }
                        }
                    } else if (pomodoro.stopWithWarning()) {
                        panel.showCurrentSelectedRow(); // in any case
                        startButton.setIcon(startIcon);
                        startButton.setToolTipText(Labels.getString("ToDoListPanel.Start"));
                        pomodoroTime.setForeground(ColorUtil.BLACK);
                        timePlus.setTimePlusRedIcon(false);
                        timeMinus.setTimeMinusRedIcon(false);
                        pauseButton.setIcon(pauseRedIcon); // set the icon before setting the visibility to false so it doesn't flicker on Win7 aero
                        pauseButton.setVisible(false);
                        pauseButton.setMargin(new Insets(0, 20, 0, 20));
                        pauseButton.setToolTipText(Labels.getString("ToDoListPanel.Pause"));
                        setToolTipText(null);
                    }
                }
            }
        });
        add(startButton, gbc);
    }

    // prepare env to start
    public void setStartEnv() {
        startButton.setIcon(startIcon);
        startButton.setToolTipText(Labels.getString("ToDoListPanel.Start"));
        pauseButton.setVisible(false);
        pauseButton.setIcon(pauseRedIcon);
        pauseButton.setToolTipText(Labels.getString("ToDoListPanel.Pause"));
        timePlus.setTimePlusRedIcon(false);
        timeMinus.setTimeMinusRedIcon(false);
        pomodoroTime.setForeground(ColorUtil.BLACK);
    }

    // turn icons black
    public void setBreakEnv() {
        startButton.setIcon(stopIcon);
        startButton.setToolTipText(Labels.getString("ToDoListPanel.Stop"));
        pauseButton.setIcon(pauseIcon);
        pauseButton.setToolTipText(Labels.getString("ToDoListPanel.Pause"));
        timePlus.setTimePlusRedIcon(false);
        timeMinus.setTimeMinusRedIcon(false);
        pomodoroTime.setForeground(ColorUtil.BLACK);
    }

    // turn icons red
    public void setPomodoroEnv() {
        startButton.setIcon(stopRedIcon);
        startButton.setToolTipText(Labels.getString("ToDoListPanel.Stop"));
        pauseButton.setIcon(pauseRedIcon);
        pauseButton.setToolTipText(Labels.getString("ToDoListPanel.Pause"));
        timePlus.setTimePlusRedIcon(true);
        timeMinus.setTimeMinusRedIcon(true);
        pomodoroTime.setForeground(ColorUtil.RED);
    }

    public void switchPomodoroCompliance() {
        if (!strictPomodoro) { // make it strict pomodoro
            if (!startButton.getIcon().equals(startIcon)) {
                startButton.setVisible(false);
            }
            pauseButton.setVisible(false);
            panel.hideDiscontinuousButton();
            timePlus.setVisible(false);
            timeMinus.setVisible(false);
            strictPomodoro = true;
        } else { // default
            startButton.setVisible(true);
            if (!startButton.getIcon().equals(startIcon)) {
                pauseButton.setVisible(true);
            }
            panel.showDiscontinuousButton();
            timePlus.setVisible(true);
            timeMinus.setVisible(true);
            strictPomodoro = false;
        }
    }
}
