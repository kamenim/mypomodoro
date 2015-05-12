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

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.mypomodoro.Main;
import org.mypomodoro.gui.IActivityInformation;
import org.mypomodoro.gui.ImageIcons;
import org.mypomodoro.gui.MainPanel;
import static org.mypomodoro.gui.todo.TimerPanel.strictPomodoro;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * This class keeps the logic for setting a timer for a pomodoro and the breaks
 * after that.
 *
 *
 */
public class Pomodoro {

    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private final int SECOND = 1000;
    private final int MINUTE = 60 * SECOND;
    private final long POMODORO_LENGTH = Main.preferences.getPomodoroLength() * MINUTE;
    private final long POMODORO_BREAK_LENGTH = Main.preferences.getShortBreakLength() * MINUTE;
    private final long POMODORO_LONG_LENGTH = Main.preferences.getLongBreakLength() * MINUTE;
    /*Test
     private final long POMODORO_LENGTH = 10 * SECOND;
     private final long POMODORO_BREAK_LENGTH = 10 * SECOND;
     private final long POMODORO_LONG_LENGTH = 10 * SECOND;*/
    private final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    private final Timer pomodoroTimer;
    private long pomodoroLength = POMODORO_LENGTH;
    private long tmpPomodoroLength = POMODORO_LENGTH;
    private long shortBreakLength = POMODORO_BREAK_LENGTH;
    private long longBreakLength = POMODORO_LONG_LENGTH;
    private final JLabel pomodoroTime;
    private final ToDoPanel panel;
    private final IActivityInformation detailsPanel;
    private final UnplannedPanel unplannedPanel;
    private TimerPanel timerPanel;
    private int currentToDoId = -1;
    private long time = pomodoroLength;
    private boolean inpomodoro = false;
    private Clip clip;
    private boolean isMute = false;
    private boolean isDiscontinuous = false;
    private int pomSetNumber = 0;

    public Pomodoro(ToDoPanel panel, IActivityInformation detailsPanel, UnplannedPanel unplannedPanel, JLabel pomodoroTime) {
        this.panel = panel;
        this.detailsPanel = detailsPanel;
        this.unplannedPanel = unplannedPanel;
        this.pomodoroTime = pomodoroTime;

        pomodoroTime.setText(sdf.format(pomodoroLength));
        pomodoroTimer = new Timer(SECOND, new UpdateAction());
    }

