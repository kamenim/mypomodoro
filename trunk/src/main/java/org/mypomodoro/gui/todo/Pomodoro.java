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

import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.mypomodoro.Main;
import org.mypomodoro.gui.ActivityInformation;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.ImageIcons;
import org.mypomodoro.gui.MainPanel;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ToDoList;
import org.mypomodoro.util.ColorUtil;
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
    private final long POMODORO_LENGTH = PreferencesPanel.preferences.getPomodoroLength() * MINUTE;
    private final long POMODORO_BREAK_LENGTH = PreferencesPanel.preferences.getShortBreakLength() * MINUTE;
    private final long POMODORO_LONG_LENGTH = PreferencesPanel.preferences.getLongBreakLength() * MINUTE;
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
    private final ActivityInformation detailsPanel;
    private TimerPanel timerPanel;
    private int currentToDoId = -1;
    private long time = pomodoroLength;
    private boolean inpomodoro = false;
    private Clip clip;
    private boolean isMute = false;
    private final ImageIcon refreshIcon = new ImageIcon(Main.class.getResource("/images/refresh.png"));

    public Pomodoro(ToDoPanel panel, ActivityInformation detailsPanel) {
        this.panel = panel;
        this.detailsPanel = detailsPanel;

        pomodoroTime = panel.getPomodoroTime();
        pomodoroTime.setText(sdf.format(pomodoroLength));
        pomodoroTimer = new Timer(SECOND, new UpdateAction());
    }

    public void start() {
        pomodoroTimer.start();
        if (PreferencesPanel.preferences.getTicking() && !isMute) {
            tick();
        }
        if (isSystemTray()) {
            if (isSystemTrayMessage()) {
                MainPanel.trayIcon.displayMessage("", Labels.getString("ToDoListPanel.Started"), TrayIcon.MessageType.NONE);
            }
            MainPanel.trayIcon.setToolTip(Labels.getString("ToDoListPanel.Started"));
        }
        inpomodoro = true;
        panel.setIconLabels();
        panel.getTable().repaint(); // trigger row renderers      
    }

    public void stop() {
        pomodoroTimer.stop();
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
        panel.setIconLabels();
        panel.getTable().repaint(); // trigger row renderers
    }

    public boolean stopWithWarning() {
        if (inpomodoro) { // in pomodoro only, not during breaks            
            String title = Labels.getString("ToDoListPanel.Void pomodoro");
            String message = Labels.getString("ToDoListPanel.Are you sure to void this pomodoro?");
            message += "\n(" + Labels.getString("ToDoListPanel.please create an unplanned activity in order to record this interruption") + ")";
            int reply = JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (reply == JOptionPane.YES_OPTION) {
                stop();
            }
        } else { // breaks
            stop();
        }
        return !inpomodoro;
    }

    class UpdateAction implements ActionListener {

        int pomSetNumber = 0;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (time >= 1) {
                time -= SECOND;
                refreshTime();
                popupTime();
            } else {
                stopSound();
                if (PreferencesPanel.preferences.getRinging() && !isMute) {
                    ring(); // riging at the end of pomodoros and breaks; no ticking during breaks
                }
                if (inPomodoro()) {
                    // increment real poms
                    getCurrentToDo().incrementPoms();
                    getCurrentToDo().databaseUpdate();
                    pomSetNumber++;
                    // break time
                    if (pomSetNumber == PreferencesPanel.preferences.getNbPomPerSet()) {
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
                    timerPanel.setStartColor(ColorUtil.BLACK);
                    inpomodoro = false;
                    // update details panel
                    detailsPanel.selectInfo(getCurrentToDo());
                    detailsPanel.showInfo();
                    panel.setIconLabels();
                    panel.setPanelRemaining();
                    panel.setPanelBorder();
                    panel.getTable().repaint(); // trigger row renderers
                } else {
                    if (panel.getTable().getSelectedRowCount() == 1) { // this addresses the case when a task is selected during the pomodoro of another task
                        int row = panel.getTable().getSelectedRow();
                        currentToDoId = (Integer) panel.getTable().getModel().getValueAt(panel.getTable().convertRowIndexToModel(row), panel.getIdKey());
                    }
                    // Retrieve activity from the database in case it's changed (concurrent work : another use may have worked on it)                                       
                    if (getCurrentToDo().hasChanged()) {
                        stop();
                        timerPanel.setStart();
                        String title = Labels.getString("ToDoListPanel.ToDo changed");
                        String message = Labels.getString("ToDoListPanel.The ToDo has changed");
                        JOptionPane.showConfirmDialog(Main.gui, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, refreshIcon);
                    } else {
                        if (getCurrentToDo().isFinished()) { // end of the break and user has not selected another ToDo (while all the pomodoros of the current one are done)
                            stop();
                            timerPanel.setStart();
                            if (isSystemTray()) {
                                if (isSystemTrayMessage()) {
                                    MainPanel.trayIcon.displayMessage("", Labels.getString("ToDoListPanel.Finished"), TrayIcon.MessageType.NONE);
                                }
                                MainPanel.trayIcon.setToolTip(Labels.getString("ToDoListPanel.Finished"));
                            }
                        } else {
                            if (PreferencesPanel.preferences.getTicking() && !isMute) {
                                tick();
                            }
                            timerPanel.setStartColor(ColorUtil.RED);
                            inpomodoro = true;
                            if (isSystemTray()) {
                                if (isSystemTrayMessage()) {
                                    MainPanel.trayIcon.displayMessage("", Labels.getString("ToDoListPanel.Started"), TrayIcon.MessageType.NONE);
                                }
                                MainPanel.trayIcon.setToolTip(Labels.getString("ToDoListPanel.Started"));
                            }
                            goInPomodoro();
                            panel.setIconLabels();
                            panel.getTable().repaint(); // trigger row renderers
                        }
                    }
                }
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

    public void tick() {
        playSound(Main.class.getResourceAsStream("/sounds/ticking.wav"), true);
    }

    public void ring() {
        playSound(Main.class.getResourceAsStream("/sounds/ringing.wav"));
    }

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
        return SystemTray.isSupported() && PreferencesPanel.preferences.getSystemTray();
    }

    private boolean isSystemTrayMessage() {
        return SystemTray.isSupported() && PreferencesPanel.preferences.getSystemTrayMessage();
    }

    public void mute() {
        if (inpomodoro) { // mute ticking
            stopSound();
        }
        isMute = true;
    }

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
}
