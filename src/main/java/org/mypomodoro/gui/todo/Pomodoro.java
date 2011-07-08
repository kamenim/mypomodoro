package org.mypomodoro.gui.todo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.mypomodoro.Main;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.mypomodoro.gui.ControlPanel;
import org.mypomodoro.model.Activity;

/**
 * This class keeps the logic for setting a timer for a pomodoro and the
 * breaks after that.
 * 
 * @author nikolavp
 * 
 */
public class Pomodoro {

    private final int SECOND = 1000;
    private final int MINUTES = 60 * SECOND;
    private final long POMODORO_LENGTH = ControlPanel.preferences.getPomodoroLength() * MINUTES;
    private final long POMODORO_BREAK_LENGTH = ControlPanel.preferences.getShortBreakLength() * MINUTES;
    private final long POMODORO_LONG_LENGTH = ControlPanel.preferences.getLongBreakLength() * MINUTES;
    private final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    private final Timer pomodoroTimer;
    private long pomodoroLength = POMODORO_LENGTH;
    private long shortBreakLength = POMODORO_BREAK_LENGTH;
    private long longBreakLength = POMODORO_LONG_LENGTH;
    private final JLabel pomodoroTime;
    private final ToDoListPanel panel;
    private TimerPanel timerPanel;
    private Activity currentToDo;
    private long time = pomodoroLength;
    private boolean inpomodoro = false;
    private Clip clip;

    public Pomodoro(ToDoListPanel panel) {
        pomodoroTime = panel.getPomodoroTime();
        pomodoroTime.setText(sdf.format(pomodoroLength));
        pomodoroTimer = new Timer(SECOND, new UpdateAction());
        this.panel = panel;
    }

    public boolean stopWithWarning() {
        if (inpomodoro && currentToDo != null) { // in pomodoro only, not during breaks
            JFrame window = new JFrame();
            String title = "Void pomodoro";
            String message = "Are you sure to void this pomodoro?";
            message += "\n(please create an unplanned activity in order to record this interruption)";
            int reply = JOptionPane.showConfirmDialog(window, message, title, JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                stop();
            }
        } else {
            stop();
        }
        return !inpomodoro;
    }

    public void stop() {
        pomodoroTimer.stop();
        time = pomodoroLength;
        pomodoroTime.setText(sdf.format(pomodoroLength));
        inpomodoro = false;
        stopSound();
    }

    class UpdateAction implements ActionListener {

        int pomSetNumber = 0;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (time >= 1) {
                time -= SECOND;
                pomodoroTime.setText(sdf.format(time));
            } else {
                stopSound();
                ring(); // riging at the end of pomodoros and breaks; no ticking during breaks
                if (inPomodoro()) {
                    // increment real poms
                    currentToDo.incrementPoms();
                    // refresh icon label for the current ToDo                    
                    ToDoIconLabel.showIconLabel(panel.getIconLabel(), currentToDo);
                    Activity selectedToDo = (Activity) panel.getToDoJList().getSelectedValue();
                    if (currentToDo.equals(selectedToDo)) {
                        ToDoIconLabel.showIconLabel(panel.getInformationPanel().getIconLabel(), currentToDo);
                        ToDoIconLabel.showIconLabel(panel.getCommentPanel().getIconLabel(), currentToDo);
                        ToDoIconLabel.showIconLabel(panel.getOverestimationPanel().getIconLabel(), currentToDo);
                    }
                    pomSetNumber++;
                    if (pomSetNumber == ControlPanel.preferences.getNbPomPerSet()) {
                        goInLongBreak();
                        pomSetNumber = 0;
                    } else {
                        goInShortBreak();
                    }
                    inpomodoro = false;
                } else {
                    if (isCurrentToDoComplete()) { // end of the break and user has not selected another ToDo (while all the pomodoros of the current one are done)
                        stop();
                        timerPanel.setStart();
                    } else {
                        tick();
                        inpomodoro = true;
                        goInPomodoro();
                    }
                }
            }
        }

        private void goInPomodoro() {
            time = pomodoroLength;
        }

        private void goInShortBreak() {
            //breakAction(shortBreakLength);
            time = shortBreakLength;
        }

        private void goInLongBreak() {
            //breakAction(longBreakLength);
            time = longBreakLength;
        }

        /*void breakAction(final long breakLength) {
        SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
        System.out.println("running");
        GraphicsDevice defaultScreenDevice = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice();
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setBackground(Color.BLACK);
        window.setUndecorated(true);
        if (defaultScreenDevice.isFullScreenSupported()) {
        try {
        defaultScreenDevice.setFullScreenWindow(window);
        JOptionPane.showMessageDialog(window,
        "Time to rest for " + breakLength
        + "minutes");
        } finally {
        defaultScreenDevice.setFullScreenWindow(null);
        }
        }
        window.setVisible(false);
        }
        });
        }*/
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

    public void start() {
        currentToDo = (Activity) panel.getToDoJList().getSelectedValue();
        pomodoroTimer.start();
        inpomodoro = true;
        tick();
    }

    public void tick() {
        if (ControlPanel.preferences.getTicking()) {
            InputStream is = Main.class.getResourceAsStream("/sounds/ticking.wav");
            playSound(is, true);
        }
    }

    public void ring() {
        if (ControlPanel.preferences.getRinging()) {
            InputStream is = Main.class.getResourceAsStream("/sounds/ringing.wav");
            playSound(is);
        }
    }

    public void playSound(InputStream is) {
        playSound(is, false);
    }

    public void playSound(InputStream is, boolean continuously) {
        try {
            AudioInputStream ain = AudioSystem.getAudioInputStream(is);
            try {
                DataLine.Info info = new DataLine.Info(Clip.class, ain.getFormat());
                clip = (Clip) AudioSystem.getLine(info);
                clip.open(ain);
                clip.loop(continuously ? clip.LOOP_CONTINUOUSLY : 0);
                clip.start();
            }
            finally {
                ain.close();
            }
        }
        catch (Exception e) {
            // no sound
        }
    }

    public void stopSound() {
        if (clip != null) {
            clip.stop();
        }
    }

    public void setCurrentToDo(Activity toDo) {
        currentToDo = toDo;
    }

    public Activity getCurrentToDo() {
        return currentToDo;
    }

    public void setTimerPanel(TimerPanel timerPanel) {
        this.timerPanel = timerPanel;
    }
    
    public TimerPanel getTimerPanel() {
        return timerPanel;
    }

    public boolean isCurrentToDoComplete() {
        return currentToDo.getActualPoms() == currentToDo.getEstimatedPoms() + currentToDo.getOverestimatedPoms();
    }
}