    public void start() {
        if (getCurrentToDo().isSubTask()
                || !ToDoList.hasSubTasks(getCurrentToDo().getId())) {
            // the user may want to star a new Set (eg : stopping the timer during a short break (or voiding a pomodoro) before lunch time and then starting a pomodoro after)
            if (!strictPomodoro
                    && pomSetNumber > 0) {
                String title = Labels.getString("ToDoListPanel.New Set");
                int pomSetNumberRemaining = Main.preferences.getNbPomPerSet() - pomSetNumber;
                int shortBreakSetNumberRemaining = pomSetNumberRemaining - 1;
                Date dateLongBreakStart = DateUtil.addMinutesToNow(pomSetNumberRemaining * Main.preferences.getPomodoroLength() + shortBreakSetNumberRemaining * Main.preferences.getShortBreakLength());
                String message = Labels.getString("ToDoListPanel.pomodoros to finish the current Set", pomSetNumberRemaining, DateUtil.getFormatedTime(dateLongBreakStart));
                int pomNewSetNumberRemaining = Main.preferences.getNbPomPerSet();
                int newSetShortBreaksNumber = pomNewSetNumberRemaining - 1;
                Date dateNewSetLongBreakStart = DateUtil.addMinutesToNow(pomNewSetNumberRemaining * Main.preferences.getPomodoroLength() + newSetShortBreaksNumber * Main.preferences.getShortBreakLength());
                message += System.getProperty("line.separator");
                message += Labels.getString("ToDoListPanel.Would you rather start a new Set", Main.preferences.getNbPomPerSet(), DateUtil.getFormatedTime(dateNewSetLongBreakStart));
                int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ImageIcons.DIALOG_ICON);
                if (reply == JOptionPane.YES_OPTION) {
                    pomSetNumber = 0;
                }
            }
            // Start timer
            pomodoroTimer.start();
            // Tooltip                        
            // If the name is modified during the pomodoro, it won't be updated, which is acceptable
            setTooltipOnImage();
            if (Main.preferences.getTicking() && !isMute) {
                tick();
            }
            if (isSystemTray()) {
                if (isSystemTrayMessage()) {
                    MainPanel.trayIcon.displayMessage("", Labels.getString("ToDoListPanel.Started"), TrayIcon.MessageType.NONE);
                }
                MainPanel.trayIcon.setToolTip(Labels.getString("ToDoListPanel.Started"));
            }
            inpomodoro = true;
            Main.gui.getIconBar().getIcon(2).setForeground(Main.taskRunningColor);
            Main.gui.getIconBar().getIcon(2).highlight();
            panel.getCurrentTable().setIconLabels();
            // Show quick interruption button and items in combo box
            ((UnplannedActivityInputForm) unplannedPanel.getFormPanel()).showInterruptionComboBox();
            refreshTitlesAndTables();
        } else {
            // no start // ToDo message: you must start a subtask ?
        }
    }

    public void stop() {
        pomodoroTimer.stop();
        // Remove tooltip 
        timerPanel.setToolTipText(null);
        time = pomodoroLength;
        tmpPomodoroLength = pomodoroLength;
        pomodoroTime.setText(sdf.format(pomodoroLength));
        stopSound();
        if (inPomodoro() && isSystemTray()) {
            if (isSystemTrayMessage()) {
                MainPanel.trayIcon.displayMessage("", Labels.getString("ToDoListPanel.Stopped"), TrayIcon.MessageType.NONE);
            }
            MainPanel.trayIcon.setToolTip(Labels.getString("ToDoListPanel.Stopped"));
            MainPanel.trayIcon.setImage(ImageIcons.MAIN_ICON.getImage());
        }
        inpomodoro = false;
        Main.gui.getIconBar().getIcon(2).setForeground(new JLabel().getForeground()); // use of getForeground is important to keep the default color of the theme (especially with JTatto Moire theme)
        Main.gui.getIconBar().getIcon(2).highlight();
        panel.getCurrentTable().setIconLabels();
        // Hide quick interruption button and items in combo box
        ((UnplannedActivityInputForm) unplannedPanel.getFormPanel()).hideInterruptionComboBox();
        refreshTitlesAndTables();
    }

    public void pause() {
        pomodoroTimer.stop();
        // Tooltip : name only
        timerPanel.setToolTipText(getCurrentToDo().getName());
        stopSound();
        if (inPomodoro() && isSystemTray()) {
            if (isSystemTrayMessage()) {
                MainPanel.trayIcon.displayMessage("", Labels.getString("ToDoListPanel.Paused"), TrayIcon.MessageType.NONE);
            }
            MainPanel.trayIcon.setToolTip(Labels.getString("ToDoListPanel.Paused"));
            MainPanel.trayIcon.setImage(ImageIcons.MAIN_ICON.getImage());
        }
        // Hide quick interruption button and items in combo box
        ((UnplannedActivityInputForm) unplannedPanel.getFormPanel()).hideInterruptionComboBox();
        refreshTitlesAndTables();
    }

    public void resume() {
        if (getCurrentToDo().isSubTask()
                || !ToDoList.hasSubTasks(getCurrentToDo().getId())) {
            pomodoroTimer.start();
            // Tooltip                        
            // If the name is modified during the pomodoro, it won't be updated, which is acceptable
            setTooltipOnImage();
            if (inPomodoro() && Main.preferences.getTicking() && !isMute) {
                tick();
            }
            if (inPomodoro() && isSystemTray()) {
                if (isSystemTrayMessage()) {
                    MainPanel.trayIcon.displayMessage("", Labels.getString("ToDoListPanel.Resumed"), TrayIcon.MessageType.NONE);
                }
                MainPanel.trayIcon.setToolTip(Labels.getString("ToDoListPanel.Resumed"));
                MainPanel.trayIcon.setImage(ImageIcons.MAIN_ICON.getImage());
            }
            // Show quick interruption button and items in combo box
            if (inPomodoro()) {
                ((UnplannedActivityInputForm) unplannedPanel.getFormPanel()).showInterruptionComboBox();
                refreshTitlesAndTables();
            }
        } else {
            // no start // ToDo message: you must start a subtask ?
        }
    }

    public boolean stopWithWarning() {
        if (inpomodoro) { // in pomodoro only, not during breaks            
            String title = Labels.getString("ToDoListPanel.Void pomodoro");
            String message = Labels.getString("ToDoListPanel.Are you sure to void this pomodoro?");
            int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, ImageIcons.DIALOG_ICON);
            if (reply == JOptionPane.YES_OPTION) {
                stop();
            }
        } else { // breaks
            stop();
        }
        return !inpomodoro;
    }

    class UpdateAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (time >= 1) {
                time -= SECOND;
                refreshTime();
                popupTime();
            } else {
                stopSound();
                if (Main.preferences.getRinging() && !isMute) {
                    ring(); // riging at the end of pomodoros and breaks; no ticking during breaks
                }
                if (inPomodoro()) { // break time
                    // update the current ToDo from the database (in case someone's changed it)
                    ToDoList.getList().refreshById(currentToDoId);
                    // updated version of the task is already finished (by someone else)
                    // increase the overestimation of the task by 1 to record the pomodoro
                    if (getCurrentToDo().isFinished()) {
                        getCurrentToDo().setOverestimatedPoms(getCurrentToDo().getOverestimatedPoms() + 1);
                        if (getCurrentToDo().isSubTask()) {
                            panel.getMainTable().addPomsToSelectedRow(0, 0, 1);
                        }
                    } else if (getCurrentToDo().getEstimatedPoms() + getCurrentToDo().getOverestimatedPoms() == 0) { // task with no estimation
                        getCurrentToDo().setEstimatedPoms(1);
                        if (getCurrentToDo().isSubTask()) {
                            panel.getMainTable().addPomsToSelectedRow(0, 1, 0);
                        }
                    }
                    getCurrentToDo().incrementPoms();
                    getCurrentToDo().databaseUpdate();
                    if (getCurrentToDo().isSubTask()) {
                        panel.getMainTable().addPomsToSelectedRow(1, 0, 0);
                    }
                    if (isDiscontinuous) { // stop timer
                        pomSetNumber = 0; // reset Set to 0 (in case the workflow is discontinued when a Set is already started: pomSetNumber > 0)
                        stop();
                        timerPanel.setStartEnv();
                        if (isSystemTray()) {
                            String message = Labels.getString("ToDoListPanel.Stopped");
                            if (isSystemTrayMessage()) {
                                MainPanel.trayIcon.displayMessage("", message, TrayIcon.MessageType.NONE);
                            }
                            MainPanel.trayIcon.setToolTip(message);
                        }
                        timerPanel.setStartEnv();
                    } else { // break time
                        pomSetNumber++;
                        if (pomSetNumber == Main.preferences.getNbPomPerSet()) {
                            goInLongBreak();
                            pomSetNumber = 0;
                            if (isSystemTray()) {
                                if (isSystemTrayMessage()) {
                                    MainPanel.trayIcon.displayMessage("", Labels.getString("ToDoListPanel.Long break"), TrayIcon.MessageType.NONE);
                                }
                                MainPanel.trayIcon.setToolTip(Labels.getString("ToDoListPanel.Long break"));
                            }
                        } else {
                            goInShortBreak();
                            if (isSystemTray()) {
                                if (isSystemTrayMessage()) {
                                    MainPanel.trayIcon.displayMessage("", Labels.getString("ToDoListPanel.Short break"), TrayIcon.MessageType.NONE);
                                }
                                MainPanel.trayIcon.setToolTip(Labels.getString("ToDoListPanel.Short break"));
                            }
                        }
                        timerPanel.setBreakEnv();
                    }
                    timerPanel.setToolTipText(null);
                    inpomodoro = false;
                    Main.gui.getIconBar().getIcon(2).setForeground(ColorUtil.BLACK);
                    Main.gui.getIconBar().getIcon(2).highlight();
                    // Hide quick interruption button and items in combo box 
                    ((UnplannedActivityInputForm) unplannedPanel.getFormPanel()).hideInterruptionComboBox();
                    refreshTitlesAndTables();
                } else { // pomodoro time
                    if (panel.getCurrentTable().getSelectedRowCount() == 1) { // this addresses the case when a task is selected during the pomodoro of another task                        
                        currentToDoId = panel.getCurrentTable().getActivityIdFromSelectedRow();
                    }
                    // update the current ToDo from the database (in case someone's changed it)
                    ToDoList.getList().refreshById(currentToDoId);
                    // end of the break and user has not selected another ToDo (while all the pomodoros of the current one are done)                    
                    if (getCurrentToDo().isFinished()) {
                        stop();
                        timerPanel.setStartEnv();
                        if (isSystemTray()) {
                            String message = Labels.getString("ToDoListPanel.Finished");
                            if (isSystemTrayMessage()) {
                                MainPanel.trayIcon.displayMessage("", message, TrayIcon.MessageType.NONE);
                            }
                            MainPanel.trayIcon.setToolTip(message);
                        }
                        timerPanel.setToolTipText(null);
                    } else {
                        if (getCurrentToDo().isSubTask()
                                || !ToDoList.hasSubTasks(getCurrentToDo().getId())) {
                            if (Main.preferences.getTicking() && !isMute) {
                                tick();
                            }
                            timerPanel.setPomodoroEnv();
                            inpomodoro = true;
                            Main.gui.getIconBar().getIcon(2).setForeground(Main.taskRunningColor);
                            Main.gui.getIconBar().getIcon(2).highlight();
                            if (isSystemTray()) {
                                if (isSystemTrayMessage()) {
                                    MainPanel.trayIcon.displayMessage("", Labels.getString("ToDoListPanel.Started"), TrayIcon.MessageType.NONE);
                                }
                                MainPanel.trayIcon.setToolTip(Labels.getString("ToDoListPanel.Started"));
                            }
                            goInPomodoro();
                            // Tooltip                        
                            // If the name is modified during the pomodoro, it won't be updated, which is acceptable
                            setTooltipOnImage();
                            // Show quick interruption button and items in combo box 
                            ((UnplannedActivityInputForm) unplannedPanel.getFormPanel()).showInterruptionComboBox();
                            refreshTitlesAndTables();
                        } else {
                            // no start // ToDo message: you must start a subtask ?
                        }
                    }
                }
                // Put app back in front (system tray, minimized, in the background)
                if (Main.preferences.getBringToFront()) {
                    // There is no guarantee the following will work on Linux (see http://stackoverflow.com/questions/309023/how-to-bring-a-window-to-the-front)
                    Main.gui.setVisible(true);
                    Main.gui.toFront();
                    Main.gui.setExtendedState(JFrame.NORMAL); // Note: full screen shrinks to preferred size (see Main) which is ok
                }
                // update details panel
                detailsPanel.selectInfo(getCurrentToDo());
                detailsPanel.showInfo();
                panel.getCurrentTable().setIconLabels();
                //panel.setPanelRemaining();
                refreshTitlesAndTables();
            }
        }

        private void goInPomodoro() {
            time = pomodoroLength;
            tmpPomodoroLength = pomodoroLength;
        }

        private void goInShortBreak() {
            time = shortBreakLength;
        }

        private void goInLongBreak() {
            time = longBreakLength;
        }
    }

    // multi-lines tooltip
    private void setTooltipOnImage() {
        String tooltip = "<html>";
        tooltip += getCurrentToDo().isSubTask() ? Labels.getString("Common.Subtask") : Labels.getString("Common.Task");
        tooltip += ": ";
        tooltip += "<br>";
        tooltip += getCurrentToDo().getName();
        tooltip += "<br>";
        Date dateShortBreakStart = DateUtil.addMinutesToNow(Main.preferences.getPomodoroLength());
        tooltip += Labels.getString("ToDoListPanel.Next break at", DateUtil.getFormatedTime(dateShortBreakStart));
        tooltip += "<br>";
        int pomSetNumberRemaining = Main.preferences.getNbPomPerSet() - pomSetNumber;
        int shortBreakSetNumberRemaining = pomSetNumberRemaining - 1;
        Date dateLongBreakStart = DateUtil.addMinutesToNow(pomSetNumberRemaining * Main.preferences.getPomodoroLength() + shortBreakSetNumberRemaining * Main.preferences.getShortBreakLength());
        tooltip += Labels.getString("ToDoListPanel.Long break at", DateUtil.getFormatedTime(dateLongBreakStart));
        tooltip += "</html>";
        timerPanel.setToolTipText(tooltip);
    }

    public void setLongBreak(long longBreakLength) {
        this.longBreakLength = longBreakLength;
    }

    public void setShortBreak(long shortBreak) {
        shortBreakLength = shortBreak;
    }

    public long getPomodoroLength() {
        return pomodoroLength;
    }

    public void setPomodoroLength(long pomodoroLength) {
        this.pomodoroLength = pomodoroLength;
    }

    public long getShortBreakLength() {
        return shortBreakLength;
    }

    public void setShortBreakLength(long shortBreakLength) {
        this.shortBreakLength = shortBreakLength;
    }

    public long getLongBreakLength() {
        return longBreakLength;
    }

    public void setLongBreakLength(long longBreakLength) {
        this.longBreakLength = longBreakLength;
    }

    public boolean inPomodoro() {
        return inpomodoro;
    }

    // Looping ticking sound
    public void tick() {
        InputStream is;
        try {
            is = new FileInputStream("./ticking.wav");
            playSound(is, true);
        } catch (FileNotFoundException ex) {
            is = Main.class.getResourceAsStream("/sounds/ticking.wav");
            playSound(is, true);
        }
    }

    // One time ringing sound
    public void ring() {
        InputStream is;
        try {
            is = new FileInputStream("./ringing.wav");
            playSound(is);
        } catch (FileNotFoundException ex) {
            is = Main.class.getResourceAsStream("/sounds/ringing.wav");
            playSound(is);
        }
    }

    // One time playing sound
    public void playSound(InputStream is) {
        playSound(is, false);
    }

    public void playSound(InputStream is, boolean continuously) {
        try {
            AudioInputStream ain = AudioSystem.getAudioInputStream(getStreamWithMarkReset(is));
            try {
                DataLine.Info info = new DataLine.Info(Clip.class, ain.getFormat());
                clip = (Clip) AudioSystem.getLine(info);
                clip.addLineListener(new LineListener() {

                    @Override
                    public void update(LineEvent event) {
                        // flush the line buffer and close the line at the end of media or on explicit stop
                        DataLine line = (DataLine) event.getSource();
                        if (event.getType() == LineEvent.Type.STOP) {
                            line.flush();
                            line.close();
                        }
                    }
                });
                clip.open(ain);
                clip.loop(continuously ? Clip.LOOP_CONTINUOUSLY : 0);
                clip.start();
            } finally {
                ain.close();
            }
        } catch (IOException ex) {
            // no sound
            logger.error("", ex);
        } catch (UnsupportedAudioFileException ex) {
            // no sound
            logger.error("", ex);
        } catch (LineUnavailableException ex) {
            // no sound
            logger.error("", ex);
        }
    }

    public void stopSound() {
        if (clip != null) {
            clip.stop();
            // allow clip to be GCed
            clip = null;
        }
    }

    public void setCurrentToDoId(int id) {
        currentToDoId = id;
    }

    public Activity getCurrentToDo() {
        return ToDoList.getList().getById(currentToDoId);
    }

    public void setTimerPanel(TimerPanel timerPanel) {
        this.timerPanel = timerPanel;
    }

    public TimerPanel getTimerPanel() {
        return timerPanel;
    }

    public Timer getTimer() {
        return pomodoroTimer;
    }

    private InputStream getStreamWithMarkReset(InputStream stream) throws IOException {
        if (stream.markSupported()) {
            return stream;
        }
        ByteArrayOutputStream output = null;
        try {
            output = new ByteArrayOutputStream(stream.available());
            byte[] buf = new byte[2048];
            int read;
            while ((read = stream.read(buf)) > 0) {
                output.write(buf, 0, read);
            }
            return new ByteArrayInputStream(output.toByteArray());
        } finally {
            try {
                stream.close();
            } catch (IOException ex) {
                logger.error("", ex);
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    logger.error("", ex);
                }
            }
        }
    }

    private boolean isSystemTray() {
        return SystemTray.isSupported() && Main.preferences.getSystemTray();
    }

    private boolean isSystemTrayMessage() {
        return SystemTray.isSupported() && Main.preferences.getSystemTrayMessage();
    }

    // mute ticking and ringing
    public void mute() {
        stopSound();
        isMute = true;
    }

    // Un-mute andplay ticking if necessary
    public void unmute() {
        if (inpomodoro) { // un-mute ticking
            tick();
        }
        isMute = false;
    }

    /*
     * increateTime
     * 
     * Increase time by one minute
     */
    public void increaseTime() {
        if (time < 59 * MINUTE) {
            time += MINUTE;
            tmpPomodoroLength += MINUTE;
            refreshTime();
        }
    }

    /*
     * decreateTime
     * 
     * Decrease time by one minute
     */
    public void decreaseTime() {
        if (time > MINUTE) {
            time -= MINUTE;
            tmpPomodoroLength -= MINUTE;
            refreshTime();
        }
    }

    private synchronized void refreshTime() {
        String now = sdf.format(time);
        pomodoroTime.setText(now);
        if (inPomodoro() && isSystemTray()) {
            MainPanel.trayIcon.setToolTip(now);
            int progressiveTrayIndex = (int) ((double) ((tmpPomodoroLength - time)) / (double) tmpPomodoroLength * 8);
            MainPanel.trayIcon.setImage(ImageIcons.MAIN_ICON_PROGRESSIVE[progressiveTrayIndex].getImage());
        }
    }

    // display popup message every 10 minutes at 05:00, 15:00, 25:00
    private void popupTime() {
        String now = sdf.format(time);
        int tenMinutes = 10 * MINUTE;
        int fiveMinutes = 5 * MINUTE;
        if (inPomodoro() && isSystemTray()
                && isSystemTrayMessage()) {
            for (int i = fiveMinutes; i < tmpPomodoroLength; i = i + tenMinutes) {
                if (time == i) {
                    MainPanel.trayIcon.displayMessage("", now, TrayIcon.MessageType.NONE);
                }
            }
        }
    }

    // set the pomodoros to stop after each break
    public void discontinueWorkflow() {
        isDiscontinuous = true;
    }

    // set the pomodoros and breaks to run continiously
    public void continueWorkflow() {
        isDiscontinuous = false;
    }

    public boolean isDiscontinuous() {
        return isDiscontinuous;
    }
    
    // Repaint title with proper buttons and trigger row renderers of the tables
    private void refreshTitlesAndTables() {
        panel.getCurrentTable().setTitle();
        panel.getCurrentTable().repaint();
        if (panel.getCurrentTable().equals(panel.getMainTable())) {
            panel.getSubTable().setTitle();
            panel.getSubTable().repaint();
        } else {
            panel.getMainTable().setTitle();
            panel.getMainTable().repaint();
        }
    }
}
