package org.mypomodoro.gui.todo;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.mypomodoro.model.ToDoList;

/**
 * This class keeps the logic for setting a timer for a pomodoro and and the
 * breaks after that.
 * 
 * @author nikolavp
 * 
 */
public class Pomodoro {

	private static final int SECOND = 1000;
	private static final int MINUTES = 60 * SECOND;
	private static final long POMODORO_LENGTH = 25 * MINUTES;
	private static final long POMODORO_BREAK_LENGTH = 5 * MINUTES;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");

	private final Timer pomodoroTimer;
	private long pomodoroLength = POMODORO_LENGTH;
	private long shortBreakLength = POMODORO_BREAK_LENGTH;
	private long longBreakLength = POMODORO_BREAK_LENGTH * 9;
	private final JLabel label;

	private long time = pomodoroLength;
	private boolean inpomodoro;

	public void stop() {
		pomodoroTimer.stop();
		time = pomodoroLength;
		label.setText(sdf.format(pomodoroLength));
		inpomodoro = false;
	}

	public Pomodoro(JLabel label) {
		this.label = label;
		label.setText(sdf.format(pomodoroLength));
		pomodoroTimer = new Timer(SECOND, new UpdateAction());
	}

	class UpdateAction implements ActionListener {

		int i = 0;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (time >= 1) {
				time -= SECOND;
				label.setText(sdf.format(time));
			} else {
				// TODO: Play a sound that is not a simple beep put something
				// that will get attention
				Toolkit.getDefaultToolkit().beep();
				if (inPomodoro()) {
					if (i > 3) {
						goInLongBreak();
						i = 0;
					} else {
						i++;
						goInShortBreak();
					}
					inpomodoro = false;
				} else {
					ToDoList.getList().currentActivity().incrementPoms();
					inpomodoro = true;
					goInPomodoro();
				}
			}
		}

		private void goInPomodoro() {
			time = pomodoroLength;
		}

		private void goInShortBreak() {
			breakAction(shortBreakLength);
			time = shortBreakLength;
		}

		private void goInLongBreak() {
			breakAction(longBreakLength);
			time = longBreakLength;
		}

		void breakAction(final long breakLength) {
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

	private boolean inPomodoro() {
		return inpomodoro;
	}

	public void start() {
		pomodoroTimer.start();
		inpomodoro = true;
	}
}
