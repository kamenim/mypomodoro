package org.mypomodoro.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.mypomodoro.gui.todo.Pomodoro;

public class ControlPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final TimerValueSlider pomodoroSlider;
	private final TimerValueSlider shortBreakSlider;
	private final TimerValueSlider longBreakSlider;

	public ControlPanel(final MyPomodoroView view, final Pomodoro pomodoro) {
		int pomtime = (int) pomodoro.getPomodoroLength() / 60000;
		int shorttime = (int) pomodoro.getShortBreakLength() / 60000;
		int longtime = (int) pomodoro.getLongBreakLength() / 60000;
		pomodoroSlider = new TimerValueSlider(0, 45, pomtime,
				"Pomodoro Length: ");
		shortBreakSlider = new TimerValueSlider(0, 10, shorttime,
				"Short Break Length: ");
		longBreakSlider = new TimerValueSlider(0, 120, longtime,
				"Long Break Length: ");
		setBackground(Color.white);
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JButton setValue = new JButton("Save Settings");
		setValue.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				pomodoro
						.setPomodoroLength(pomodoroSlider.getSliderValue() * 60000);
				pomodoro.setLongBreakLength(longBreakSlider
						.getSliderValue() * 60000);
				pomodoro.setShortBreakLength(shortBreakSlider
						.getSliderValue() * 60000);
				view.setWindow(view.getIconBar().getSelectedIcon().getPanel());
			}
		});
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = .5;
		c.fill = GridBagConstraints.BOTH;
		add(pomodoroSlider, c);
		c.gridy = 1;
		add(shortBreakSlider, c);
		c.gridy = 2;
		add(longBreakSlider, c);
		c.gridy = 3;
		c.fill = GridBagConstraints.NONE;
		add(setValue, c);
	}

}